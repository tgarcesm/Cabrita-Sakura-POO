import Exceptions.DataPersistenceException;
import Exceptions.EmptyCartException;
import Exceptions.InvalidClientOperationException;
import Exceptions.InvalidProductException;

import model.*;

import persistence.PersistenceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    // ================== UTIL GENERAL ==================
    private static <T> Map<Integer, T> indexById(List<T> list, java.util.function.ToIntFunction<T> idGetter) {
        Map<Integer, T> map = new HashMap<>();
        for (T t : list) map.put(idGetter.applyAsInt(t), t);
        return map;
    }

    // ================== CREDENCIAL (LOGIN) ==================
    static class Credencial {
        private String username;
        private String password;
        private String rol;
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

        public boolean verificarPassword(String ingreso) { return password.equals(ingreso); }
        public void cambiarPassword(String nueva) { this.password = nueva; }
    }

    // ================== PERSISTENCIA (USANDO PersistenceService) ==================

    private static final PersistenceService PS = new PersistenceService();

    private static class EstadoPersistente {
        ArrayList<Categoria> categorias = new ArrayList<>();
        ArrayList<Producto> productos = new ArrayList<>();
        ArrayList<Cliente> clientes = new ArrayList<>();
        ArrayList<MetodoPago> metodosPago = new ArrayList<>();
        ArrayList<Compra> compras = new ArrayList<>();
    }

    private static EstadoPersistente cargarTodo()
            throws DataPersistenceException, InvalidProductException, InvalidClientOperationException {
        EstadoPersistente st = new EstadoPersistente();
        st.categorias.addAll(PS.cargarCategorias());
        var idxCat = indexById(st.categorias, Categoria::getId);

        st.productos.addAll(PS.cargarProductos(idxCat));

        // ⬇️ ahora este método también puede lanzar InvalidClientOperationException
        st.clientes.addAll(PS.cargarClientes());

        st.metodosPago.addAll(PS.cargarMetodosPago());
        var idxMetodo = indexById(st.metodosPago, MetodoPago::getId);

        var idxProd = indexById(st.productos, Producto::getId);
        st.compras.addAll(PS.cargarCompras(idxMetodo, idxProd));

        System.out.println("📂 Datos cargados desde /data con PersistenceService.");
        return st;
    }


    private static void guardarTodo(EstadoPersistente st) throws DataPersistenceException {
        // Guardamos en el orden habitual (no se persiste Dueña)
        PS.guardarCategorias(st.categorias);
        PS.guardarProductos(st.productos);
        PS.guardarClientes(st.clientes);

        // Persistimos métodos de pago solo si el proyecto los está manejando
        if (!st.metodosPago.isEmpty()) PS.guardarMetodosPago(st.metodosPago);

        PS.guardarCompras(st.compras);

        System.out.println("💾 Datos guardados correctamente en /data con PersistenceService.");
    }

    // ================== UTILIDADES LOGIN ==================

    private static void inicializarUsuariosPorDefecto(ArrayList<Credencial> usuarios) {
        usuarios.add(new Credencial("adminContenido", "1234", "ADMIN_CONTENIDO", "contenido@sakura.com"));
        usuarios.add(new Credencial("adminUsuarios", "1234", "ADMIN_USUARIOS", "usuarios@sakura.com"));
        usuarios.add(new Credencial("consejo", "1234", "CONSEJO", "consejo@sakura.com"));
        usuarios.add(new Credencial("sakura", "9999", "SAKURA", "sakura@sakura.com"));
    }

    private static Credencial buscarUsuario(ArrayList<Credencial> usuarios, String username) {
        for (Credencial u : usuarios) if (u.getUsername().equalsIgnoreCase(username)) return u;
        return null;
    }

    private static Cliente buscarClientePorId(ArrayList<Cliente> clientes, Integer id) {
        if (id == null) return null;
        for (Cliente c : clientes) if (c.getId() == id) return c;
        return null;
    }

    // ================== MENÚS ==================

    private static void mostrarMenuLogin() {
        System.out.println("============== SAKURA ENTERPRISES - LOGIN ==============");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse como cliente");
        System.out.println("3. Restablecer contraseña");
        System.out.println("4. Salir");
        System.out.println("========================================================");
    }

    private static void menuAdminContenido(Scanner sc, ArrayList<Producto> productos)
            throws InvalidProductException {
        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Administrador de Contenido ===");
            System.out.println("1. Ver productos");
            System.out.println("2. Crear producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Publicar producto (marcar)");
            System.out.println("5. Borrar producto");
            System.out.println("6. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> {
                    if (productos.isEmpty()) System.out.println("No hay productos.");
                    else for (Producto p : productos)
                        System.out.println("ID: " + p.getId() + " | " + p.getNombre() +
                                " | $" + p.getPrecio() + " | Stock: " + p.getStock());
                }
                case 2 -> {
                    System.out.print("ID: "); int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    System.out.print("Descripción: "); String desc = sc.nextLine();
                    System.out.print("Precio: "); double precio = Double.parseDouble(sc.nextLine());
                    System.out.print("Stock: "); int stock = Integer.parseInt(sc.nextLine());
                    System.out.print("Fecha lanzamiento: "); String fecha = sc.nextLine();
                    System.out.print("Categoría (nombre libre o existente): "); String cat = sc.nextLine();
                    // Nota: si no manejas categorías aún, dejamos id 0 y descripción vacía
                    productos.add(new Producto(id, nombre, desc, precio, stock, fecha, new Categoria(0, cat, "")));
                    System.out.println("✅ Producto creado.");
                }
                case 3 -> {
                    System.out.print("ID del producto: "); int idEdit = Integer.parseInt(sc.nextLine());
                    Producto prod = null;
                    for (Producto p : productos) if (p.getId() == idEdit) prod = p;
                    if (prod == null) System.out.println("⚠ Producto no encontrado.");
                    else {
                        System.out.print("Nuevo precio: "); prod.setPrecio(Double.parseDouble(sc.nextLine()));
                        System.out.print("Nuevo stock: ");  prod.setStock(Integer.parseInt(sc.nextLine()));
                        System.out.println("✅ Producto actualizado.");
                    }
                }
                case 4 -> {
                    System.out.print("ID del producto a publicar: "); int idPub = Integer.parseInt(sc.nextLine());
                    Producto prod = null;
                    for (Producto p : productos) if (p.getId() == idPub) prod = p;
                    if (prod == null) System.out.println("⚠ Producto no encontrado.");
                    else System.out.println("✅ Producto " + prod.getNombre() + " publicado (simulado).");
                }
                case 5 -> {
                    System.out.print("ID del producto a borrar: "); int idDel = Integer.parseInt(sc.nextLine());
                    boolean eliminado = productos.removeIf(p -> p.getId() == idDel);
                    System.out.println(eliminado ? "✅ Eliminado." : "⚠ No encontrado.");
                }
                case 6 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static void menuAdminUsuarios(Scanner sc,
                                          ArrayList<Credencial> usuarios,
                                          ArrayList<Cliente> clientes)
            throws InvalidClientOperationException {
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
                case 1 -> usuarios.forEach(u ->
                        System.out.println("Usuario: " + u.getUsername() + " | Rol: " + u.getRol()));
                case 2 -> {
                    System.out.print("Nuevo nombre de usuario: ");
                    String nuevoUser = sc.nextLine();
                    if (buscarUsuario(usuarios, nuevoUser) != null) {
                        System.out.println("⚠ Ese nombre de usuario ya existe."); break;
                    }
                    System.out.print("Email: "); String email = sc.nextLine();
                    System.out.print("Contraseña: "); String pass = sc.nextLine();
                    System.out.println("Rol (1=ADMIN_CONTENIDO, 2=ADMIN_USUARIOS, 3=CONSEJO, 4=SAKURA, 5=CLIENTE): ");
                    int r = Integer.parseInt(sc.nextLine());
                    String rol = switch (r) {
                        case 1 -> "ADMIN_CONTENIDO";
                        case 2 -> "ADMIN_USUARIOS";
                        case 3 -> "CONSEJO";
                        case 4 -> "SAKURA";
                        case 5 -> "CLIENTE";
                        default -> "";
                    };
                    if (rol.isEmpty()) { System.out.println("⚠ Rol inválido."); break; }

                    Credencial nuevo = new Credencial(nuevoUser, pass, rol, email);
                    if (rol.equals("CLIENTE")) {
                        System.out.println("Creación de datos de cliente asociado:");
                        System.out.print("ID cliente (numérico): "); int idC = Integer.parseInt(sc.nextLine());
                        System.out.print("Dirección: "); String dir = sc.nextLine();
                        System.out.print("Teléfono: "); String tel = sc.nextLine();
                        clientes.add(new Cliente(idC, dir, tel));
                        nuevo.setClienteId(idC);
                    }
                    usuarios.add(nuevo);
                    System.out.println("✅ Usuario creado.");
                }
                case 3 -> {
                    System.out.print("Nombre de usuario a eliminar: ");
                    String userDel = sc.nextLine();
                    Credencial u = buscarUsuario(usuarios, userDel);
                    if (u == null) { System.out.println("⚠ Usuario no encontrado."); break; }
                    if ("SAKURA".equals(u.getRol())) { System.out.println("⚠ No se puede eliminar a Sakura."); break; }
                    if (u.getClienteId() != null) clientes.removeIf(cl -> cl.getId() == u.getClienteId());
                    usuarios.remove(u);
                    System.out.println("✅ Usuario eliminado.");
                }
                case 4 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static void menuCliente(Scanner sc,
                                    ArrayList<Producto> productos,
                                    ArrayList<Cliente> clientes,
                                    ArrayList<Compra> compras,
                                    ArrayList<LineaCarrito> carrito,
                                    Credencial usuarioActual,
                                    Cliente clienteInicial)
            throws InvalidClientOperationException, EmptyCartException
    {
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
                    if (clientes.isEmpty()) { System.out.println("⚠ No hay clientes."); break; }
                    System.out.println("=== Seleccionar cliente activo ===");
                    for (Cliente c : clientes) System.out.println("ID: " + c.getId() + " | " + c.getTelefono());
                    System.out.print("ID del cliente: "); int idSel = Integer.parseInt(sc.nextLine());
                    clienteActual = buscarClientePorId(clientes, idSel);
                    System.out.println(clienteActual != null ? "Cliente activo: " + clienteActual.getId() : "⚠ Cliente no encontrado.");
                }
                case 2 -> {
                    if (clienteActual == null) { System.out.println("Seleccione cliente antes de continuar."); break; }
                    if (productos.isEmpty()) { System.out.println("⚠ No hay productos disponibles."); break; }
                    System.out.print("ID producto: "); int idP = Integer.parseInt(sc.nextLine());
                    System.out.print("Cantidad: "); int cant = Integer.parseInt(sc.nextLine());
                    Producto prod = null;
                    for (Producto p : productos) if (p.getId() == idP) prod = p;
                    if (prod == null) { System.out.println("⚠ Producto no encontrado."); break; }
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
                    if (carrito.isEmpty()) { System.out.println("⚠ Carrito vacío."); break; }
                    System.out.print("ID producto a eliminar: "); int idElim = Integer.parseInt(sc.nextLine());
                    boolean removed = carrito.removeIf(l -> l.getProducto().getId() == idElim);
                    System.out.println(removed ? "✅ Eliminado del carrito." : "⚠ No estaba en el carrito.");
                }
                case 5 -> {
                    if (clienteActual == null) { System.out.println("Seleccione cliente antes de comprar."); break; }
                    if (carrito.isEmpty()) { System.out.println("⚠ Carrito vacío."); break; }
                    System.out.print("Método de pago: "); String metodo = sc.nextLine();

                    Compra compra = new Compra();
                    // Si no gestionas metodosPago en memoria, igual funciona; el CSV guardará el id
                    compra.setMetodoPago(new MetodoPago(1, metodo, "titular", "****0000"));
                    compra.setLineasDesdeCarrito(carrito);
                    compra.calcularTotal();
                    compra.setEstado("PAGADA");
                    compras.add(compra);
                    clienteActual.agregarCompra(compra);
                    carrito.clear();
                    System.out.println("✅ Compra registrada. Total: $" + compra.getTotal());
                }
                case 6 -> {
                    if (clienteActual == null) { System.out.println("Seleccione cliente."); break; }
                    if (clienteActual.getCompras() == null || clienteActual.getCompras().isEmpty()) {
                        System.out.println("Este cliente no tiene compras."); break;
                    }
                    for (Compra c : clienteActual.getCompras())
                        System.out.println("Compra total: $" + c.getTotal() + " | Estado: " + c.getEstado());
                }
                case 7 -> {
                    if (productos.isEmpty()) { System.out.println("⚠ No hay productos."); break; }
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
                            if (p.getCategoria() != null &&
                                    p.getCategoria().getNombre().toLowerCase().equals(catB))
                                System.out.println(p.getId() + " - " + p.getNombre());
                    }
                }
                case 8 -> {
                    System.out.print("ID producto: "); int idDet = Integer.parseInt(sc.nextLine());
                    for (Producto p : productos)
                        if (p.getId() == idDet) {
                            System.out.println("Nombre: " + p.getNombre());
                            System.out.println("Descripción: " + p.getDescripcion());
                            System.out.println("Precio: $" + p.getPrecio());
                            System.out.println("Stock: " + p.getStock());
                            System.out.println("Categoría: " + (p.getCategoria() != null ? p.getCategoria().getNombre() : "(sin categoría)"));
                        }
                }
                case 9 -> {
                    if (clienteActual == null) { System.out.println("Seleccione cliente."); break; }
                    System.out.print("Nueva dirección: "); clienteActual.setDireccionEnvio(sc.nextLine());
                    System.out.print("Nuevo teléfono: ");  clienteActual.setTelefono(sc.nextLine());
                    System.out.println("✅ Cliente actualizado.");
                }
                case 10 -> {
                    System.out.print("Contraseña actual: "); String actual = sc.nextLine();
                    if (!usuarioActual.verificarPassword(actual)) { System.out.println("⚠ Contraseña incorrecta."); break; }
                    System.out.print("Nueva contraseña: "); String nueva = sc.nextLine();
                    usuarioActual.cambiarPassword(nueva);
                    System.out.println("✅ Contraseña actualizada.");
                }
                case 11 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static void menuSakura(Scanner sc,
                                   ArrayList<Credencial> usuarios,
                                   ArrayList<Producto> productos,
                                   ArrayList<Cliente> clientes,
                                   ArrayList<Compra> compras,
                                   ArrayList<LineaCarrito> carrito,
                                   ArrayList<Fabrica> fabricaList,
                                   ArrayList<TrabajadorEsclavizado> registroConfidencial)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException
    {
        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú de Sakura (Dueña) ===");
            System.out.println("1. Menú Administrador de Contenido");
            System.out.println("2. Menú Administrador de Usuarios");
            System.out.println("3. Menú Cliente (ver como cliente)");
            System.out.println("4. Gestión básica de fábricas");
            System.out.println("5. Registrar trabajadores esclavizados");
            System.out.println("6. Ver registro confidencial de trabajadores");
            System.out.println("7. Volver al login");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> menuAdminContenido(sc, productos);
                case 2 -> menuAdminUsuarios(sc, usuarios, clientes);
                case 3 -> {
                    if (clientes.isEmpty()) { System.out.println("⚠ No hay clientes."); break; }
                    System.out.println("Seleccione cliente para ver como:");
                    for (Cliente c : clientes) System.out.println("ID: " + c.getId() + " | Tel: " + c.getTelefono());
                    System.out.print("ID cliente: "); int idCli = Integer.parseInt(sc.nextLine());
                    Cliente cliente = buscarClientePorId(clientes, idCli);
                    if (cliente == null) { System.out.println("⚠ Cliente no encontrado."); break; }
                    Credencial fake = new Credencial("sakura-como-cliente", "x", "CLIENTE", "sakura@sakura.com");
                    fake.setClienteId(cliente.getId());
                    menuCliente(sc, productos, clientes, compras, carrito, fake, cliente);
                }
                case 4 -> {
                    System.out.println("1. Registrar fábrica\n2. Ver fábricas");
                    int subF = Integer.parseInt(sc.nextLine());
                    if (subF == 1) {
                        System.out.print("ID: "); int idF = Integer.parseInt(sc.nextLine());
                        System.out.print("País: "); String pais = sc.nextLine();
                        System.out.print("Ciudad: "); String ciudad = sc.nextLine();
                        System.out.print("Capacidad: "); int cap = Integer.parseInt(sc.nextLine());
                        System.out.print("Nivel automatización: "); int auto = Integer.parseInt(sc.nextLine());
                        fabricaList.add(new Fabrica(idF, pais, ciudad, cap, auto));
                        System.out.println("✅ Fábrica registrada.");
                    } else for (Fabrica f : fabricaList)
                        System.out.println("ID: " + f.getId() + " | " + f.getCiudad() + " - " + f.getPais());
                }
                case 5 -> {
                    if (fabricaList.isEmpty()) { System.out.println("⚠ Registre fábricas primero."); break; }
                    System.out.print("ID trabajador: "); int idT = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: "); String nomT = sc.nextLine();
                    System.out.print("País origen: "); String paisO = sc.nextLine();
                    System.out.print("Edad: "); int edad = Integer.parseInt(sc.nextLine());
                    System.out.print("Salud: "); String salud = sc.nextLine();
                    System.out.println("Seleccione fábrica:");
                    for (Fabrica f : fabricaList) System.out.println(f.getId() + " - " + f.getCiudad());
                    int idFab = Integer.parseInt(sc.nextLine());
                    Fabrica fab = null;
                    for (Fabrica f : fabricaList) if (f.getId() == idFab) fab = f;
                    if (fab == null) { System.out.println("⚠ Fábrica no encontrada."); break; }
                    TrabajadorEsclavizado t = new TrabajadorEsclavizado(idT, nomT, paisO, edad, "Hoy", salud, true);
                    fab.asignarTrabajador(t);
                    registroConfidencial.add(t);
                    System.out.println("✅ Trabajador asignado y registrado confidencialmente.");
                }
                case 6 -> {
                    if (registroConfidencial.isEmpty()) { System.out.println("No hay trabajadores registrados."); break; }
                    System.out.println("=== Registro confidencial de trabajadores ===");
                    for (TrabajadorEsclavizado t : registroConfidencial)
                        System.out.println("ID: " + t.getId() + " | Nombre: " + t.getNombre());
                }
                case 7 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static void menuConsejo(Scanner sc,
                                    ArrayList<Credencial> usuarios,
                                    ArrayList<Producto> productos,
                                    ArrayList<Cliente> clientes,
                                    ArrayList<Compra> compras,
                                    ArrayList<LineaCarrito> carrito,
                                    ArrayList<Fabrica> fabricaList,
                                    ArrayList<TrabajadorEsclavizado> registroConfidencial)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException
    {
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
                    if (sakura == null) { System.out.println("⚠ No existe el usuario Sakura."); break; }
                    System.out.print("Contraseña de Sakura: "); String pass = sc.nextLine();
                    if (!sakura.verificarPassword(pass)) { System.out.println("⚠ Contraseña incorrecta."); break; }
                    menuSakura(sc, usuarios, productos, clientes, compras, carrito, fabricaList, registroConfidencial);
                }
                case 4 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException, DataPersistenceException
    {
        Scanner sc = new Scanner(System.in);

        // Estado en memoria
        ArrayList<Categoria> categorias;
        ArrayList<Producto> productos;
        ArrayList<Cliente> clientes;
        ArrayList<MetodoPago> metodosPago;
        ArrayList<Compra> compras;
        ArrayList<LineaCarrito> carrito = new ArrayList<>();
        ArrayList<Fabrica> fabricaList = new ArrayList<>();
        ArrayList<TrabajadorEsclavizado> registroConfidencial = new ArrayList<>();
        ArrayList<Credencial> usuarios = new ArrayList<>();

        // Cargar todo desde PersistenceService
        EstadoPersistente st = cargarTodo();
        categorias  = st.categorias;
        productos   = st.productos;
        clientes    = st.clientes;
        metodosPago = st.metodosPago;
        compras     = st.compras;

        inicializarUsuariosPorDefecto(usuarios);

        boolean salir = false;
        while (!salir) {
            mostrarMenuLogin();
            System.out.print("→ Opción: ");
            int opcionLogin = Integer.parseInt(sc.nextLine());

            switch (opcionLogin) {
                case 1 -> {
                    System.out.print("Usuario: "); String user = sc.nextLine();
                    System.out.print("Contraseña: "); String pass = sc.nextLine();
                    Credencial u = buscarUsuario(usuarios, user);
                    if (u == null || !u.verificarPassword(pass)) {
                        System.out.println("⚠ Usuario o contraseña incorrectos."); break;
                    }
                    switch (u.getRol()) {
                        case "ADMIN_CONTENIDO" -> menuAdminContenido(sc, productos);
                        case "ADMIN_USUARIOS" -> menuAdminUsuarios(sc, usuarios, clientes);
                        case "CONSEJO" -> menuConsejo(sc, usuarios, productos, clientes, compras, carrito, fabricaList, registroConfidencial);
                        case "SAKURA" -> menuSakura(sc, usuarios, productos, clientes, compras, carrito, fabricaList, registroConfidencial);
                        case "CLIENTE" -> {
                            Cliente cli = buscarClientePorId(clientes, u.getClienteId());
                            if (cli == null) { System.out.println("⚠ Cliente asociado no encontrado."); break; }
                            menuCliente(sc, productos, clientes, compras, carrito, u, cli);
                        }
                        default -> System.out.println("⚠ Rol desconocido.");
                    }
                }
                case 2 -> {
                    System.out.print("Elija nombre de usuario: ");
                    String nuevoUser = sc.nextLine();
                    if (buscarUsuario(usuarios, nuevoUser) != null) { System.out.println("⚠ Ese usuario ya existe."); break; }
                    System.out.print("Email: "); String email = sc.nextLine();
                    System.out.print("Contraseña: "); String pass = sc.nextLine();

                    System.out.println("Datos del cliente asociado:");
                    System.out.print("ID (número): "); int idC = Integer.parseInt(sc.nextLine());
                    System.out.print("Dirección: "); String dir = sc.nextLine();
                    System.out.print("Teléfono: "); String tel = sc.nextLine();
                    clientes.add(new Cliente(idC, dir, tel));

                    Credencial cred = new Credencial(nuevoUser, pass, "CLIENTE", email);
                    cred.setClienteId(idC);
                    usuarios.add(cred);

                    System.out.println("✅ Usuario cliente registrado.");
                }
                case 3 -> {
                    System.out.print("Nombre de usuario: "); String user = sc.nextLine();
                    Credencial u = buscarUsuario(usuarios, user);
                    if (u == null) { System.out.println("⚠ Usuario no encontrado."); break; }
                    System.out.print("Email registrado: "); String email = sc.nextLine();
                    if (!u.getEmail().equalsIgnoreCase(email)) { System.out.println("⚠ El email no coincide."); break; }
                    System.out.print("Nueva contraseña: "); String nueva = sc.nextLine();
                    u.cambiarPassword(nueva);
                    System.out.println("✅ Contraseña restablecida.");
                }
                case 4 -> {
                    System.out.println("Guardando cambios y saliendo...");
                    // Actualizar estado persistente con las listas actuales
                    st.categorias  = categorias;
                    st.productos   = productos;
                    st.clientes    = clientes;
                    st.metodosPago = metodosPago; // si no manejaste ninguno, estará vacío
                    st.compras     = compras;

                    guardarTodo(st);
                    salir = true;
                }
                default -> System.out.println("⚠ Opción inválida.");
            }
        }

        sc.close();
        System.out.println(" Gracias por usar Sakura Enterprises ");
    }
}
