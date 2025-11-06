import Exceptions.DataPersistenceException;
import Exceptions.EmptyCartException;
import Exceptions.InvalidClientOperationException;
import Exceptions.InvalidProductException;

import model.*;
import persistence.DataManager;

import java.util.*;

/**
 * Main “puro” con persistencia por CSV:
 * - Carga todo al iniciar y opera en memoria.
 * - Inicializa TODOS los CSV con header si no existen.
 * - Guarda TODO al presionar “Salir”.
 * - Login por email y passwordHash (texto plano por ahora).
 * - Incluye seed de credenciales base (se crean sólo si no existen).
 */
public class Main {

    // ================== PERSISTENCIA ==================
    private static final DataManager DM = new DataManager();

    private static class EstadoPersistente {
        // Dominio “tienda”
        ArrayList<Categoria> categorias = new ArrayList<>();
        ArrayList<Producto> productos = new ArrayList<>();
        ArrayList<Cliente> clientes = new ArrayList<>();
        ArrayList<MetodoPago> metodosPago = new ArrayList<>();
        ArrayList<Compra> compras = new ArrayList<>();

        // Otros módulos
        ArrayList<Fabrica> fabricas = new ArrayList<>();
        ArrayList<TrabajadorEsclavizado> trabajadores = new ArrayList<>();

        // Usuarios no-cliente
        ArrayList<AdministradorContenido> adminsContenido = new ArrayList<>();
        ArrayList<AdministradorUsuario> adminsUsuario = new ArrayList<>();
        ArrayList<DesarrolladorProducto> desarrolladores = new ArrayList<>();

        // ConsejoSombrio NO es Usuario; se guarda aparte si lo usas en otro flujo
        ArrayList<ConsejoSombrio> consejos = new ArrayList<>();
    }

    // ---------- util ----------
    private static int nextUsuarioId(EstadoPersistente st) {
        int max = 0;
        for (AdministradorContenido u : st.adminsContenido) max = Math.max(max, u.getId());
        for (AdministradorUsuario u : st.adminsUsuario)   max = Math.max(max, u.getId());
        for (DesarrolladorProducto u : st.desarrolladores) max = Math.max(max, u.getId());
        for (Cliente u : st.clientes)                      max = Math.max(max, u.getId());
        return max + 1;
    }

    private static boolean emailExiste(EstadoPersistente st, String email) {
        String e = email.trim().toLowerCase();
        for (AdministradorContenido u : st.adminsContenido) if (u.getEmail().equalsIgnoreCase(e)) return true;
        for (AdministradorUsuario u : st.adminsUsuario)     if (u.getEmail().equalsIgnoreCase(e)) return true;
        for (DesarrolladorProducto u : st.desarrolladores)  if (u.getEmail().equalsIgnoreCase(e)) return true;
        for (Cliente u : st.clientes)                       if (u.getEmail().equalsIgnoreCase(e)) return true;
        return false;
    }

    private static void seedCredencialesBase(EstadoPersistente st) {
        // Se crean SOLO si NO existen ya por email (así quedan en CSV al salir)
        record Seed(String email, String pass, String rol, String nombre) {}
        List<Seed> base = List.of(
                new Seed("contenido@sakura.com", "1234", "ADMIN_CONTENIDO", "adminContenido"),
                new Seed("usuarios@sakura.com",  "1234", "ADMIN_USUARIOS",  "adminUsuarios"),
                new Seed("consejo@sakura.com",   "1234", "CONSEJO",         "consejo"),
                new Seed("sakura@sakura.com",    "9999", "SAKURA",          "Sakura")
        );

        for (Seed s : base) {
            if (emailExiste(st, s.email)) continue;
            int id = nextUsuarioId(st);
            switch (s.rol) {
                case "ADMIN_CONTENIDO" -> st.adminsContenido.add(
                        new AdministradorContenido(id, s.nombre, s.email, s.pass, s.rol, "", true)
                );
                case "ADMIN_USUARIOS" -> st.adminsUsuario.add(
                        new AdministradorUsuario(id, s.nombre, s.email, s.pass, s.rol, "", true, /*nivel*/1)
                );
                // Para poder loguear CONSEJO y SAKURA como Usuario, usamos AdministradorUsuario con ese rol.
                case "CONSEJO" -> st.adminsUsuario.add(
                        new AdministradorUsuario(id, s.nombre, s.email, s.pass, s.rol, "", true, 9)
                );
                case "SAKURA" -> st.adminsUsuario.add(
                        new AdministradorUsuario(id, s.nombre, s.email, s.pass, s.rol, "", true, 99)
                );
            }
        }
    }

