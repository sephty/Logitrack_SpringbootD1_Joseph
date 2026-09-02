USE logitrack_db;

-- =========================================================
-- DATOS DE PRUEBA - LOGITRACK
-- Actualizado a 2026
-- =========================================================


-- =========================================================
-- 1. USUARIOS
-- =========================================================

-- =========================================================
-- 1. USUARIOS
-- Passwords hashed with BCrypt (cost 10)
--
-- admin.logitrack  -> admin123
-- carlos.rojas     -> carlos123
-- andrea.torres    -> andrea123
-- miguel.gomez     -> miguel123
-- sofia.mendoza    -> sofia123
-- daniel.castro    -> daniel123
-- valentina.ruiz   -> valentina123
-- juan.morales     -> juan123
-- =========================================================

INSERT INTO usuarios
(username, password, email, nombre_completo, rol, activo, fecha_creacion)
VALUES
(
    'admin.logitrack',
    '$2y$10$fdqqHz7lSVf3THk0B.VmbesBdHtRWlmIjavZux0BIep7YhfuZNw1u',
    'admin@logitrack.com',
    'Laura Martínez Gómez',
    'ADMIN',
    TRUE,
    '2026-01-10 08:15:00'
),
(
    'carlos.rojas',
    '$2y$10$S4XD3YQJOqx0pfbjuPJliOSUdUfi4aQ71y2sfVn62DmPTgdr.bebu',
    'carlos.rojas@logitrack.com',
    'Carlos Rojas Pérez',
    'EMPLEADO',
    TRUE,
    '2026-01-15 09:20:00'
),
(
    'andrea.torres',
    '$2y$10$/zFiG3ChXeC4EJ.zjhI2tuuD9i4Mnh/sRQpyMUSBxuiqmQ28wGyFu',
    'andrea.torres@logitrack.com',
    'Andrea Torres Rodríguez',
    'EMPLEADO',
    TRUE,
    '2026-01-20 08:45:00'
),
(
    'miguel.gomez',
    '$2y$10$io.yX1CgeDdpM7bNc/rn/OVWmf7BJzYDlvTJMRZ.RwxICMtIR3fc2',
    'miguel.gomez@logitrack.com',
    'Miguel Gómez Vargas',
    'EMPLEADO',
    TRUE,
    '2026-02-03 10:10:00'
),
(
    'sofia.mendoza',
    '$2y$10$7n5w4dWZsFLu/2lchb/FZu2nY2wANbQD0PO.vrayWPBN/h95bnk1G',
    'sofia.mendoza@logitrack.com',
    'Sofía Mendoza Ramírez',
    'EMPLEADO',
    TRUE,
    '2026-02-12 07:55:00'
),
(
    'daniel.castro',
    '$2y$10$NUjzyesR1Y7qk293HLqoeuO4p/iK1o5K8z8IdrhVNTbK1xeud0cN.',
    'daniel.castro@logitrack.com',
    'Daniel Castro León',
    'EMPLEADO',
    TRUE,
    '2026-03-01 09:00:00'
),
(
    'valentina.ruiz',
    '$2y$10$Zo37roWJlCZz86gtjURnJuoPoo8bC5LTb5pqwPZVWYeYxL5BjdYF6',
    'valentina.ruiz@logitrack.com',
    'Valentina Ruiz Herrera',
    'EMPLEADO',
    TRUE,
    '2026-03-15 11:30:00'
),
(
    'juan.morales',
    '$2y$10$bUmxPw6601S1sMrRZItxv.idLKD7TYzRb4JtEiP3XtKkwZMA9S4he',
    'juan.morales@logitrack.com',
    'Juan Morales Díaz',
    'EMPLEADO',
    FALSE,
    '2026-04-05 08:30:00'
);

-- =========================================================
-- 2. BODEGAS
-- =========================================================

INSERT INTO bodegas
(nombre, ubicacion, capacidad, encargado, activo, fecha_creacion)
VALUES
('Bodega Principal Bucaramanga',
 'Zona Industrial de Bucaramanga', 5000,
 'Carlos Rojas Pérez', TRUE, '2026-01-05 07:30:00'),

