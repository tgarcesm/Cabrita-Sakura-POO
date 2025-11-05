import Exceptions.DataPersistenceException;
import Exceptions.EmptyCartException;
import Exceptions.InvalidClientOperationException;
import Exceptions.InvalidProductException;
import model.*;
import persistence.DataManager;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // con esta clase creamos los 4 usuarios predeterminados que se tienen inicialmente
    static class Credencial {
        private String username;
        private String password;
        private String rol;     // podemos distinguir que menu vamos a usar segun el rol
        private Integer clienteId;
        private String email;

        public Credencial(String username, String password, String rol, String email) {
            this.username = username;
            this.password = password;
            this.rol = rol;
            this.email = email;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRol() { return rol; }
        public Integer getClienteId() { return clienteId; }
        public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
        public String getEmail() { return email; }

        public boolean verificarPassword(String ingreso) {
            return password.equals(ingreso);
        }

        public void cambiarPassword(String nueva) {
            this.password = nueva;
        }
    }

    // ======= PERSISTENCIA =======
// no sabiamos hacerlo, lo hicimos con ayuda de la IA
// para que quedara mejor, pero no teniamos conocimientos sobre esto
    private static void guardarDatos(ArrayList<Producto> productos,
                                     ArrayList<Cliente> clientes,
                                     ArrayList<Compra> compras)
            throws DataPersistenceException {

        ArrayList<String> prodLineas = new ArrayList<>();
        for (Producto p : productos) {
            prodLineas.add(p.getId() + ";" + p.getNombre() + ";" + p.getDescripcion() + ";" +
                    p.getPrecio() + ";" + p.getStock() + ";" +
                    p.getFechaLanzamiento() + ";" + p.getCategoria().getNombre());
        }
        DataManager.guardar("productos.csv", prodLineas);

        ArrayList<String> cliLineas = new ArrayList<>();
        for (Cliente c : clientes) {
            cliLineas.add(c.getId() + ";" + c.getDireccionEnvio() + ";" + c.getTelefono());
        }
        DataManager.guardar("clientes.csv", cliLineas);

        ArrayList<String> compLineas = new ArrayList<>();
        for (Compra comp : compras) {
            compLineas.add(comp.getId() + ";" + comp.getFecha() + ";" + comp.getTotal() + ";" + comp.getEstado());
        }
        DataManager.guardar("compras.csv", compLineas);

        System.out.println("💾 Datos guardados correctamente en la carpeta /data.");
    }

    private static void cargarDatos(ArrayList<Producto> productos,
                                    ArrayList<Cliente> clientes,
                                    ArrayList<Compra> compras)
            throws DataPersistenceException, InvalidProductException, InvalidClientOperationException {

        for (String linea : DataManager.cargar("productos.csv")) {
            String[] p = linea.split(";");
            if (p.length >= 7) {
                productos.add(new Producto(
                        Integer.parseInt(p[0]), p[1], p[2],
                        Double.parseDouble(p[3]), Integer.parseInt(p[4]),
                        p[5], new Categoria(0, p[6], "")
                ));
            }
        }

        for (String linea : DataManager.cargar("clientes.csv")) {
            String[] c = linea.split(";");
            if (c.length >= 3) {
                clientes.add(new Cliente(Integer.parseInt(c[0]), c[1], c[2]));
            }
        }

        for (String linea : DataManager.cargar("compras.csv")) {
            String[] comp = linea.split(";");
            if (comp.length >= 4) {
                Compra compra = new Compra();
                compra.setId(Integer.parseInt(comp[0]));
                compra.setFecha(comp[1]);
                compra.setTotal(Double.parseDouble(comp[2]));
                compra.setEstado(comp[3]);
                compras.add(compra);
            }
        }

        System.out.println("📂 Datos cargados correctamente desde /data.");
    }

    // ======= UTILIDADES LOGIN =======

    private static void inicializarUsuariosPorDefecto(ArrayList<Credencial> usuarios) {
        // usuarios prestrablacidos
        usuarios.add(new Credencial("adminContenido", "1234", "ADMIN_CONTENIDO", "contenido@sakura.com"));
        usuarios.add(new Credencial("adminUsuarios", "1234", "ADMIN_USUARIOS", "usuarios@sakura.com"));
        usuarios.add(new Credencial("consejo", "1234", "CONSEJO", "consejo@sakura.com"));
        usuarios.add(new Credencial("sakura", "9999", "SAKURA", "sakura@sakura.com"));
    }


    private static Credencial buscarUsuario(ArrayList<Credencial> usuarios, String username) {
        for (Credencial u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    private static Cliente buscarClientePorId(ArrayList<Cliente> clientes, Integer id) {
        if (id == null) return null;
        for (Cliente c : clientes) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    // ======= MENÚ LOGIN =======

    private static void mostrarMenuLogin() {
        System.out.println("============== SAKURA ENTERPRISES - LOGIN ==============");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse como cliente");
        System.out.println("3. Restablecer contraseña");
        System.out.println("4. Salir");
        System.out.println("========================================================");
    }

    // ======= MENÚ ADMIN CONTENIDO =======

    private static void menuAdminContenido(Scanner sc,
                                           ArrayList<Producto> productos)
            throws InvalidProductException {
        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Administrador de Contenido ===");
            System.out.println("1. Ver productos");
            System.out.println("2. Crear producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Publicar producto (simplemente marcarlo)");
            System.out.println("5. Borrar producto");
            System.out.println("6. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> {
                    if (productos.isEmpty()) System.out.println("No hay productos registrados.");
                    else for (Producto p : productos) // else for, muy buena
                        System.out.println("ID: " + p.getId() + " | " + p.getNombre() +
                                " | $" + p.getPrecio() + " | Stock: " + p.getStock());
                }
                case 2 -> {
                    System.out.print("ID: ");
                    int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("Descripción: ");
                    String desc = sc.nextLine();
                    System.out.print("Precio: ");
                    double precio = Double.parseDouble(sc.nextLine());
                    System.out.print("Stock: ");
                    int stock = Integer.parseInt(sc.nextLine());
                    System.out.print("Fecha lanzamiento: ");
                    String fecha = sc.nextLine();
                    System.out.print("Categoría: ");
                    String cat = sc.nextLine();
                    productos.add(new Producto(id, nombre, desc, precio, stock, fecha, new Categoria(0, cat, "")));
                    System.out.println("✅ Producto creado.");
                }
                case 3 -> {
                    System.out.print("Ingrese ID del producto: ");
                    int idEdit = Integer.parseInt(sc.nextLine());
                    Producto prod = null;
                    for (Producto p : productos)
                        if (p.getId() == idEdit) prod = p;

                    if (prod == null) System.out.println("⚠ Producto no encontrado.");
                    else {
                        System.out.print("Nuevo precio: ");
                        prod.setPrecio(Double.parseDouble(sc.nextLine()));
                        System.out.print("Nuevo stock: ");
                        prod.setStock(Integer.parseInt(sc.nextLine()));
                        System.out.println("✅ Producto actualizado.");
                    }
                }
                case 4 -> {
                    System.out.print("ID del producto a publicar: ");
                    int idPub = Integer.parseInt(sc.nextLine());
                    Producto prod = null;
                    for (Producto p : productos)
                        if (p.getId() == idPub) prod = p;
                    if (prod == null) System.out.println("⚠ Producto no encontrado.");
                    else System.out.println("✅ Producto " + prod.getNombre() + " publicado (simulado).");
                }
                case 5 -> {
                    System.out.print("ID del producto a borrar: ");
                    int idDel = Integer.parseInt(sc.nextLine());
                    boolean eliminado = productos.removeIf(p -> p.getId() == idDel);
                    if (eliminado) System.out.println("✅ Producto eliminado.");
                    else System.out.println("⚠ Producto no encontrado.");
                }
                case 6 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // ======= MENÚ ADMIN USUARIOS =======

    private static void menuAdminUsuarios(Scanner sc,
                                          ArrayList<Credencial> usuarios,
                                          ArrayList<Cliente> clientes)
            throws InvalidClientOperationException { // por si se intenta hacer algo imposible

        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Administrador de Usuarios ===");
            System.out.println("1. Ver usuarios");
            System.out.println("2. Crear usuario");
            System.out.println("3. Eliminar usuario");
            System.out.println("4. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> {
                    for (Credencial u : usuarios) {
                        System.out.println("Usuario: " + u.getUsername() + " | Rol: " + u.getRol());
                    }
                }
                case 2 -> {
                    System.out.print("Nuevo nombre de usuario: ");
                    String nuevoUser = sc.nextLine();
                    if (buscarUsuario(usuarios, nuevoUser) != null) {
                        System.out.println("⚠ Ese nombre de usuario ya existe.");
                        break;
                    }
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Contraseña: ");
                    String pass = sc.nextLine();
                    System.out.println("Rol (1=ADMIN_CONTENIDO, 2=ADMIN_USUARIOS, 3=CONSEJO, 4=SAKURA, 5=CLIENTE): ");
                    int r = Integer.parseInt(sc.nextLine());
                    String rol = "";
                    switch (r) {
                        case 1 -> rol = "ADMIN_CONTENIDO";
                        case 2 -> rol = "ADMIN_USUARIOS";
                        case 3 -> rol = "CONSEJO";
                        case 4 -> rol = "SAKURA";
                        case 5 -> rol = "CLIENTE";
                        default -> {
                            System.out.println("⚠ Rol inválido.");
                            break;
                        }
                    }
                    Credencial nuevo = new Credencial(nuevoUser, pass, rol, email);
                    if (rol.equals("CLIENTE")) {
                        System.out.println("Creación de datos de cliente asociado:");
                        System.out.print("ID cliente (numérico): ");
                        int idC = Integer.parseInt(sc.nextLine());
                        System.out.print("Dirección: ");
                        String dir = sc.nextLine();
                        System.out.print("Teléfono: ");
                        String tel = sc.nextLine();
                        Cliente cliente = new Cliente(idC, dir, tel);
                        clientes.add(cliente);
                        nuevo.setClienteId(idC);
                    }
                    usuarios.add(nuevo);
                    System.out.println("✅ Usuario creado.");
                }
                case 3 -> {
                    System.out.print("Nombre de usuario a eliminar: ");
                    String userDel = sc.nextLine();
                    Credencial u = buscarUsuario(usuarios, userDel);
                    if (u == null) {
                        System.out.println("⚠ Usuario no encontrado.");
                        break;
                    }
                    if ("SAKURA".equals(u.getRol())) {
                        System.out.println("⚠ No se puede eliminar a Sakura.");
                        break;
                    }
                    // si es cliente, opcionalmente eliminar Cliente asociado
                    if (u.getClienteId() != null) {
                        int idCli = u.getClienteId();
                        clientes.removeIf(cl -> cl.getId() == idCli);
                    }
                    usuarios.remove(u);
                    System.out.println("✅ Usuario eliminado.");
                }
                case 4 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // ======= MENÚ CLIENTE =======

    private static void menuCliente(Scanner sc,
                                    ArrayList<Producto> productos,
                                    ArrayList<Cliente> clientes,
                                    ArrayList<Compra> compras,
                                    ArrayList<LineaCarrito> carrito,
                                    Credencial usuarioActual,
                                    Cliente clienteInicial)
            throws InvalidClientOperationException, EmptyCartException { // no se puede comprar si el carro está vacio :(

        Cliente clienteActual = clienteInicial;

        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Cliente (" + usuarioActual.getUsername() + ") ===");
            System.out.println("1. Seleccionar cliente activo");
            System.out.println("2. Agregar producto al carrito");
            System.out.println("3. Ver carrito");
            System.out.println("4. Eliminar producto del carrito");
            System.out.println("5. Realizar compra");
            System.out.println("6. Ver historial de compras");
            System.out.println("7. Buscar producto por nombre o categoría");
            System.out.println("8. Ver detalles de un producto");
            System.out.println("9. Editar datos del cliente");
            System.out.println("10. Editar contraseña");
            System.out.println("11. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> {
                    if (clientes.isEmpty()) {
                        System.out.println("⚠ No hay clientes registrados.");
                        break;
                    }
                    System.out.println("=== Seleccionar cliente activo ===");
                    for (Cliente c : clientes)
                        System.out.println("ID: " + c.getId() + " | " + c.getTelefono());
                    System.out.print("ID del cliente: ");
                    int idSel = Integer.parseInt(sc.nextLine());
                    clienteActual = null;
                    for (Cliente c : clientes)
                        if (c.getId() == idSel) clienteActual = c;
                    if (clienteActual != null)
                        System.out.println("Cliente activo: " + clienteActual.getId());
                    else System.out.println("⚠ Cliente no encontrado.");
                }

                case 2 -> {
                    if (clienteActual == null) {
                        System.out.println("Seleccione cliente antes de continuar.");
                        break;
                    }
                    if (productos.isEmpty()) {
                        System.out.println("⚠ No hay productos disponibles.");
                        break;
                    }
                    System.out.print("ID producto: ");
                    int idP = Integer.parseInt(sc.nextLine());
                    System.out.print("Cantidad: ");
                    int cant = Integer.parseInt(sc.nextLine());
                    Producto prod = null;
                    for (Producto p : productos)
                        if (p.getId() == idP) prod = p;
                    if (prod == null) {
                        System.out.println("⚠ Producto no encontrado.");
                        break;
                    }
                    carrito.add(new LineaCarrito(prod, cant));
                    System.out.println("✅ Producto añadido al carrito.");
                }

                case 3 -> {
                    if (carrito.isEmpty()) System.out.println("Carrito vacío.");
                    else {
                        double total = 0;
                        for (LineaCarrito l : carrito) {
                            System.out.println(l.getProducto().getNombre() + " x" + l.getCantidad() + " = $" + l.getSubtotal());
                            total += l.getSubtotal();
                        }
                        System.out.println("Total parcial: $" + total);
                    }
                }

                case 4 -> {
                    if (carrito.isEmpty()) {
                        System.out.println("⚠ Carrito vacío.");
                        break;
                    }
                    System.out.print("ID producto a eliminar: ");
                    int idElim = Integer.parseInt(sc.nextLine());
                    boolean removed = carrito.removeIf(l -> l.getProducto().getId() == idElim);
                    if (removed) System.out.println("✅ Producto eliminado del carrito.");
                    else System.out.println("⚠ Producto no estaba en el carrito.");
                }

                case 5 -> {
                    if (clienteActual == null) {
                        System.out.println("Seleccione cliente antes de comprar.");
                        break;
                    }
                    if (carrito.isEmpty()) {
                        System.out.println("⚠ Carrito vacío.");
                        break;
                    }
                    System.out.print("Método de pago: ");
                    String metodo = sc.nextLine();
                    Compra compra = new Compra();
                    compra.setMetodoPago(new MetodoPago(1, metodo, "titular", "-0000"));
                    compra.setLineasDesdeCarrito(carrito);
                    compra.calcularTotal();
                    compra.setEstado("PAGADA");
                    compras.add(compra);
                    clienteActual.agregarCompra(compra);
                    carrito.clear();
                    System.out.println("✅ Compra registrada. Total: $" + compra.getTotal());
                }

                case 6 -> {
                    if (clienteActual == null) {
                        System.out.println("Seleccione cliente.");
                        break;
                    }
                    if (clienteActual.getCompras() == null || clienteActual.getCompras().isEmpty()) {
                        System.out.println("Este cliente no tiene compras.");
                        break;
                    }
                    for (Compra c : clienteActual.getCompras())
                        System.out.println("Compra total: $" + c.getTotal() + " | Estado: " + c.getEstado());
                }

                case 7 -> {
                    if (productos.isEmpty()) {
                        System.out.println("⚠ No hay productos.");
                        break;
                    }
                    System.out.println("1. Buscar por nombre\n2. Buscar por categoría");
                    int tipoBusq = Integer.parseInt(sc.nextLine());
                    if (tipoBusq == 1) {
                        System.out.print("Nombre: ");
                        String txt = sc.nextLine().toLowerCase();
                        for (Producto p : productos)
                            if (p.getNombre().toLowerCase().contains(txt))
                                System.out.println(p.getId() + " - " + p.getNombre());
                    } else {
                        System.out.print("Categoría: ");
                        String catB = sc.nextLine().toLowerCase();
                        for (Producto p : productos)
                            if (p.getCategoria().getNombre().toLowerCase().equals(catB))
                                System.out.println(p.getId() + " - " + p.getNombre());
                    }
                }

                case 8 -> {
                    System.out.print("ID producto: ");
                    int idDet = Integer.parseInt(sc.nextLine());
                    for (Producto p : productos)
                        if (p.getId() == idDet) {
                            System.out.println("Nombre: " + p.getNombre());
                            System.out.println("Descripción: " + p.getDescripcion());
                            System.out.println("Precio: $" + p.getPrecio());
                            System.out.println("Stock: " + p.getStock());
                            System.out.println("Categoría: " + p.getCategoria().getNombre());
                        }
                }

                case 9 -> {
                    if (clienteActual == null) {
                        System.out.println("Seleccione cliente.");
                        break;
                    }
                    System.out.print("Nueva dirección: ");
                    clienteActual.setDireccionEnvio(sc.nextLine());
                    System.out.print("Nuevo teléfono: ");
                    clienteActual.setTelefono(sc.nextLine());
                    System.out.println("✅ Cliente actualizado.");
                }

                case 10 -> {
                    System.out.print("Contraseña actual: ");
                    String actual = sc.nextLine();
                    if (!usuarioActual.verificarPassword(actual)) {
                        System.out.println("⚠ Contraseña incorrecta.");
                        break;
                    }
                    System.out.print("Nueva contraseña: ");
                    String nueva = sc.nextLine();
                    usuarioActual.cambiarPassword(nueva);
                    System.out.println("✅ Contraseña actualizada.");
                }

                case 11 -> seguir = false;

                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // ======= MENÚ SAKURA =======

    private static void menuSakura(Scanner sc,
                                   ArrayList<Credencial> usuarios,
                                   ArrayList<Producto> productos,
                                   ArrayList<Cliente> clientes,
                                   ArrayList<Compra> compras,
                                   ArrayList<LineaCarrito> carrito,
                                   ArrayList<Fabrica> fabricaList,
                                   ArrayList<TrabajadorEsclavizado> registroConfidencial)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException {

        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú de Sakura (Dueña) ===");
            System.out.println("1. Menú Administrador de Contenido");
            System.out.println("2. Menú Administrador de Usuarios");
            System.out.println("3. Menú Cliente (ver como cliente)");
            System.out.println("4. Gestión básica de fábricas");
            System.out.println("5. Registrar trabajadores esclavizados");
            System.out.println("6. Ver registro confidencial de trabajadores esclavizados");
            System.out.println("7. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> menuAdminContenido(sc, productos);

                case 2 -> menuAdminUsuarios(sc, usuarios, clientes);

                case 3 -> {
                    // elegir cliente para ver como cliente
                    if (clientes.isEmpty()) {
                        System.out.println("⚠ No hay clientes.");
                        break;
                    }
                    System.out.println("Seleccione cliente para ver como:");
                    for (Cliente c : clientes)
                        System.out.println("ID: " + c.getId() + " | Tel: " + c.getTelefono());
                    System.out.print("ID cliente: ");
                    int idCli = Integer.parseInt(sc.nextLine());
                    Cliente cliente = buscarClientePorId(clientes, idCli);
                    if (cliente == null) {
                        System.out.println("⚠ Cliente no encontrado.");
                        break;
                    }
                    Credencial fake = new Credencial("sakura-como-cliente", "x", "CLIENTE", "sakura@sakura.com");
                    fake.setClienteId(cliente.getId());
                    menuCliente(sc, productos, clientes, compras, carrito, fake, cliente);
                }

                case 4 -> {
                    System.out.println("1. Registrar fábrica\n2. Ver fábricas");
                    int subF = Integer.parseInt(sc.nextLine());
                    if (subF == 1) {
                        System.out.print("ID: ");
                        int idF = Integer.parseInt(sc.nextLine());
                        System.out.print("País: ");
                        String pais = sc.nextLine();
                        System.out.print("Ciudad: ");
                        String ciudad = sc.nextLine();
                        System.out.print("Capacidad: ");
                        int cap = Integer.parseInt(sc.nextLine());
                        System.out.print("Nivel automatización: ");
                        int auto = Integer.parseInt(sc.nextLine());
                        fabricaList.add(new Fabrica(idF, pais, ciudad, cap, auto));
                        System.out.println("✅ Fábrica registrada.");
                    } else for (Fabrica f : fabricaList)
                        System.out.println("ID: " + f.getId() + " | " + f.getCiudad() + " - " + f.getPais());
                }

                case 5 -> {
                    if (fabricaList.isEmpty()) {
                        System.out.println("⚠ Registre fábricas primero.");
                        break;
                    }
                    System.out.print("ID trabajador: ");
                    int idT = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: ");
                    String nomT = sc.nextLine();
                    System.out.print("País origen: ");
                    String paisO = sc.nextLine();
                    System.out.print("Edad: ");
                    int edad = Integer.parseInt(sc.nextLine());
                    System.out.print("Salud: ");
                    String salud = sc.nextLine();
                    System.out.println("Seleccione fábrica:");
                    for (Fabrica f : fabricaList)
                        System.out.println(f.getId() + " - " + f.getCiudad());
                    int idFab = Integer.parseInt(sc.nextLine());
                    Fabrica fabSeleccionada = null;
                    for (Fabrica f : fabricaList)
                        if (f.getId() == idFab) fabSeleccionada = f;

                    if (fabSeleccionada == null) {
                        System.out.println("⚠ Fábrica no encontrada.");
                        break;
                    }

                    TrabajadorEsclavizado t = new TrabajadorEsclavizado(idT, nomT, paisO, edad, "Hoy", salud, true);
                    fabSeleccionada.asignarTrabajador(t);
                    registroConfidencial.add(t);
                    System.out.println("✅ Trabajador asignado y registrado confidencialmente.");
                }

                case 6 -> {
                    if (registroConfidencial.isEmpty()) {
                        System.out.println("No hay trabajadores registrados confidencialmente.");
                        break;
                    }
                    System.out.println("=== Registro confidencial de trabajadores ===");
                    for (TrabajadorEsclavizado t : registroConfidencial) {
                        // Asumiendo getters básicos
                        System.out.println("ID: " + t.getId() + " | Nombre: " + t.getNombre());
                    }
                }

                case 7 -> seguir = false;

                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // ======= MENÚ CONSEJO SOMBRÍO =======

    private static void menuConsejo(Scanner sc,
                                    ArrayList<Credencial> usuarios,
                                    ArrayList<Producto> productos,
                                    ArrayList<Cliente> clientes,
                                    ArrayList<Compra> compras,
                                    ArrayList<LineaCarrito> carrito,
                                    ArrayList<Fabrica> fabricaList,
                                    ArrayList<TrabajadorEsclavizado> registroConfidencial)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException {

        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Consejo Sombrío ===");
            System.out.println("1. Menú Administrador de Contenido");
            System.out.println("2. Menú Administrador de Usuarios");
            System.out.println("3. Ingresar como Sakura");
            System.out.println("4. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> menuAdminContenido(sc, productos);
                case 2 -> menuAdminUsuarios(sc, usuarios, clientes);
                case 3 -> {
                    Credencial sakura = buscarUsuario(usuarios, "sakura");
                    if (sakura == null) {
                        System.out.println("⚠ No existe el usuario Sakura.");
                        break;
                    }
                    System.out.print("Contraseña de Sakura: ");
                    String pass = sc.nextLine();
                    if (!sakura.verificarPassword(pass)) {
                        System.out.println("⚠ Contraseña incorrecta.");
                        break;
                    }
                    menuSakura(sc, usuarios, productos, clientes, compras, carrito, fabricaList, registroConfidencial);
                }
                case 4 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // ahora si empezamos

    public static void main(String[] args)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException, DataPersistenceException {

        Scanner sc = new Scanner(System.in);

        ArrayList<Producto> productos = new ArrayList<>();
        ArrayList<Cliente> clientes = new ArrayList<>();
        ArrayList<Compra> compras = new ArrayList<>();
        ArrayList<LineaCarrito> carrito = new ArrayList<>();
        ArrayList<Fabrica> fabricaList = new ArrayList<>();
        ArrayList<TrabajadorEsclavizado> registroConfidencial = new ArrayList<>();
        ArrayList<Credencial> usuarios = new ArrayList<>();

        cargarDatos(productos, clientes, compras);
        inicializarUsuariosPorDefecto(usuarios);

        boolean salir = false;

        while (!salir) {
            mostrarMenuLogin();
            System.out.print("→ Opción: ");
            int opcionLogin = Integer.parseInt(sc.nextLine());

            switch (opcionLogin) {
                case 1 -> {
                    System.out.print("Usuario: ");
                    String user = sc.nextLine();
                    System.out.print("Contraseña: ");
                    String pass = sc.nextLine();
                    Credencial u = buscarUsuario(usuarios, user);
                    if (u == null || !u.verificarPassword(pass)) {
                        System.out.println("⚠ Usuario o contraseña incorrectos.");
                        break;
                    }
                    switch (u.getRol()) {
                        case "ADMIN_CONTENIDO" -> menuAdminContenido(sc, productos);
                        case "ADMIN_USUARIOS" -> menuAdminUsuarios(sc, usuarios, clientes);
                        case "CONSEJO" -> menuConsejo(sc, usuarios, productos, clientes, compras, carrito, fabricaList, registroConfidencial);
                        case "SAKURA" -> menuSakura(sc, usuarios, productos, clientes, compras, carrito, fabricaList, registroConfidencial);
                        case "CLIENTE" -> {
                            Cliente cli = buscarClientePorId(clientes, u.getClienteId());
                            if (cli == null) {
                                System.out.println("⚠ No se encontró el cliente asociado a este usuario.");
                                break;
                            }
                            menuCliente(sc, productos, clientes, compras, carrito, u, cli); // bases
                        }
                        default -> System.out.println("⚠ Rol desconocido.");
                    }
                }

                case 2 -> {
                    // Registrarse como cliente
                    System.out.print("Elija nombre de usuario: ");
                    String nuevoUser = sc.nextLine();
                    if (buscarUsuario(usuarios, nuevoUser) != null) {
                        System.out.println("⚠ Ese usuario ya existe.");
                        break;
                    }
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Contraseña: ");
                    String pass = sc.nextLine();

                    System.out.println("Datos del cliente asociado:");
                    System.out.print("ID (número): ");
                    int idC = Integer.parseInt(sc.nextLine());
                    System.out.print("Dirección: ");
                    String dir = sc.nextLine();
                    System.out.print("Teléfono: ");
                    String tel = sc.nextLine();
                    Cliente cliente = new Cliente(idC, dir, tel);
                    clientes.add(cliente);

                    Credencial cred = new Credencial(nuevoUser, pass, "CLIENTE", email);
                    cred.setClienteId(idC);
                    usuarios.add(cred);

                    System.out.println("✅ Usuario cliente registrado. Ahora puedes iniciar sesión.");
                }

                case 3 -> {
                    // Restablecer contraseña
                    System.out.print("Nombre de usuario: ");
                    String user = sc.nextLine();
                    Credencial u = buscarUsuario(usuarios, user);
                    if (u == null) {
                        System.out.println("⚠ Usuario no encontrado.");
                        break;
                    }
                    System.out.print("Email registrado: ");
                    String email = sc.nextLine();
                    if (!u.getEmail().equalsIgnoreCase(email)) {
                        System.out.println("⚠ El email no coincide.");
                        break;
                    }
                    System.out.print("Nueva contraseña: ");
                    String nueva = sc.nextLine();
                    u.cambiarPassword(nueva);
                    System.out.println("✅ Contraseña restablecida.");
                }

                case 4 -> {
                    System.out.println("Guardando cambios y saliendo...");
                    guardarDatos(productos, clientes, compras);
                    salir = true;
                }

                default -> System.out.println("⚠ Opción inválida.");
            }
        }

        sc.close();
        System.out.println(" Gracias por usar Sakura Enterprises ");
    }
}