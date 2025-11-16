-- Insertar USUARIOS
INSERT INTO core.usuario (usuario_id, nombre, email, hash_password, rol) VALUES (1, 'Juan Perez', 'juan@test.com', 'hash123', 'CUSTOMER');
INSERT INTO core.usuario (usuario_id, nombre, email, hash_password, rol) VALUES (2, 'Maria Garcia', 'maria@test.com', 'hash456', 'CUSTOMER');
INSERT INTO core.usuario (usuario_id, nombre, email, hash_password, rol) VALUES (3, 'Admin User', 'admin@test.com', 'hashAdmin', 'ADMIN');

-- Insertar DIRECCIONES
INSERT INTO core.direcciones (id, calle, ciudad, provincia, codigo_postal) VALUES (1, 'Calle Principal 123', 'Santo Domingo', 'Nacional', '10001');
INSERT INTO core.direcciones (id, calle, ciudad, provincia, codigo_postal) VALUES (2, 'Avenida Independencia 456', 'Santiago', 'Santiago', '51000');
INSERT INTO core.direcciones (id, calle, ciudad, provincia, codigo_postal) VALUES (3, 'Calle Segunda 789', 'La Vega', 'La Vega', '41000');

-- Insertar PRODUCTOS
INSERT INTO core.producto (producto_id, nombre, descripcion, precio, stock, sku) VALUES (1, 'Camiseta Básica', 'Camiseta de algodón 100%', 500.00, 50, 'CAM-001');
INSERT INTO core.producto (producto_id, nombre, descripcion, precio, stock, sku) VALUES (2, 'Pantalón Jean', 'Jean azul talla M', 1200.00, 30, 'PAN-002');
INSERT INTO core.producto (producto_id, nombre, descripcion, precio, stock, sku) VALUES (3, 'Zapatos Deportivos', 'Zapatos para correr', 2500.00, 20, 'ZAP-003');
INSERT INTO core.producto (producto_id, nombre, descripcion, precio, stock, sku) VALUES (4, 'Gorra', 'Gorra negra ajustable', 300.00, 100, 'GOR-004');