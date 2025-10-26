-------------------------------------------------
-- LIMPIEZA (borra data existente para un arranque limpio)
-------------------------------------------------

-- primero tablas hijas (tienen foreign keys)
DELETE FROM dbo.transacciones_pago;
DELETE FROM dbo.envios;
DELETE FROM dbo.items_pedido;
DELETE FROM dbo.lista_deseos;
DELETE FROM dbo.pedidos;

-- luego tablas padres
DELETE FROM dbo.productos;
DELETE FROM dbo.cupones;
DELETE FROM dbo.direcciones;
DELETE FROM dbo.usuarios;

-------------------------------------------------
-- SEED DATA (insertamos valores conocidos con IDs fijos)
-------------------------------------------------

-----------------------
-- USUARIOS (id = 1)
-----------------------
SET IDENTITY_INSERT dbo.usuarios ON;
INSERT INTO dbo.usuarios (id, rol, contrasena, nombre, correo)
VALUES (1,'CLIENTE','123','Juan Pérez','juan@example.com');
SET IDENTITY_INSERT dbo.usuarios OFF;

-----------------------
-- DIRECCIONES (id = 1)
-----------------------
SET IDENTITY_INSERT dbo.direcciones ON;
INSERT INTO dbo.direcciones (id, calle, ciudad, provincia, codigo_postal)
VALUES (1,'Av. Siempre Viva 123','Santo Domingo','DN','10101');
SET IDENTITY_INSERT dbo.direcciones OFF;

-----------------------
-- PRODUCTOS (id = 1 y 2)
-----------------------
SET IDENTITY_INSERT dbo.productos ON;
INSERT INTO dbo.productos (id, nombre, descripcion, precio, stock)
VALUES (1,'Camiseta Negra','Algodón 100%', 799.99, 50),
       (2,'Gorra Urbana','Edición limitada', 499.50, 30);
SET IDENTITY_INSERT dbo.productos OFF;

-----------------------
-- CUPON (id = 1)
-----------------------
SET IDENTITY_INSERT dbo.cupones ON;
INSERT INTO dbo.cupones (
    id,
    codigo,
    tipo,
    valor_descuento,
    activo,
    minimo_compra,
    tope_descuento,
    fecha_inicio,
    fecha_fin
)
VALUES (
    1,
    'WELCOME10',
    'PORCENTAJE',
    10,
    1,
    NULL,
    NULL,
    NULL,
    NULL
);
SET IDENTITY_INSERT dbo.cupones OFF;

-------------------------------------------------
-- NOTA:
-- No insertamos pedidos aquí.
-- Los pedidos los vamos a crear nosotros vía Swagger (POST /api/pedidos).
-------------------------------------------------
