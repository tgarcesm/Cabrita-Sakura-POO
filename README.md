Orientado por la IA
Opción 1 – Iniciar sesión
1.	Pide:
o	Email
o	Contraseña
2.	Busca el usuario por email entre:
o	Administradores de contenido
o	Administradores de usuario
o	Desarrolladores
o	Clientes
3.	Según el rol del usuario entra a uno de estos menús:
o	ADMIN_CONTENIDO → Menú Administrador de Contenido
o	ADMIN_USUARIOS → Menú Administrador de Usuarios
o	DESARROLLADOR → Menú Administrador de Contenido
o	CONSEJO → Menú Consejo Sombrío
o	SAKURA → Menú de Sakura
o	CLIENTE → Menú Cliente
Si email/contraseña no coinciden → mensaje de error y vuelve al login.
________________________________________
Opción 2 – Registrarse como cliente
Flujo:
1.	Pide:
o	ID (número)
o	Nombre
o	Email (login)
o	Contraseña
o	Dirección
o	Teléfono
2.	Verifica que el email no exista ya en ningún tipo de usuario.
3.	Crea un objeto Cliente con rol "CLIENTE".
4.	Lo agrega a st.clientes.
Importante: en este código, el comentario dice “se guardará al salir” porque la persistencia definitiva se hace cuando eliges “4. Salir” en el menú principal, que llama a guardarTodo(st).
________________________________________
Opción 3 – Restablecer contraseña (por email)
1.	Pide:
o	Email
2.	Busca el usuario con ese email en todos los tipos.
3.	Si existe, pide:
o	Nueva contraseña
4.	Cambia el campo passwordHash del usuario en memoria mediante reflexión.
El cambio queda guardado en los CSV cuando se elige “4. Salir” y se ejecuta guardarTodo(st).
________________________________________
Opción 4 – Salir
1.	Muestra "Guardando cambios y saliendo".
2.	Llama a guardarTodo(st), que:
o	Escribe todas las categorías en categorias.csv.
o	Escribe todos los productos en productos.csv.
o	Escribe todos los clientes en clientes.csv.
o	Escribe todos los métodos de pago en metodos_pago.csv.
o	Escribe todas las compras en compras.csv (y sus líneas).
o	Escribe fábricas, trabajadores, admins, desarrolladores, consejos, etc.
3.	Sale del programa.
Si cierras el programa “a lo bruto” sin pasar por la opción 4, los cambios que estaban solo en memoria no se guardan.
________________________________________
4. Menú Administrador de Contenido
Acceden:
•	ADMIN_CONTENIDO
•	DESARROLLADOR
•	También Sakura y Consejo si eligen esta opción desde sus propios menús.
Opciones:
1.	Ver productos
Lista todos los productos con:
o	ID, nombre, precio, stock.
2.	Crear producto
Pide:
o	ID, nombre, descripción, precio, stock, fecha lanzamiento, categoría (texto)
Crea un Producto con una Categoria simple (por nombre) y lo agrega a la lista de productos.
3.	Editar producto
o	Pide ID del producto.
o	Si existe → permite cambiar precio y stock.
4.	Publicar producto (marcar)
o	Pide ID del producto.
o	Si existe → solo muestra un mensaje como “publicado” (es un efecto visual/simbólico).
5.	Borrar producto
o	Pide ID.
o	Lo elimina de la lista de productos si existe.
6.	Volver al login
o	Regresa al menú principal.
Los cambios definitivos se guardan en CSV al salir del sistema (guardarTodo).
________________________________________
5. Menú Administrador de Usuarios
Acceden:
•	ADMIN_USUARIOS
•	También Sakura y Consejo a través de sus menús.
Opciones:
1.	Ver usuarios (todos)
Muestra por categorías:
o	Admin Contenido
o	Admin Usuario
o	Desarrolladores
o	Clientes
Indicando id, email y rol.
2.	Crear usuario
Pide:
o	Tipo de usuario:
1.	Admin Contenido
2.	Admin Usuario
3.	Desarrollador
4.	Cliente
o	ID, nombre, email (login), contraseña.
o	Para Admin Usuario pide también “nivel de acceso”.
o	Para Cliente pide también:
	dirección
	teléfono
Según el tipo, crea:
o	AdministradorContenido
o	AdministradorUsuario
o	DesarrolladorProducto
o	Cliente
3.	Eliminar usuario (por email)
o	Pide email.
o	Busca y elimina coincidencias en todas las listas de usuarios (admins, devs, clientes).
4.	Volver
o	Regresa al menú anterior (Sakura, Consejo o login).
Igual que en el resto del sistema, esta creación/eliminación afecta primero la memoria, y se persiste en CSV al salir.
________________________________________
6. Menú Cliente
Acceden:
•	Cualquier usuario con rol "CLIENTE" que haya iniciado sesión.
•	Sakura también puede entrar “como cliente” desde su propio menú.
Opciones:
1.	Seleccionar cliente activo
Lista todos los clientes y permite elegir uno por ID para trabajar sobre él (útil cuando Sakura prueba distintos clientes).
2.	Agregar producto al carrito
Pide:
o	ID de producto
o	Cantidad
Añade un LineaCarrito al carrito en memoria.
3.	Ver carrito
Muestra:
o	Productos del carrito con nombre, cantidad y subtotal.
o	Total parcial de la compra.
4.	Eliminar producto del carrito
Pide ID de producto y lo quita del carrito si está.
5.	Realizar compra
o	Verifica que haya cliente activo y carrito no vacío.
o	Pide texto de método de pago.
o	Crea un objeto Compra:
	Asigna un MetodoPago.
	Copia las líneas del carrito.
	Calcula total.
	Marca estado "Pagada".