    private static EstadoPersistente cargarTodo() throws DataPersistenceException {
        EstadoPersistente st = new EstadoPersistente();

        // crea headers de TODOS los csv si no existen
        bootstrapCrearArchivos();

        // tienda
        st.categorias.addAll(DM.loadCategorias());
        st.productos.addAll(DM.loadProductos());
        st.clientes.addAll(DM.loadClientes());
        st.metodosPago.addAll(DM.loadMetodosPago());
        st.compras.addAll(DM.loadCompras());

        // otros módulos
        st.fabricas.addAll(DM.loadFabricas());
        st.trabajadores.addAll(DM.loadTrabajadores());

        // usuarios no-cliente
        st.adminsContenido.addAll(DM.loadAdminsContenido());
        st.adminsUsuario.addAll(DM.loadAdminsUsuario());
        st.desarrolladores.addAll(DM.loadDesarrolladores());
        st.consejos.addAll(DM.loadConsejo());

        // credenciales base (si hacen falta)
        seedCredencialesBase(st);

        System.out.println("📂 Datos cargados correctamente desde /data.");
        return st;
    }

    private static void guardarTodo(EstadoPersistente st) throws DataPersistenceException {
        // tienda
        DM.saveAllCategorias(st.categorias);
        DM.saveAllProductos(st.productos);
        DM.saveAllClientes(st.clientes);
        DM.saveAllMetodosPago(st.metodosPago);
        DM.saveAllCompras(st.compras);

        // otros módulos
        DM.saveAllFabricas(st.fabricas);
        DM.saveAllTrabajadores(st.trabajadores);

        // usuarios no-cliente
        DM.saveAllAdminsContenido(st.adminsContenido);
        DM.saveAllAdminsUsuario(st.adminsUsuario);
        DM.saveAllDesarrolladores(st.desarrolladores);
        DM.saveAllConsejos(st.consejos);

        System.out.println("💾 Datos guardados correctamente en /data.");
    }

    // Inicializa TODOS los CSV con sus headers (se ejecuta una vez al inicio)
    private static void bootstrapCrearArchivos() {
        try {
            // tienda
            DM.loadCategorias();
            DM.loadProductos();
            DM.loadClientes();
            DM.loadMetodosPago();
            DM.loadCompras();
            DM.loadLineasCompraPorCompraId();

            // otros módulos
            DM.loadFabricas();
            DM.loadTrabajadores();

            // usuarios no-cliente
            DM.loadAdminsContenido();
            DM.loadAdminsUsuario();
            DM.loadDesarrolladores();
            DM.loadConsejo();

            // relaciones auxiliares
            DM.loadConsejoMiembros(id -> null);
            DM.loadRegistrosEsclavos();
            DM.loadRegistroTrabajadores(id -> null);

            System.out.println("✅ Archivos CSV inicializados (headers).");
        } catch (DataPersistenceException e) {
            System.out.println("⚠ No se pudieron inicializar todos los CSV: " + e.getMessage());
        }
    }

    // ================== LOGIN ==================
    private static Usuario encontrarUsuarioPorEmail(String email,
                                                    List<AdministradorContenido> ac,
                                                    List<AdministradorUsuario> au,
                                                    List<DesarrolladorProducto> devs,
                                                    List<Cliente> clientes) {
        String key = email.trim().toLowerCase();
        for (AdministradorContenido u : ac) if (u.getEmail().equalsIgnoreCase(key)) return u;
        for (AdministradorUsuario u : au) if (u.getEmail().equalsIgnoreCase(key)) return u;
        for (DesarrolladorProducto u : devs) if (u.getEmail().equalsIgnoreCase(key)) return u;
        for (Cliente u : clientes) if (u.getEmail().equalsIgnoreCase(key)) return u;
        return null;
    }

    private static void mostrarMenuLogin() {
        System.out.println("============== GLOW UP LOGIN ==============");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse como cliente");
        System.out.println("3. Restablecer contraseña (por email)");
        System.out.println("4. Salir");
        System.out.println("==========================================");
    }

    // ================== MENÚS ==================
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