('Bodega Floridablanca',
 'Floridablanca, Santander', 3000,
 'Andrea Torres Rodríguez', TRUE, '2026-01-18 08:00:00'),

('Bodega Cabecera',
 'Cabecera del Llano, Bucaramanga', 1500,
 'Miguel Gómez Vargas', TRUE, '2026-02-10 09:15:00'),

('Centro de Distribución Girón',
 'Zona Industrial de Girón, Santander', 7500,
 'Sofía Mendoza Ramírez', TRUE, '2026-02-25 07:45:00'),

('Bodega Norte',
 'Bucaramanga, Santander', 2000,
 'Daniel Castro León', FALSE, '2026-03-20 10:00:00');


-- =========================================================
-- 3. PRODUCTOS
-- =========================================================

INSERT INTO productos
(nombre, categoria, stock, precio, fecha_creacion)
VALUES
('Laptop Lenovo IdeaPad 3',
 'Computadores', 35, 2499900.00, '2026-01-12 09:00:00'),

('Laptop HP 15',
 'Computadores', 28, 2199900.00, '2026-01-14 09:30:00'),

('Monitor LG 24 pulgadas',
 'Monitores', 42, 699900.00, '2026-01-18 10:15:00'),

('Monitor Samsung 27 pulgadas',
 'Monitores', 25, 1099900.00, '2026-01-22 08:45:00'),

('Teclado Logitech K380',
 'Periféricos', 65, 189900.00, '2026-02-02 11:00:00'),

('Mouse Logitech M185',
 'Periféricos', 90, 79900.00, '2026-02-04 09:20:00'),

('Mouse Logitech G203',
 'Periféricos', 37, 159900.00, '2026-02-08 14:00:00'),

('Teclado mecánico Redragon K552',
 'Periféricos', 31, 249900.00, '2026-02-12 10:30:00'),

('SSD Kingston 1TB NVMe',
 'Almacenamiento', 22, 329900.00, '2026-02-15 08:20:00'),

('SSD Samsung 990 EVO 1TB',
 'Almacenamiento', 18, 449900.00, '2026-02-20 09:40:00'),

('Disco duro Seagate 2TB',
 'Almacenamiento', 14, 299900.00, '2026-02-22 13:15:00'),

('Memoria RAM DDR5 16GB',
 'Componentes', 45, 279900.00, '2026-02-26 10:00:00'),

('Memoria RAM DDR5 32GB',
 'Componentes', 19, 499900.00, '2026-03-01 08:30:00'),

('Tarjeta gráfica RTX 4060',
 'Componentes', 12, 1599900.00, '2026-03-05 11:45:00'),

('Fuente Corsair 650W',
 'Componentes', 24, 349900.00, '2026-03-08 09:10:00'),

('Router TP-Link Archer AX55',
 'Redes', 17, 399900.00, '2026-03-12 10:20:00'),

('Switch TP-Link 8 puertos',
 'Redes', 29, 179900.00, '2026-03-15 12:00:00'),

('Cable HDMI 2.1 2 metros',
 'Accesorios', 85, 59900.00, '2026-03-18 15:30:00'),

('Webcam Logitech C920',
 'Accesorios', 16, 329900.00, '2026-03-22 09:45:00'),

('Audífonos Logitech G435',
 'Audio', 27, 299900.00, '2026-03-25 14:20:00');


-- =========================================================
-- 4. MOVIMIENTOS
-- =========================================================
-- Bodegas:
-- 1 = Principal Bucaramanga
-- 2 = Floridablanca
-- 3 = Cabecera
-- 4 = Girón
-- 5 = Norte


-- ENTRADA 1
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-04-02 08:30:00', 'ENTRADA', 2, NULL, 1,
 'Recepción de mercancía de proveedor tecnológico');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 1, 20),
(LAST_INSERT_ID(), 3, 15),
(LAST_INSERT_ID(), 5, 30);


-- ENTRADA 2
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-04-08 10:15:00', 'ENTRADA', 3, NULL, 4,
 'Ingreso de equipos para distribución regional');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 2, 15),
(LAST_INSERT_ID(), 4, 10),
(LAST_INSERT_ID(), 14, 8);