o	Agrega la compra a la lista global de compras y a clienteActual.
o	Vacía el carrito.
6.	Ver historial de compras
Recorre clienteActual.getCompras() mostrando total y estado de cada compra.
7.	Buscar producto por nombre o categoría
Permite elegir:
o	Buscar por nombre → muestra productos cuyo nombre contiene el texto.
o	Buscar por categoría → muestra productos cuya categoría coincide (en minúsculas).
8.	Ver detalles de un producto
Pide ID de producto y muestra:
o	nombre, descripción, precio, stock, categoría.
9.	Editar datos del cliente
o	Cambia dirección de envío.
o	Cambia teléfono.
10.	Cambiar contraseña
o	Pide contraseña actual.
o	Si coincide con passwordHash del cliente → pide nueva contraseña y la asigna (en memoria).
11.	Volver
o	Regresa al menú anterior (Sakura o login).
________________________________________
7. Menú de Sakura (dueña)
Accede solo:
•	Usuario con email sakura@sakura.com y rol SAKURA.
Opciones:
1.	Menú Administrador de Contenido
Reutiliza el mismo menú visto antes.
2.	Menú Administrador de Usuarios
Reutiliza el menú admin usuarios.
3.	Menú Cliente (ver como cliente)
o	Lista clientes por ID y email.
o	Permite elegir uno y abre el menú de cliente “simulando” que Sakura es ese cliente.
4.	Gestión básica de fábricas
Submenú:
o	Registrar fábrica → pide id, país, ciudad, capacidad, nivel de automatización, crea una Fabrica y la guarda en memoria.
o	Ver fábricas → lista todas las fábricas registradas.
5.	Registrar trabajadores esclavizados
o	Pide datos del trabajador (id, nombre, país origen, edad, salud).
o	Pide fábrica por id.
o	Asigna el trabajador a la fábrica y lo agrega al registro global de trabajadores.
6.	Ver registro confidencial de trabajadores
o	Lista todos los trabajadores con:
	id, nombre
	y la fábrica donde trabajan (si se puede encontrar).
7.	Volver
o	Regresa al menú Consejo o login.
________________________________________
8. Menú Consejo Sombrío
Acceden:
•	Usuarios con rol "CONSEJO".
Opciones:
1.	Menú Administrador de Contenido
Igual que el admin estándar.
2.	Menú Administrador de Usuarios
Igual que el admin estándar.
3.	Ingresar como Sakura
o	Pide la contraseña de sakura@sakura.com.
o	Si es correcta, abre el menú de Sakura.
4.	Volver
o	Regresa al login.
________________________________________
9. Nota sobre la parte de persistencia (honesta)
La parte de persistencia (clase DataManager y todas las clases *Csv del paquete persistence.csv) no la diseñamos solos desde cero.
•	No sabíamos bien cómo estructurar la lectura y escritura en CSV de forma ordenada.
•	Por eso pedimos ayuda a una inteligencia artificial (ChatGPT) para:
o	Generar el esqueleto de DataManager.
o	Generar las clases de mapeo XxxCsv (por ejemplo ClienteCsv, AdministradorContenidoCsv, etc.).
o	Manejar temas como:
	asegurarse de que los archivos existan y tengan cabecera (header());
	ignorar líneas vacías o corruptas;
	reescribir archivos completos (saveAll…) sin romper el formato.
Luego fuimos leyendo y entendiendo casi todos los procesos:
•	Cargar listas desde CSV en el arranque (cargarTodo()).
•	Guardar todo al salir (guardarTodo()).
•	Cómo los roles y usuarios se mezclan con los datos de la tienda (clientes, compras, etc.).
En resumen:
•	La lógica del menú, flujos de usuario, mensajes en consola, nombres, y reglas de negocio los hicimos nosotros.
•	Hay que tener en cuenta que la parte del persistence y todos los csv se hicieron con apoyo de la IA, con el fin de que el entregable quedara mucho mas completo y se pareciera lo mejor posible a una base de datos teniendo en cuenta que no era obligatorio (así vamos guardando los datos y los podemos usar luego sin necesidad de ingresar por consola siempre), entendimos casi todos los procedimientos realizados aquí, sin embargo, hay unos que no pudimos entender a la perfección, tuvimos un pequeño problema que el cual por alguna razón no nos está guardando los datos correctamente del registro del cliente, sin embargo, los datos de administrador de contenido, administrador de usuarios y todos los datos que deberían quedar en la memoria quedan, agradecemos si nos puedes explicar porque no nos dio con los clientes 😊. Cabe aclarar que este error no afecta en lo absoluto en el funcionamiento del sistema.

