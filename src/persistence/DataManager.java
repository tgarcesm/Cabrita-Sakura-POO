package persistence;

import Exceptions.DataPersistenceException;
import Exceptions.InvalidClientOperationException;
import model.*;
import persistence.csv.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataManager {

    // --- rutas base ---
    private static final Path DATA_DIR               = Paths.get("data");
    private static final Path FILE_CATEGORIAS        = DATA_DIR.resolve("categororias.csv".replace("ror", "rio")); // → "categorias.csv"
    private static final Path FILE_PRODUCTOS         = DATA_DIR.resolve("productos.csv");
    private static final Path FILE_CLIENTES          = DATA_DIR.resolve("clientes.csv");
    private static final Path FILE_METODOS_PAGO      = DATA_DIR.resolve("metodos_pago.csv");
    private static final Path FILE_COMPRAS           = DATA_DIR.resolve("compras.csv");
    private static final Path FILE_LINEAS_COMPRA     = DATA_DIR.resolve("lineas_compra.csv");
    private static final Path FILE_FABRICAS          = DATA_DIR.resolve("fabricas.csv");
    private static final Path FILE_TRABAJADORES      = DATA_DIR.resolve("trabajadores.csv");
    private static final Path FILE_ADMIN_CONTENIDO   = DATA_DIR.resolve("admin_contenido.csv");
    private static final Path FILE_ADMIN_USUARIO     = DATA_DIR.resolve("admin_usuario.csv");
    private static final Path FILE_DESARROLLADORES   = DATA_DIR.resolve("desarrolladores.csv");
    private static final Path FILE_CONSEJO_SOMBRIO   = DATA_DIR.resolve("consejo_sombrio.csv");
    private static final Path FILE_CONSEJO_MIEMBROS  = DATA_DIR.resolve("consejo_miembros.csv");
    private static final Path FILE_REGISTRO_ESCLAVOS = DATA_DIR.resolve("registro_esclavos.csv");
    private static final Path FILE_REGISTRO_TRAB     = DATA_DIR.resolve("registro_trabajadores.csv");

    // === Constructor: asegura headers/archivos ===
    public DataManager() {
        try {
            ensureAllFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= util IO =================

    public void ensureAllFiles() throws IOException {
        Files.createDirectories(DATA_DIR);

        ensureFileWithHeader(FILE_CATEGORIAS,        CategoriaCsv.header());
        ensureFileWithHeader(FILE_PRODUCTOS,         ProductoCsv.header());
        ensureFileWithHeader(FILE_CLIENTES,          ClienteCsv.header());
        ensureFileWithHeader(FILE_METODOS_PAGO,      MetodoPagoCsv.header());
        ensureFileWithHeader(FILE_COMPRAS,           CompraCsv.header());
        ensureFileWithHeader(FILE_LINEAS_COMPRA,     LineaCompraCsv.header());
        ensureFileWithHeader(FILE_FABRICAS,          FabricaCsv.header());
        ensureFileWithHeader(FILE_TRABAJADORES,      TrabajadorEsclavizadoCsv.header());
        ensureFileWithHeader(FILE_ADMIN_CONTENIDO,   AdministradorContenidoCsv.header());
        ensureFileWithHeader(FILE_ADMIN_USUARIO,     AdministradorUsuarioCsv.header());
        ensureFileWithHeader(FILE_DESARROLLADORES,   DesarrolladorProductoCsv.header());
        ensureFileWithHeader(FILE_CONSEJO_SOMBRIO,   ConsejoSombrioCsv.header());
        ensureFileWithHeader(FILE_CONSEJO_MIEMBROS,  ConsejoMiembrosCsv.header());
        ensureFileWithHeader(FILE_REGISTRO_ESCLAVOS, RegistroEsclavosCsv.header());
        ensureFileWithHeader(FILE_REGISTRO_TRAB,     RegistroTrabajadoresCsv.header());
    }

    private static void ensureFileWithHeader(Path file, String header) throws IOException {
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW)) {
                bw.write(header);
                bw.newLine();
            }
        }
    }

    private static boolean isEffectivelyEmptyCsvLine(String s) {
        if (s == null) return true;
        String t = stripBOM(s).trim();
        if (t.isEmpty()) return true;
        if ("<null>".equalsIgnoreCase(t)) return true; // IntelliJ CSV plugin placeholder
        String noPunct = t.replace(",", "").replace(";", "").replace("\"", "").trim();
        return noPunct.isEmpty();
    }

    private static String stripBOM(String s) {
        if (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
    }

    /** Si detecta separador ';' y no hay comas, las convierte a ',' para que *Csv.fromRow* parsee igual. */
    private static String normalizeSeparators(String s) {
        String t = stripBOM(s);
        if (t.indexOf(',') < 0 && t.indexOf(';') >= 0) {
            return t.replace(';', ',');
        }
        return t;
    }

    private static List<String> readAllLinesSkippingHeader(Path file) throws IOException {
        if (Files.notExists(file)) return List.of();
        List<String> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // header
                if (!isEffectivelyEmptyCsvLine(line)) {
                    out.add(normalizeSeparators(line));
                }
            }
        }
        return out;
    }

    private static void appendLine(Path file, String header, String row) throws IOException {
        ensureFileWithHeader(file, header);
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            bw.write(row);
            bw.newLine();
        }
    }

    private static void writeAll(Path file, String header, Collection<String> rows) throws IOException {
        Files.createDirectories(file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            bw.write(header);
            bw.newLine();
            for (String r : rows) {
                if (!isEffectivelyEmptyCsvLine(r)) {
                    bw.write(r);
                    bw.newLine();
                }
            }
        }
    }

    // helper genérico
    private static <T> void addIfParseable(List<T> out, java.util.function.Supplier<T> maker,
                                           String raw, String tag) {
        try {
            out.add(maker.get());
        } catch (Exception ex) {
            System.out.println("⚠ Fila inválida en " + tag + " (omitida): " + raw + "  → " + ex.getMessage());
        }
    }

    // ============ CATEGORIAS ============
    public List<Categoria> loadCategorias() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_CATEGORIAS, CategoriaCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_CATEGORIAS);
            List<Categoria> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> CategoriaCsv.fromRow(r), r, "categorias.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo categorias", e);
        }
    }

    public void saveCategoria(Categoria c) throws DataPersistenceException {
        try {
            appendLine(FILE_CATEGORIAS, CategoriaCsv.header(), CategoriaCsv.toRow(c));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando categoria", e);
        }
    }

    public void saveAllCategorias(Collection<Categoria> list) throws DataPersistenceException {
        try {
            writeAll(FILE_CATEGORIAS, CategoriaCsv.header(),
                    list.stream().map(CategoriaCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo categorias", e);
        }
    }

    // ============ PRODUCTOS ============
    public List<Producto> loadProductos() throws DataPersistenceException {
        try {
            Map<Integer, Categoria> categorias = loadCategorias().stream()
                    .collect(Collectors.toMap(Categoria::getId, Function.identity()));

            ensureFileWithHeader(FILE_PRODUCTOS, ProductoCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_PRODUCTOS);
            List<Producto> list = new ArrayList<>();
            for (String r : rows) {
                addIfParseable(list, () -> {
                    try {
                        return ProductoCsv.fromRow(r, categorias::get);
                    } catch (Exceptions.InvalidProductException e) {
                        throw new RuntimeException(e);
                    }
                }, r, "productos.csv");
            }
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo productos", e);
        }
    }

    public void saveProducto(Producto p) throws DataPersistenceException {
        try {
            appendLine(FILE_PRODUCTOS, ProductoCsv.header(), ProductoCsv.toRow(p));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando producto", e);
        }
    }

    public void saveAllProductos(Collection<Producto> list) throws DataPersistenceException {
        try {
            writeAll(FILE_PRODUCTOS, ProductoCsv.header(),
                    list.stream().map(ProductoCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo productos", e);
        }
    }

    // ============ CLIENTES ============
    public List<Cliente> loadClientes() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_CLIENTES, ClienteCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_CLIENTES);
            List<Cliente> list = new ArrayList<>();
            for (String r : rows) {
                addIfParseable(list, () -> {
                    try {
                        return ClienteCsv.fromRow(r);
                    } catch (InvalidClientOperationException e) {
                        throw new RuntimeException(e);
                    }
                }, r, "clientes.csv");
            }
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo clientes", e);
        }
    }

    public void saveCliente(Cliente c) throws DataPersistenceException {
        try {
            appendLine(FILE_CLIENTES, ClienteCsv.header(), ClienteCsv.toRow(c));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando cliente", e);
        }
    }

    public void saveAllClientes(Collection<Cliente> list) throws DataPersistenceException {
        try {
            writeAll(FILE_CLIENTES, ClienteCsv.header(),
                    list.stream().map(ClienteCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo clientes", e);
        }
    }

    // ============ METODOS PAGO ============
    public List<MetodoPago> loadMetodosPago() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_METODOS_PAGO, MetodoPagoCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_METODOS_PAGO);
            List<MetodoPago> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> MetodoPagoCsv.fromRow(r), r, "metodos_pago.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo metodos de pago", e);
        }
    }

    public void saveMetodoPago(MetodoPago m) throws DataPersistenceException {
        try {
            appendLine(FILE_METODOS_PAGO, MetodoPagoCsv.header(), MetodoPagoCsv.toRow(m));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando metodo de pago", e);
        }
    }

    public void saveAllMetodosPago(Collection<MetodoPago> list) throws DataPersistenceException {
        try {
            writeAll(FILE_METODOS_PAGO, MetodoPagoCsv.header(),
                    list.stream().map(MetodoPagoCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo metodos de pago", e);
        }
    }

    // ============ COMPRAS ============
    public List<Compra> loadCompras() throws DataPersistenceException {
        try {
            Map<Integer, MetodoPago> metodos = loadMetodosPago().stream()
                    .collect(Collectors.toMap(MetodoPago::getId, Function.identity()));

            ensureFileWithHeader(FILE_COMPRAS, CompraCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_COMPRAS);
            List<Compra> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> CompraCsv.fromRow(r, metodos::get), r, "compras.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo compras", e);
        }
    }

    public void saveCompra(Compra compra) throws DataPersistenceException {
        try {
            appendLine(FILE_COMPRAS, CompraCsv.header(), CompraCsv.toRow(compra));
            saveLineasIfPresent(compra);
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando compra", e);
        }
    }

    public void saveAllCompras(Collection<Compra> list) throws DataPersistenceException {
        try {
            writeAll(FILE_COMPRAS, CompraCsv.header(),
                    list.stream().map(CompraCsv::toRow).collect(Collectors.toList()));
            rewriteAllLineasForCompras(list);
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo compras", e);
        }
    }

    // ============ LINEAS COMPRA ============
    public void saveLineaCompra(LineaCompra l, int compraId) throws DataPersistenceException {
        try {
            appendLine(FILE_LINEAS_COMPRA, LineaCompraCsv.header(), LineaCompraCsv.toRow(l, compraId));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando línea de compra", e);
        }
    }

    public Map<Integer, List<LineaCompra>> loadLineasCompraPorCompraId() throws DataPersistenceException {
        try {
            Map<Integer, Producto> productos = loadProductos().stream()
                    .collect(Collectors.toMap(Producto::getId, Function.identity()));

            ensureFileWithHeader(FILE_LINEAS_COMPRA, LineaCompraCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_LINEAS_COMPRA);

            Map<Integer, List<LineaCompra>> porCompra = new HashMap<>();
            for (String r : rows) {
                try {
                    List<String> cols = CsvUtils.splitRow(r);
                    int compraId = Integer.parseInt(cols.get(0).trim());
                    LineaCompra lc = LineaCompraCsv.fromRow(r, productos::get);
                    porCompra.computeIfAbsent(compraId, k -> new ArrayList<>()).add(lc);
                } catch (Exception ex) {
                    System.out.println("⚠ Fila inválida en lineas_compra.csv (omitida): " + r + " → " + ex.getMessage());
                }
            }
            return porCompra;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo líneas de compra", e);
        }
    }

    public List<LineaCompra> loadLineasDeCompra(int compraId) throws DataPersistenceException {
        return loadLineasCompraPorCompraId().getOrDefault(compraId, List.of());
    }

    // ============ FABRICAS ============
    public List<Fabrica> loadFabricas() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_FABRICAS, FabricaCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_FABRICAS);
            List<Fabrica> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> FabricaCsv.fromRow(r), r, "fabricas.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo fabricas", e);
        }
    }

    public void saveFabrica(Fabrica f) throws DataPersistenceException {
        try {
            appendLine(FILE_FABRICAS, FabricaCsv.header(), FabricaCsv.toRow(f));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando fabrica", e);
        }
    }

    public void saveAllFabricas(Collection<Fabrica> list) throws DataPersistenceException {
        try {
            writeAll(FILE_FABRICAS, FabricaCsv.header(),
                    list.stream().map(FabricaCsv::toRow).toList());
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo fabricas", e);
        }
    }

    // ============ TRABAJADORES ============
    public List<TrabajadorEsclavizado> loadTrabajadores() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_TRABAJADORES, TrabajadorEsclavizadoCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_TRABAJADORES);
            List<TrabajadorEsclavizado> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> TrabajadorEsclavizadoCsv.fromRow(r), r, "trabajadores.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo trabajadores", e);
        }
    }

    public void saveTrabajador(TrabajadorEsclavizado t) throws DataPersistenceException {
        try {
            appendLine(FILE_TRABAJADORES, TrabajadorEsclavizadoCsv.header(), TrabajadorEsclavizadoCsv.toRow(t));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando trabajador", e);
        }
    }

    public void saveAllTrabajadores(Collection<TrabajadorEsclavizado> list) throws DataPersistenceException {
        try {
            writeAll(FILE_TRABAJADORES, TrabajadorEsclavizadoCsv.header(),
                    list.stream().map(TrabajadorEsclavizadoCsv::toRow).toList());
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo trabajadores", e);
        }
    }

    // ============ ADMINS / DESARROLLADORES ============
    public List<AdministradorContenido> loadAdminsContenido() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_ADMIN_CONTENIDO, AdministradorContenidoCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_ADMIN_CONTENIDO);
            List<AdministradorContenido> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> AdministradorContenidoCsv.fromRow(r), r, "admin_contenido.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo admins contenido", e);
        }
    }

    public void saveAdminContenido(AdministradorContenido a) throws DataPersistenceException {
        try {
            appendLine(FILE_ADMIN_CONTENIDO, AdministradorContenidoCsv.header(), AdministradorContenidoCsv.toRow(a));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando admin contenido", e);
        }
    }

    public void saveAllAdminsContenido(Collection<AdministradorContenido> list) throws DataPersistenceException {
        try {
            writeAll(FILE_ADMIN_CONTENIDO, AdministradorContenidoCsv.header(),
                    list.stream().map(AdministradorContenidoCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo admin contenido", e);
        }
    }

    public List<AdministradorUsuario> loadAdminsUsuario() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_ADMIN_USUARIO, AdministradorUsuarioCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_ADMIN_USUARIO);
            List<AdministradorUsuario> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> AdministradorUsuarioCsv.fromRow(r), r, "admin_usuario.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo admins usuario", e);
        }
    }

    public void saveAdminUsuario(AdministradorUsuario a) throws DataPersistenceException {
        try {
            appendLine(FILE_ADMIN_USUARIO, AdministradorUsuarioCsv.header(), AdministradorUsuarioCsv.toRow(a));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando admin usuario", e);
        }
    }

    public void saveAllAdminsUsuario(Collection<AdministradorUsuario> list) throws DataPersistenceException {
        try {
            writeAll(FILE_ADMIN_USUARIO, AdministradorUsuarioCsv.header(),
                    list.stream().map(AdministradorUsuarioCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo admin usuario", e);
        }
    }

    public List<DesarrolladorProducto> loadDesarrolladores() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_DESARROLLADORES, DesarrolladorProductoCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_DESARROLLADORES);
            List<DesarrolladorProducto> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> DesarrolladorProductoCsv.fromRow(r), r, "desarrolladores.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo desarrolladores", e);
        }
    }

    public void saveDesarrollador(DesarrolladorProducto d) throws DataPersistenceException {
        try {
            appendLine(FILE_DESARROLLADORES, DesarrolladorProductoCsv.header(), DesarrolladorProductoCsv.toRow(d));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando desarrollador", e);
        }
    }

    public void saveAllDesarrolladores(Collection<DesarrolladorProducto> list) throws DataPersistenceException {
        try {
            writeAll(FILE_DESARROLLADORES, DesarrolladorProductoCsv.header(),
                    list.stream().map(DesarrolladorProductoCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo desarrolladores", e);
        }
    }

    // ============ CONSEJO ============
    public List<ConsejoSombrio> loadConsejo() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_CONSEJO_SOMBRIO, ConsejoSombrioCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_CONSEJO_SOMBRIO);
            List<ConsejoSombrio> list = new ArrayList<>();
            for (String r : rows) addIfParseable(list, () -> ConsejoSombrioCsv.fromRow(r), r, "consejo_sombrio.csv");
            return list;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo consejo sombrio", e);
        }
    }

    public void saveConsejo(ConsejoSombrio c) throws DataPersistenceException {
        try {
            appendLine(FILE_CONSEJO_SOMBRIO, ConsejoSombrioCsv.header(), ConsejoSombrioCsv.toRow(c));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando consejo", e);
        }
    }

    public void saveAllConsejos(Collection<ConsejoSombrio> list) throws DataPersistenceException {
        try {
            writeAll(FILE_CONSEJO_SOMBRIO, ConsejoSombrioCsv.header(),
                    list.stream().map(ConsejoSombrioCsv::toRow).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo consejos", e);
        }
    }

    public void saveConsejoMiembro(int consejoId, Usuario usuario) throws DataPersistenceException {
        try {
            appendLine(FILE_CONSEJO_MIEMBROS, ConsejoMiembrosCsv.header(),
                    ConsejoMiembrosCsv.toRow(consejoId, usuario));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando miembro de consejo", e);
        }
    }

    public List<Usuario> loadConsejoMiembros(Function<Integer, Usuario> findUsuarioById) throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_CONSEJO_MIEMBROS, ConsejoMiembrosCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_CONSEJO_MIEMBROS);
            List<Usuario> out = new ArrayList<>();
            for (String r : rows) addIfParseable(out, () -> ConsejoMiembrosCsv.fromRow(r, findUsuarioById), r, "consejo_miembros.csv");
            return out;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo miembros del consejo", e);
        }
    }

    // ============ REGISTROS ESCLAVOS ============
    public void saveRegistroEsclavos(int id, RegistroEsclavos r) throws DataPersistenceException {
        try {
            appendLine(FILE_REGISTRO_ESCLAVOS, RegistroEsclavosCsv.header(), RegistroEsclavosCsv.toRow(r, id));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando registro esclavos", e);
        }
    }

    public List<RegistroEsclavos> loadRegistrosEsclavos() throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_REGISTRO_ESCLAVOS, RegistroEsclavosCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_REGISTRO_ESCLAVOS);
            List<RegistroEsclavos> out = new ArrayList<>();
            for (String r : rows) addIfParseable(out, () -> RegistroEsclavosCsv.fromRow(r), r, "registro_esclavos.csv");
            return out;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo registros esclavos", e);
        }
    }

    public void saveRegistroTrabajador(int registroId, TrabajadorEsclavizado t) throws DataPersistenceException {
        try {
            appendLine(FILE_REGISTRO_TRAB, RegistroTrabajadoresCsv.header(),
                    RegistroTrabajadoresCsv.toRow(registroId, t));
        } catch (IOException e) {
            throw new DataPersistenceException("Error guardando relación registro-trabajador", e);
        }
    }

    public List<TrabajadorEsclavizado> loadRegistroTrabajadores(Function<Integer, TrabajadorEsclavizado> findById)
            throws DataPersistenceException {
        try {
            ensureFileWithHeader(FILE_REGISTRO_TRAB, RegistroTrabajadoresCsv.header());
            List<String> rows = readAllLinesSkippingHeader(FILE_REGISTRO_TRAB);
            List<TrabajadorEsclavizado> out = new ArrayList<>();
            for (String r : rows) addIfParseable(out, () -> RegistroTrabajadoresCsv.fromRow(r, findById), r, "registro_trabajadores.csv");
            return out;
        } catch (IOException e) {
            throw new DataPersistenceException("Error leyendo relación registro-trabajador", e);
        }
    }

    // ============ USUARIOS agregados ============
    public List<Usuario> loadAllUsuarios() throws DataPersistenceException {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(loadAdminsContenido());
        usuarios.addAll(loadAdminsUsuario());
        usuarios.addAll(loadDesarrolladores());
        // Cliente no se mete aquí porque ya lo cargas por separado
        return usuarios;
    }

    public void saveAllUsuarios(Collection<Usuario> usuarios) throws DataPersistenceException {
        List<AdministradorContenido> contenidos = new ArrayList<>();
        List<AdministradorUsuario> admins = new ArrayList<>();
        List<DesarrolladorProducto> desarrolladores = new ArrayList<>();

        for (Usuario u : usuarios) {
            if (u instanceof AdministradorContenido a) contenidos.add(a);
            else if (u instanceof AdministradorUsuario a) admins.add(a);
            else if (u instanceof DesarrolladorProducto d) desarrolladores.add(d);
        }

        try {
            writeAll(FILE_ADMIN_CONTENIDO, AdministradorContenidoCsv.header(),
                    contenidos.stream().map(AdministradorContenidoCsv::toRow).toList());
            writeAll(FILE_ADMIN_USUARIO, AdministradorUsuarioCsv.header(),
                    admins.stream().map(AdministradorUsuarioCsv::toRow).toList());
            writeAll(FILE_DESARROLLADORES, DesarrolladorProductoCsv.header(),
                    desarrolladores.stream().map(DesarrolladorProductoCsv::toRow).toList());
        } catch (IOException e) {
            throw new DataPersistenceException("Error escribiendo archivos de usuarios", e);
        }
    }

    // ===== Helpers internos para lineas_compra =====
    private void rewriteAllLineasForCompras(Collection<Compra> compras) throws IOException {
        List<String> rows = new ArrayList<>();
        for (Compra c : compras) {
            int compraId = c.getId();
            for (LineaCompra l : getLineasReflect(c)) {
                rows.add(LineaCompraCsv.toRow(l, compraId));
            }
        }
        writeAll(FILE_LINEAS_COMPRA, LineaCompraCsv.header(), rows);
    }

    private void saveLineasIfPresent(Compra compra) throws IOException {
        int compraId = compra.getId();
        for (LineaCompra l : getLineasReflect(compra)) {
            appendLine(FILE_LINEAS_COMPRA, LineaCompraCsv.header(), LineaCompraCsv.toRow(l, compraId));
        }
    }

    @SuppressWarnings("unchecked")
    private List<LineaCompra> getLineasReflect(Compra compra) {
        try {
            Method m = null;
            try { m = compra.getClass().getMethod("getLineas"); }
            catch (NoSuchMethodException ignore) {}
            if (m == null) {
                try { m = compra.getClass().getMethod("getLineasCompra"); }
                catch (NoSuchMethodException ignore) {}
            }
            if (m != null) {
                Object val = m.invoke(compra);
                if (val instanceof List<?>) {
                    List<?> raw = (List<?>) val;
                    List<LineaCompra> out = new ArrayList<>();
                    for (Object o : raw) if (o instanceof LineaCompra lc) out.add(lc);
                    return out;
                }
            }
        } catch (Exception ignore) {}
        return Collections.emptyList();
    }

    @SuppressWarnings("unused")
    private void setLineasIfPresent(Compra compra, List<LineaCompra> lineas) {
        try {
            Method set = null;
            try { set = compra.getClass().getMethod("setLineas", List.class); }
            catch (NoSuchMethodException ignore) {}
            if (set != null) set.invoke(compra, lineas);
        } catch (Exception ignore) {}
    }
}