    private static void menuAdminUsuarios(Scanner sc, EstadoPersistente st)
            throws InvalidClientOperationException {
        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Administrador de Usuarios ===");
            System.out.println("1. Ver usuarios (todos)");
            System.out.println("2. Crear usuario");
            System.out.println("3. Eliminar usuario (por email)");
            System.out.println("4. Volver");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> {
                    System.out.println("-- ADM CONTENIDO --");
                    for (AdministradorContenido u : st.adminsContenido)
                        System.out.println(u.getId() + " | " + u.getEmail() + " | " + u.getRol());
                    System.out.println("-- ADM USUARIO --");
                    for (AdministradorUsuario u : st.adminsUsuario)
                        System.out.println(u.getId() + " | " + u.getEmail() + " | " + u.getRol());
                    System.out.println("-- DESARROLLADORES --");
                    for (DesarrolladorProducto u : st.desarrolladores)
                        System.out.println(u.getId() + " | " + u.getEmail() + " | " + u.getRol());
                    System.out.println("-- CLIENTES --");
                    for (Cliente u : st.clientes)
                        System.out.println(u.getId() + " | " + u.getEmail() + " | CLIENTE");
                }
                case 2 -> {
                    System.out.println("Tipo: 1) Admin Contenido  2) Admin Usuario  3) Desarrollador  4) Cliente");
                    int tipo = Integer.parseInt(sc.nextLine());
                    System.out.print("ID: "); int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    System.out.print("Email (login): "); String email = sc.nextLine();

                    if (emailExiste(st, email)) {
                        System.out.println("⚠ Ya existe un usuario con ese email."); break;
                    }

                    System.out.print("Contraseña (se guarda tal cual en passwordHash): "); String pass = sc.nextLine();
                    String fecha = ""; boolean estado = true;

                    if (tipo == 1) {
                        String rol = "ADMIN_CONTENIDO";
                        st.adminsContenido.add(new AdministradorContenido(id, nombre, email, pass, rol, fecha, estado));
                    } else if (tipo == 2) {
                        String rol = "ADMIN_USUARIOS";
                        System.out.print("Nivel de acceso (int): "); int lvl = Integer.parseInt(sc.nextLine());
                        st.adminsUsuario.add(new AdministradorUsuario(id, nombre, email, pass, rol, fecha, estado, lvl));
                    } else if (tipo == 3) {
                        String rol = "DESARROLLADOR";
                        st.desarrolladores.add(new DesarrolladorProducto(id, nombre, email, pass, rol, fecha, estado));
                    } else if (tipo == 4) {
                        String rol = "CLIENTE";
                        System.out.print("Dirección: "); String dir = sc.nextLine();
                        System.out.print("Teléfono: ");  String tel = sc.nextLine();
                        st.clientes.add(new Cliente(id, nombre, email, pass, rol, fecha, estado, dir, tel));
                    } else {
                        System.out.println("⚠ Tipo inválido.");
                    }
                    System.out.println("✅ Usuario creado en memoria.");
                }
                case 3 -> {
                    System.out.print("Email del usuario a eliminar: ");
                    String email = sc.nextLine().trim().toLowerCase();

                    boolean removed = st.adminsContenido.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
                    removed |= st.adminsUsuario.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
                    removed |= st.desarrolladores.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
                    removed |= st.clientes.removeIf(u -> u.getEmail().equalsIgnoreCase(email));

                    System.out.println(removed ? "✅ Eliminado de memoria." : "⚠ No encontrado.");
                }
                case 4 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static Cliente buscarClientePorId(ArrayList<Cliente> clientes, Integer id) {
        if (id == null) return null;
        for (Cliente c : clientes) if (c.getId() == id) return c;
        return null;
    }

    private static void menuCliente(Scanner sc,
                                    ArrayList<Producto> productos,
                                    ArrayList<Cliente> clientes,
                                    ArrayList<Compra> compras,
                                    ArrayList<LineaCarrito> carrito,
                                    Cliente clienteInicial)
            throws InvalidClientOperationException, EmptyCartException {
        Cliente clienteActual = clienteInicial;

        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Cliente (" + clienteActual.getNombre() + ") ===");
            System.out.println("1. Seleccionar cliente activo");
            System.out.println("2. Agregar producto al carrito");
            System.out.println("3. Ver carrito");
            System.out.println("4. Eliminar producto del carrito");
            System.out.println("5. Realizar compra");
            System.out.println("6. Ver historial de compras");
            System.out.println("7. Buscar producto por nombre o categoría");
            System.out.println("8. Ver detalles de un producto");
            System.out.println("9. Editar datos del cliente");
            System.out.println("10. Cambiar contraseña");
            System.out.println("11. Volver");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> {
                    if (clientes.isEmpty()) { System.out.println("⚠ No hay clientes."); break; }
                    System.out.println("=== Seleccionar cliente activo ===");
                    for (Cliente c : clientes)
                        System.out.println("ID: " + c.getId() + " | " + c.getEmail() + " | " + c.getTelefono());
                    System.out.print("ID del cliente: "); int idSel = Integer.parseInt(sc.nextLine());
                    clienteActual = buscarClientePorId(clientes, idSel);
                    System.out.println(clienteActual != null ? "Cliente activo: " + clienteActual.getEmail()
                            : "⚠ Cliente no encontrado.");
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
                    System.out.print("Método de pago (texto): "); String metodo = sc.nextLine();
                    Compra compra = new Compra();
                    compra.setMetodoPago(new MetodoPago(1, metodo, "titular", "**0000"));
                    compra.setLineasDesdeCarrito(carrito);
                    compra.calcularTotal();
                    compra.setEstado("PAGADA");
                    compras.add(compra);
                    clienteActual.agregarCompra(compra);
                    carrito.clear();
                    System.out.println("✅ Compra registrada. Total: $" + compra.getTotal());
                }
                case 6 -> {
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
                            System.out.println("Categoría: " + (p.getCategoria() != null
                                    ? p.getCategoria().getNombre()
                                    : "(sin categoría)"));
                        }
                }
                case 9 -> {
                    System.out.print("Nueva dirección: "); clienteActual.setDireccionEnvio(sc.nextLine());
                    System.out.print("Nuevo teléfono: ");  clienteActual.setTelefono(sc.nextLine());
                    System.out.println("✅ Cliente actualizado en memoria.");
                }
                case 10 -> {
                    System.out.print("Contraseña actual: "); String actual = sc.nextLine();
                    if (!Objects.equals(clienteActual.getPasswordHash(), actual)) {
                        System.out.println("⚠ Contraseña incorrecta."); break;
                    }
                    System.out.print("Nueva contraseña: "); String nueva = sc.nextLine();
                    try {
                        var f = Usuario.class.getDeclaredField("passwordHash");
                        f.setAccessible(true);
                        f.set(clienteActual, nueva);
                    } catch (Exception ignore) {}
                    System.out.println("✅ Contraseña actualizada en memoria.");
                }
                case 11 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static void menuSakura(Scanner sc,
                                   EstadoPersistente st,
                                   ArrayList<Producto> productos,
                                   ArrayList<Compra> compras,
                                   ArrayList<LineaCarrito> carrito)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException {
        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú de Sakura (Dueña) ===");
            System.out.println("1. Menú Administrador de Contenido");
            System.out.println("2. Menú Administrador de Usuarios");
            System.out.println("3. Menú Cliente (ver como cliente)");
            System.out.println("4. Gestión básica de fábricas");
            System.out.println("5. Registrar trabajadores esclavizados");
            System.out.println("6. Ver registro confidencial de trabajadores");
            System.out.println("7. Volver");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> menuAdminContenido(sc, productos);
                case 2 -> menuAdminUsuarios(sc, st);
                case 3 -> {
                    if (st.clientes.isEmpty()) { System.out.println("⚠ No hay clientes."); break; }
                    System.out.println("Seleccione cliente para ver como:");
                    for (Cliente c : st.clientes)
                        System.out.println("ID: " + c.getId() + " | " + c.getEmail());
                    System.out.print("ID cliente: "); int idCli = Integer.parseInt(sc.nextLine());
                    Cliente cliente = buscarClientePorId(st.clientes, idCli);
                    if (cliente == null) { System.out.println("⚠ Cliente no encontrado."); break; }
                    menuCliente(sc, productos, st.clientes, compras, carrito, cliente);
                }
                case 4 -> {
                    System.out.println("1. Registrar fábrica  2. Ver fábricas");
                    int subF = Integer.parseInt(sc.nextLine());
                    if (subF == 1) {
                        System.out.print("ID: "); int idF = Integer.parseInt(sc.nextLine());
                        System.out.print("País: "); String pais = sc.nextLine();
                        System.out.print("Ciudad: "); String ciudad = sc.nextLine();
                        System.out.print("Capacidad: "); int cap = Integer.parseInt(sc.nextLine());
                        System.out.print("Nivel automatización: "); int auto = Integer.parseInt(sc.nextLine());
                        st.fabricas.add(new Fabrica(idF, pais, ciudad, cap, auto));
                        System.out.println("Fábrica registrada (memoria).");
                    } else {
                        if (st.fabricas.isEmpty()) {
                            System.out.println("No hay fábricas registradas."); // por si no
                        } else {
                            for (Fabrica f : st.fabricas)
                                System.out.println("ID: " + f.getId() + " | " + f.getCiudad() + " - " + f.getPais());
                        }
                    }
                }
                case 5 -> {
                    if (st.fabricas.isEmpty()) { System.out.println("⚠ Registre fábricas primero."); break; }
                    System.out.print("ID trabajador: "); int idT = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: "); String nomT = sc.nextLine();
                    System.out.print("País origen: "); String paisO = sc.nextLine();
                    System.out.print("Edad: "); int edad = Integer.parseInt(sc.nextLine());
                    System.out.print("Salud: "); String salud = sc.nextLine();
                    System.out.println("Seleccione fábrica (id):");
                    for (Fabrica f : st.fabricas) System.out.println(f.getId() + " - " + f.getCiudad());
                    int idFab = Integer.parseInt(sc.nextLine());
                    Fabrica fab = null;
                    for (Fabrica f : st.fabricas) if (f.getId() == idFab) fab = f;
                    if (fab == null) { System.out.println("⚠ Fábrica no encontrada."); break; }
                    TrabajadorEsclavizado t = new TrabajadorEsclavizado(idT, nomT, paisO, edad, "Hoy", salud, true);
                    fab.asignarTrabajador(t);
                    st.trabajadores.add(t);
                    // CAMBIO: confirmar indicando en qué fábrica trabaja
                    System.out.println("Trabajador asignado. ID fabrica "
                            + fab.getId() + "  " + fab.getCiudad() + " " + fab.getPais() + " ");
                }
                case 6 -> {
                    if (st.trabajadores.isEmpty()) { System.out.println("No hay trabajadores registrados."); break; }
                    System.out.println("=== Registro confidencial de trabajadores ===");
                    for (TrabajadorEsclavizado t : st.trabajadores) {
                        Fabrica f = buscarFabricaDeTrabajador(st, t);
                        String etiquetaFab = (f == null)
                                ? "(sin fábrica asignada)"
                                : ("Fábrica ID " + f.getId() + "  " + f.getCiudad() + " " + f.getPais() + "");
                        System.out.println("ID: " + t.getId() + "  Nombre: " + t.getNombre()
                                + "  " + etiquetaFab);
                    }
                }
                case 7 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    private static void menuConsejo(Scanner sc,
                                    EstadoPersistente st,
                                    ArrayList<Producto> productos,
                                    ArrayList<Compra> compras,
                                    ArrayList<LineaCarrito> carrito)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException {
        boolean seguir = true;
        while (seguir) {
            System.out.println("=== Menú Consejo Sombrío ===");
            System.out.println("1. Menú Administrador de Contenido");
            System.out.println("2. Menú Administrador de Usuarios");
            System.out.println("3. Ingresar como Sakura");
            System.out.println("4. Volver");
            System.out.print("→ Opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> menuAdminContenido(sc, productos);
                case 2 -> menuAdminUsuarios(sc, st);
                case 3 -> {
                    Usuario sakura = encontrarUsuarioPorEmail("sakura@sakura.com",
                            st.adminsContenido, st.adminsUsuario, st.desarrolladores, st.clientes);
                    if (sakura == null) { System.out.println("⚠ No existe Sakura."); break; }
                    System.out.print("Contraseña de Sakura: "); String pass = sc.nextLine();
                    if (!Objects.equals(sakura.getPasswordHash(), pass)) { System.out.println("⚠ Contraseña incorrecta."); break; }
                    menuSakura(sc, st, productos, compras, carrito);
                }
                case 4 -> seguir = false;
                default -> System.out.println("⚠ Opción inválida.");
            }
        }
    }

    // busqueda
    private static Fabrica buscarFabricaDeTrabajador(EstadoPersistente st, TrabajadorEsclavizado t) {
        if (t == null) return null;
        for (Fabrica f : st.fabricas) {
            try {
                List<TrabajadorEsclavizado> lista = f.getTrabajadores();
                if (lista == null) continue;
                for (TrabajadorEsclavizado tt : lista) {
                    if (tt != null && tt.getId() == t.getId()) return f;
                }
            } catch (Exception ignore) {

            }
        }
        return null;
    }


    public static void main(String[] args)
            throws InvalidProductException, InvalidClientOperationException, EmptyCartException, DataPersistenceException {

        Scanner sc = new Scanner(System.in);

        // Estado en memoria (carga + seed base si falta)
        EstadoPersistente st = cargarTodo();

        // Carrito (temporal en memoria)
        ArrayList<LineaCarrito> carrito = new ArrayList<>();

        boolean salir = false;
        while (!salir) {
            mostrarMenuLogin();
            System.out.print("→ Opción: ");
            int opcionLogin = Integer.parseInt(sc.nextLine());

            switch (opcionLogin) {
                case 1 -> {
                    System.out.print("Email: "); String email = sc.nextLine();
                    System.out.print("Contraseña: "); String pass = sc.nextLine();

                    Usuario u = encontrarUsuarioPorEmail(email, st.adminsContenido, st.adminsUsuario, st.desarrolladores, st.clientes);
                    if (u == null || !Objects.equals(u.getPasswordHash(), pass)) {
                        System.out.println("⚠ Usuario o contraseña incorrectos."); break;
                    }
                    String rol = u.getRol();
                    if ("ADMIN_CONTENIDO".equalsIgnoreCase(rol)) {
                        menuAdminContenido(sc, st.productos);
                    } else if ("ADMIN_USUARIOS".equalsIgnoreCase(rol)) {
                        menuAdminUsuarios(sc, st);
                    } else if ("DESARROLLADOR".equalsIgnoreCase(rol)) {
                        menuAdminContenido(sc, st.productos);
                    } else if ("CONSEJO".equalsIgnoreCase(rol)) {
                        menuConsejo(sc, st, st.productos, st.compras, carrito);
                    } else if ("SAKURA".equalsIgnoreCase(rol)) {
                        menuSakura(sc, st, st.productos, st.compras, carrito);
                    } else if ("CLIENTE".equalsIgnoreCase(rol)) {
                        Cliente cli = (u instanceof Cliente c) ? c : null;
                        if (cli == null) {
                            for (Cliente c : st.clientes) if (c.getEmail().equalsIgnoreCase(email)) { cli = c; break; }
                        }
                        if (cli == null) { System.out.println("⚠ Cliente asociado no encontrado."); break; }
                        menuCliente(sc, st.productos, st.clientes, st.compras, carrito, cli);
                    } else {
                        System.out.println("⚠ Rol desconocido: " + rol);
                    }
                }
                case 2 -> {
                    System.out.println("=== Registro de nuevo cliente ===");
                    System.out.print("ID (número): "); int idC = Integer.parseInt(sc.nextLine());
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    System.out.print("Email (login): "); String email = sc.nextLine();

                    if (emailExiste(st, email)) {
                        System.out.println("⚠ Ya existe un usuario con ese email."); break;
                    }

                    System.out.print("Contraseña: "); String pass = sc.nextLine();
                    System.out.print("Dirección: "); String dir = sc.nextLine();
                    System.out.print("Teléfono: "); String tel = sc.nextLine();

                    Cliente c = new Cliente(idC, nombre, email, pass, "CLIENTE", "", true, dir, tel);
                    st.clientes.add(c);

                    System.out.println("✅ Cliente registrado en memoria (se guardará al salir).");
                }
                case 3 -> {
                    System.out.print("Email: "); String email = sc.nextLine();
                    Usuario u = encontrarUsuarioPorEmail(email, st.adminsContenido, st.adminsUsuario, st.desarrolladores, st.clientes);
                    if (u == null) { System.out.println("⚠ Usuario no encontrado."); break; }
                    System.out.print("Nueva contraseña: "); String nueva = sc.nextLine();
                    try {
                        var f = Usuario.class.getDeclaredField("passwordHash");
                        f.setAccessible(true);
                        f.set(u, nueva);
                    } catch (Exception ignore) {}
                    System.out.println("✅ Contraseña actualizada en memoria.");
                }
                case 4 -> {
                    System.out.println("Guardando cambios y saliendo...");
                    guardarTodo(st);
                    salir = true;
                }
                default -> System.out.println("⚠ Opción inválida.");
            }
        }

        sc.close();
        System.out.println("Gracias por usar Sakura Enterprises.");
    }
}
