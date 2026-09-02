DROP DATABASE IF EXISTS logitrack_db;
CREATE DATABASE logitrack_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE logitrack_db;

CREATE TABLE usuarios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    email           VARCHAR(120) NOT NULL UNIQUE,
    nombre_completo VARCHAR(150) NOT NULL,
    rol             ENUM('ADMIN', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE bodegas (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    ubicacion      VARCHAR(150) NOT NULL,
    capacidad      INT NOT NULL,
    encargado      VARCHAR(150) NOT NULL,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bodega_capacidad CHECK (capacidad > 0)
) ENGINE=InnoDB;

CREATE TABLE productos (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(150) NOT NULL,
    categoria      VARCHAR(80)  NOT NULL,
    stock          INT NOT NULL DEFAULT 0,
    precio         DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_producto_stock  CHECK (stock >= 0),
    CONSTRAINT chk_producto_precio CHECK (precio >= 0)
) ENGINE=InnoDB;

CREATE TABLE movimientos (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento   ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
    usuario_id        BIGINT NOT NULL,
    bodega_origen_id  BIGINT NULL,
    bodega_destino_id BIGINT NULL,
    observaciones     VARCHAR(255) NULL,
    CONSTRAINT fk_mov_usuario         FOREIGN KEY (usuario_id)        REFERENCES usuarios(id),
    CONSTRAINT fk_mov_bodega_origen   FOREIGN KEY (bodega_origen_id)  REFERENCES bodegas(id),
    CONSTRAINT fk_mov_bodega_destino  FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(id),
    CONSTRAINT chk_mov_tipo_coherencia CHECK (
        (tipo_movimiento = 'ENTRADA'       AND bodega_destino_id IS NOT NULL) OR
        (tipo_movimiento = 'SALIDA'        AND bodega_origen_id  IS NOT NULL) OR
        (tipo_movimiento = 'TRANSFERENCIA' AND bodega_origen_id IS NOT NULL AND bodega_destino_id IS NOT NULL)
    )
) ENGINE=InnoDB;


CREATE TABLE movimiento_detalle (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    producto_id   BIGINT NOT NULL,
    cantidad      INT NOT NULL,
    CONSTRAINT fk_detalle_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimientos(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto   FOREIGN KEY (producto_id)   REFERENCES productos(id),
    CONSTRAINT uq_detalle_mov_producto UNIQUE (movimiento_id, producto_id),
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB;


CREATE TABLE auditoria (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion   ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_hora       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id       BIGINT NULL,
    entidad_afectada VARCHAR(100) NOT NULL,
    entidad_id       BIGINT NOT NULL,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;


CREATE TABLE auditoria_detalle (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    auditoria_id    BIGINT NOT NULL,
    campo           VARCHAR(100) NOT NULL,
    valor_anterior  VARCHAR(255) NULL,
    valor_nuevo     VARCHAR(255) NULL,
    CONSTRAINT fk_auditoria_detalle FOREIGN KEY (auditoria_id) REFERENCES auditoria(id) ON DELETE CASCADE,
    CONSTRAINT uq_auditoria_campo UNIQUE (auditoria_id, campo)
) ENGINE=InnoDB;

-- =========================================================
-- Indices para consultas avanzadas y reportes
-- =========================================================

-- Productos con stock bajo (< 10 unidades)
CREATE INDEX idx_productos_stock     ON productos(stock);
CREATE INDEX idx_productos_categoria ON productos(categoria);

-- Movimientos por rango de fechas y tipo
CREATE INDEX idx_movimientos_fecha   ON movimientos(fecha);
CREATE INDEX idx_movimientos_tipo    ON movimientos(tipo_movimiento);
CREATE INDEX idx_movimientos_usuario ON movimientos(usuario_id);

-- Detalle: acelerar "productos mas movidos"
CREATE INDEX idx_detalle_producto   ON movimiento_detalle(producto_id);
CREATE INDEX idx_detalle_movimiento ON movimiento_detalle(movimiento_id);

-- Auditoria por usuario, tipo de operacion o entidad
CREATE INDEX idx_auditoria_usuario  ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_tipo     ON auditoria(tipo_operacion);
CREATE INDEX idx_auditoria_fecha    ON auditoria(fecha_hora);
CREATE INDEX idx_auditoria_entidad  ON auditoria(entidad_afectada, entidad_id);



DELIMITER $$

-- ---------- PRODUCTOS ----------

CREATE TRIGGER trg_productos_after_insert
AFTER INSERT ON productos
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('INSERT', @usuario_actual_id, 'Producto', NEW.id);

    SET @auditoria_id = LAST_INSERT_ID();

    INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo) VALUES
    (@auditoria_id, 'nombre',    NULL, NEW.nombre),
    (@auditoria_id, 'categoria', NULL, NEW.categoria),
    (@auditoria_id, 'stock',     NULL, NEW.stock),
    (@auditoria_id, 'precio',    NULL, NEW.precio);
END$$

CREATE TRIGGER trg_productos_after_update
AFTER UPDATE ON productos
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('UPDATE', @usuario_actual_id, 'Producto', NEW.id);

    SET @auditoria_id = LAST_INSERT_ID();

    IF NOT (OLD.nombre <=> NEW.nombre) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'nombre', OLD.nombre, NEW.nombre);
    END IF;

    IF NOT (OLD.categoria <=> NEW.categoria) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'categoria', OLD.categoria, NEW.categoria);
    END IF;

    IF NOT (OLD.stock <=> NEW.stock) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'stock', OLD.stock, NEW.stock);
    END IF;

    IF NOT (OLD.precio <=> NEW.precio) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'precio', OLD.precio, NEW.precio);
    END IF;
