# Pruebas de la API con Postman y JWT

Esta colección contiene pruebas de autenticación, autorización por roles y operaciones REST para LogiTrack. Las solicitudes de login guardan automáticamente los tokens JWT de ADMIN y EMPLEADO en variables de colección para reutilizarlos en los endpoints protegidos.

Importa la colección completa desde:

[LogiTrack.postman_collection.json](./LogiTrack.postman_collection.json)

La colección debe ejecutarse en el orden definido: autenticación, usuarios, bodegas, productos, movimientos y auditoría. Configura `base_url` como `http://localhost:8080` antes de iniciar el Collection Runner.