-- ENTRADA 3
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-04-15 09:00:00', 'ENTRADA', 4, NULL, 2,
 'Reposición de periféricos');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 6, 40),
(LAST_INSERT_ID(), 7, 20),
(LAST_INSERT_ID(), 8, 15);


-- TRANSFERENCIA 1
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-04-20 14:30:00', 'TRANSFERENCIA', 5, 1, 2,
 'Traslado de equipos a bodega Floridablanca');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 1, 8),
(LAST_INSERT_ID(), 3, 10),
(LAST_INSERT_ID(), 5, 15);


-- TRANSFERENCIA 2
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-05-03 08:45:00', 'TRANSFERENCIA', 6, 4, 1,
 'Redistribución de inventario');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 2, 5),
(LAST_INSERT_ID(), 14, 3),
(LAST_INSERT_ID(), 16, 4);


-- SALIDA 1
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-05-10 11:20:00', 'SALIDA', 2, 1, NULL,
 'Despacho de pedido empresarial');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 1, 5),
(LAST_INSERT_ID(), 3, 5),
(LAST_INSERT_ID(), 6, 10);


-- SALIDA 2
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-05-18 15:00:00', 'SALIDA', 3, 2, NULL,
 'Pedido para cliente corporativo');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 2, 4),
(LAST_INSERT_ID(), 7, 5),
(LAST_INSERT_ID(), 8, 4);


-- TRANSFERENCIA 3
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-05-25 09:30:00', 'TRANSFERENCIA', 4, 1, 3,
 'Abastecimiento de punto de venta');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 4, 6),
(LAST_INSERT_ID(), 18, 20),
(LAST_INSERT_ID(), 19, 5);


-- ENTRADA 4
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-06-02 07:50:00', 'ENTRADA', 7, NULL, 4,
 'Ingreso de componentes de computador');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 9, 20),
(LAST_INSERT_ID(), 12, 25),
(LAST_INSERT_ID(), 15, 15);


-- SALIDA 3
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-06-12 13:40:00', 'SALIDA', 5, 4, NULL,
 'Despacho de componentes para ensamblaje');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 9, 8),
(LAST_INSERT_ID(), 12, 10),
(LAST_INSERT_ID(), 15, 5);


-- TRANSFERENCIA 4
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-06-20 10:10:00', 'TRANSFERENCIA', 6, 4, 2,
 'Traslado de inventario por demanda');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 9, 5),
(LAST_INSERT_ID(), 16, 5),
(LAST_INSERT_ID(), 17, 8);


-- SALIDA 4
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-07-05 16:00:00', 'SALIDA', 2, 1, NULL,
 'Venta de accesorios y periféricos');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 5, 10),
(LAST_INSERT_ID(), 6, 15),
(LAST_INSERT_ID(), 18, 20);


-- ENTRADA 5
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-07-15 08:25:00', 'ENTRADA', 3, NULL, 1,
 'Reposición mensual de inventario');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 10, 15),
(LAST_INSERT_ID(), 11, 10),
(LAST_INSERT_ID(), 20, 20);


-- SALIDA 5
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-08-01 10:50:00', 'SALIDA', 7, 2, NULL,
 'Pedido para empresa local');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 3, 5),
(LAST_INSERT_ID(), 6, 8),
(LAST_INSERT_ID(), 20, 4);


-- TRANSFERENCIA 5
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-08-18 14:15:00', 'TRANSFERENCIA', 4, 1, 4,
 'Redistribución de inventario para alta demanda');


INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 1, 5),
(LAST_INSERT_ID(), 4, 5),
(LAST_INSERT_ID(), 14, 2);


-- SALIDA 6
INSERT INTO movimientos
(fecha, tipo_movimiento, usuario_id, bodega_origen_id,
 bodega_destino_id, observaciones)
VALUES
('2026-08-28 10:30:00', 'SALIDA', 6, 4, NULL,
 'Despacho de equipos para cliente corporativo');

INSERT INTO movimiento_detalle
(movimiento_id, producto_id, cantidad)
VALUES
(LAST_INSERT_ID(), 1, 3),
(LAST_INSERT_ID(), 3, 4),
(LAST_INSERT_ID(), 19, 3);