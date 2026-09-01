package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.request.MovimientoDetalleRequest;
import com.Springboot_project.inventory_service.dto.request.MovimientoRequest;
import com.Springboot_project.inventory_service.dto.response.MovimientoResponse;
import com.Springboot_project.inventory_service.exception.BusinessRuleException;
import com.Springboot_project.inventory_service.mapper.MovimientoMapper;
import com.Springboot_project.inventory_service.model.*;
import com.Springboot_project.inventory_service.repository.*;
import com.Springboot_project.inventory_service.service.ContextService;
import com.Springboot_project.inventory_service.service.MovimientoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoDetalleRepository movimientoDetalleRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContextService contextService;
    private final MovimientoMapper movimientoMapper;

    @Override
    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest dto, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + usuarioId));

        Bodega bodegaOrigen = null;
        Bodega bodegaDestino = null;

        if (dto.getBodegaOrigenId() != null) {
            bodegaOrigen = bodegaRepository.findById(dto.getBodegaOrigenId())
                    .orElseThrow(() -> new EntityNotFoundException("Bodega origen no encontrada"));
        }
        if (dto.getBodegaDestinoId() != null) {
            bodegaDestino = bodegaRepository.findById(dto.getBodegaDestinoId())
                    .orElseThrow(() -> new EntityNotFoundException("Bodega destino no encontrada"));
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
        movimiento.setObservaciones(dto.getObservaciones());
        movimiento.setDetalles(new ArrayList<>());

        List<MovimientoDetalleRequest> detallesRequest = dto.getDetalles();
        for (int i = 0; i < detallesRequest.size(); i++) {
            MovimientoDetalleRequest detalleRequest = detallesRequest.get(i);

            Producto producto = productoRepository.findById(detalleRequest.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Producto no encontrado con id: " + detalleRequest.getProductoId()));

            aplicarMovimientoAlStock(producto, dto.getTipoMovimiento(), detalleRequest.getCantidad());
            productoRepository.save(producto);

            DetalleMovimiento detalle = new DetalleMovimiento();
            detalle.setMovimiento(movimiento);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            movimiento.getDetalles().add(detalle);
        }

        return movimientoMapper.entityToDto(movimientoRepository.save(movimiento));
    }

    @Override
    public List<MovimientoResponse> obtenerTodos() {
        return movimientoRepository.findAll()
                .stream()
                .map(movimientoMapper::entityToDto)
                .toList();
    }

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado con id: " + id));
        return movimientoMapper.entityToDto(movimiento);
    }

    @Override
    public List<MovimientoResponse> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(movimientoMapper::entityToDto)
                .toList();
    }

    @Override
    public Map<String, Object> obtenerResumenGeneral() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("stockPorBodega", calcularStockPorBodega());
        resumen.put("productosMasMovidos", obtenerProductosMasMovidos());
        return resumen;
    }

    private void aplicarMovimientoAlStock(Producto producto, TipoMovimiento tipo, Long cantidad) {
        if (tipo == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + cantidad);
            return;
        }

        if (producto.getStock() < cantidad) {
            throw new BusinessRuleException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        if (tipo == TipoMovimiento.SALIDA) {
            producto.setStock(producto.getStock() - cantidad);
        }
        // TRANSFERENCIA: no cambia el stock total, solo cambia de bodega.
    }

    private List<Map<String, Object>> calcularStockPorBodega() {
        Map<Long, Map<String, Object>> stockPorBodega = new HashMap<>();

        List<Bodega> bodegas = bodegaRepository.findAll();
        for (int i = 0; i < bodegas.size(); i++) {
            Bodega bodega = bodegas.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("bodegaId", bodega.getId());
            item.put("bodegaNombre", bodega.getNombre());
            item.put("stockTotal", 0L);
            stockPorBodega.put(bodega.getId(), item);
        }

        List<Object[]> entradas = movimientoDetalleRepository.sumarEntradasPorBodega();
        for (int i = 0; i < entradas.size(); i++) {
            Object[] fila = entradas.get(i);
            sumarAlStock(stockPorBodega, (Long) fila[0], (Long) fila[2]);
        }

        List<Object[]> salidas = movimientoDetalleRepository.sumarSalidasPorBodega();
        for (int i = 0; i < salidas.size(); i++) {
            Object[] fila = salidas.get(i);
            sumarAlStock(stockPorBodega, (Long) fila[0], -(Long) fila[2]);
        }

        return new ArrayList<>(stockPorBodega.values());
    }

    private void sumarAlStock(Map<Long, Map<String, Object>> stockPorBodega, Long bodegaId, Long delta) {
        Map<String, Object> item = stockPorBodega.get(bodegaId);
        if (item == null) {
            return;
        }
        long actual = (Long) item.get("stockTotal");
        item.put("stockTotal", actual + delta);
    }

    private List<Map<String, Object>> obtenerProductosMasMovidos() {
        List<Object[]> filas = movimientoDetalleRepository.productosMasMovidos();
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (int i = 0; i < filas.size(); i++) {
            Object[] fila = filas.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("productoId", fila[0]);
            item.put("productoNombre", fila[1]);
            item.put("totalMovido", fila[2]);
            respuesta.add(item);
        }
        return respuesta;
    }
}