END$$

CREATE TRIGGER trg_productos_after_delete
AFTER DELETE ON productos
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('DELETE', @usuario_actual_id, 'Producto', OLD.id);

    SET @auditoria_id = LAST_INSERT_ID();

    INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo) VALUES
    (@auditoria_id, 'nombre',    OLD.nombre,    NULL),
    (@auditoria_id, 'categoria', OLD.categoria, NULL),
    (@auditoria_id, 'stock',     OLD.stock,     NULL),
    (@auditoria_id, 'precio',    OLD.precio,    NULL);
END$$

-- ---------- BODEGAS ----------

CREATE TRIGGER trg_bodegas_after_insert
AFTER INSERT ON bodegas
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('INSERT', @usuario_actual_id, 'Bodega', NEW.id);

    SET @auditoria_id = LAST_INSERT_ID();

    INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo) VALUES
    (@auditoria_id, 'nombre',    NULL, NEW.nombre),
    (@auditoria_id, 'ubicacion', NULL, NEW.ubicacion),
    (@auditoria_id, 'capacidad', NULL, NEW.capacidad),
    (@auditoria_id, 'encargado', NULL, NEW.encargado);
END$$

CREATE TRIGGER trg_bodegas_after_update
AFTER UPDATE ON bodegas
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('UPDATE', @usuario_actual_id, 'Bodega', NEW.id);

    SET @auditoria_id = LAST_INSERT_ID();

    IF NOT (OLD.nombre <=> NEW.nombre) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'nombre', OLD.nombre, NEW.nombre);
    END IF;

    IF NOT (OLD.ubicacion <=> NEW.ubicacion) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'ubicacion', OLD.ubicacion, NEW.ubicacion);
    END IF;

    IF NOT (OLD.capacidad <=> NEW.capacidad) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'capacidad', OLD.capacidad, NEW.capacidad);
    END IF;

    IF NOT (OLD.encargado <=> NEW.encargado) THEN
        INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo)
        VALUES (@auditoria_id, 'encargado', OLD.encargado, NEW.encargado);
    END IF;
END$$

CREATE TRIGGER trg_bodegas_after_delete
AFTER DELETE ON bodegas
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('DELETE', @usuario_actual_id, 'Bodega', OLD.id);

    SET @auditoria_id = LAST_INSERT_ID();

    INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo) VALUES
    (@auditoria_id, 'nombre',    OLD.nombre,    NULL),
    (@auditoria_id, 'ubicacion', OLD.ubicacion, NULL),
    (@auditoria_id, 'capacidad', OLD.capacidad, NULL),
    (@auditoria_id, 'encargado', OLD.encargado, NULL);
END$$

-- ---------- MOVIMIENTOS ----------

CREATE TRIGGER trg_movimientos_after_insert
AFTER INSERT ON movimientos
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('INSERT', @usuario_actual_id, 'Movimiento', NEW.id);

    SET @auditoria_id = LAST_INSERT_ID();

    INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo) VALUES
    (@auditoria_id, 'tipo_movimiento',   NULL, NEW.tipo_movimiento),
    (@auditoria_id, 'bodega_origen_id',  NULL, NEW.bodega_origen_id),
    (@auditoria_id, 'bodega_destino_id', NULL, NEW.bodega_destino_id);
END$$

CREATE TRIGGER trg_movimientos_after_delete
AFTER DELETE ON movimientos
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tipo_operacion, usuario_id, entidad_afectada, entidad_id)
    VALUES ('DELETE', @usuario_actual_id, 'Movimiento', OLD.id);

    SET @auditoria_id = LAST_INSERT_ID();

    INSERT INTO auditoria_detalle (auditoria_id, campo, valor_anterior, valor_nuevo) VALUES
    (@auditoria_id, 'tipo_movimiento',   OLD.tipo_movimiento,   NULL),
    (@auditoria_id, 'bodega_origen_id',  OLD.bodega_origen_id,  NULL),
    (@auditoria_id, 'bodega_destino_id', OLD.bodega_destino_id, NULL);
END$$

DELIMITER ;