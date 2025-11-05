package persistence;

import Exceptions.DataPersistenceException;
import Exceptions.InvalidClientOperationException;
import Exceptions.InvalidProductException;

import model.*;

import persistence.csv.CategoriaCsv;
import persistence.csv.ProductoCsv;
import persistence.csv.ClienteCsv;
import persistence.csv.MetodoPagoCsv;
import persistence.csv.CarritoCsv;
import persistence.csv.LineaCarritoCsv;
import persistence.csv.CompraCsv;
import persistence.csv.LineaCompraCsv;
import persistence.csv.FabricaCsv;
import persistence.csv.TrabajadorEsclavizadoCsv;
import persistence.csv.DesarrolladorProductoCsv;
import persistence.csv.AdministradorUsuarioCsv;
import persistence.csv.AdministradorContenidoCsv;
import persistence.csv.ConsejoSombrioCsv;
import persistence.csv.ConsejoMiembrosCsv;
import persistence.csv.RegistroEsclavosCsv;
import persistence.csv.RegistroTrabajadoresCsv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de persistencia centralizado usando CSV + DataManager.
 * Nota: La Dueña no se persiste (es única y se instancia al arrancar).
 */
public class PersistenceService {

    // ====== Nombres de archivos ======
    private static final String F_CATEGORIAS            = "categorias.csv";
    private static final String F_PRODUCTOS             = "productos.csv";
    private static final String F_CLIENTES              = "clientes.csv";
    private static final String F_METODOS_PAGO          = "metodos_pago.csv";
    private static final String F_CARRITOS              = "carritos.csv";
    private static final String F_LINEAS_CARRITO        = "lineas_carrito.csv";
    private static final String F_COMPRAS               = "compras.csv";
    private static final String F_LINEAS_COMPRA         = "lineas_compra.csv";
    private static final String F_FABRICAS              = "fabricas.csv";
    private static final String F_TRABAJADORES          = "trabajadores_esclavizados.csv";
    private static final String F_DESARROLLADORES       = "desarrolladores_producto.csv";
    private static final String F_ADMIN_USU             = "admins_usuario.csv";
    private static final String F_ADMIN_CONT            = "admins_contenido.csv";
    private static final String F_CONSEJO               = "consejo_sombrio.csv";
    private static final String F_CONSEJO_MIEMBROS      = "consejo_miembros.csv";
    private static final String F_REGISTROS             = "registro_esclavos.csv";
    private static final String F_REG_TRAB              = "registro_trabajadores.csv";

    // ====== Utilidad: crear índice por id ======
    private static <T> Map<Integer, T> indexById(List<T> list, java.util.function.ToIntFunction<T> idGetter) {
        Map<Integer, T> map = new HashMap<>();
        for (T t : list) map.put(idGetter.applyAsInt(t), t);
        return map;
    }

    // ====== CATEGORÍAS ======
    public List<Categoria> cargarCategorias() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_CATEGORIAS);
        List<Categoria> out = new ArrayList<>();
        if (lines.isEmpty()) return out;
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(CategoriaCsv.fromRow(row));
        }
        return out;
    }

    public void guardarCategorias(List<Categoria> categorias) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(CategoriaCsv.header());
        for (Categoria c : categorias) out.add(CategoriaCsv.toRow(c));
        DataManager.guardar(F_CATEGORIAS, out);
    }

    // ====== PRODUCTOS ======
    public List<Producto> cargarProductos(Map<Integer, Categoria> idxCategoria)
            throws DataPersistenceException, InvalidProductException {
        List<String> lines = DataManager.cargar(F_PRODUCTOS);
        List<Producto> out = new ArrayList<>();
        if (lines.isEmpty()) return out;
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(ProductoCsv.fromRow(row, idxCategoria::get));
        }
        return out;
    }

    public void guardarProductos(List<Producto> productos) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(ProductoCsv.header());
        for (Producto p : productos) out.add(ProductoCsv.toRow(p));
        DataManager.guardar(F_PRODUCTOS, out);
    }

    // ====== CLIENTES ======
    public List<Cliente> cargarClientes() throws DataPersistenceException, InvalidClientOperationException {
        List<String> lines = DataManager.cargar(F_CLIENTES);
        List<Cliente> out = new ArrayList<>();
        if (lines.isEmpty()) return out;
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(ClienteCsv.fromRow(row));
        }
        return out;
    }

    public void guardarClientes(List<Cliente> clientes) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(ClienteCsv.header());
        for (Cliente c : clientes) out.add(ClienteCsv.toRow(c));
        DataManager.guardar(F_CLIENTES, out);
    }

    // ====== MÉTODOS DE PAGO ======
    public List<MetodoPago> cargarMetodosPago() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_METODOS_PAGO);
        List<MetodoPago> out = new ArrayList<>();
        if (lines.isEmpty()) return out;
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(MetodoPagoCsv.fromRow(row));
        }
        return out;
    }

    public void guardarMetodosPago(List<MetodoPago> metodos) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(MetodoPagoCsv.header());
        for (MetodoPago m : metodos) out.add(MetodoPagoCsv.toRow(m));
        DataManager.guardar(F_METODOS_PAGO, out);
    }

    // ====== CARRITOS + LÍNEAS ======
    public List<Carrito> cargarCarritos(Map<Integer, Producto> idxProducto) throws DataPersistenceException {
        List<Carrito> carritos = new ArrayList<>();
        List<String> linesC = DataManager.cargar(F_CARRITOS);
        for (int i = 1; i < linesC.size(); i++) {
            String row = linesC.get(i);
            if (!row.isBlank()) carritos.add(CarritoCsv.fromRow(row));
        }
        Map<Integer, Carrito> idxCarr = indexById(carritos, Carrito::getId);

        List<String> linesL = DataManager.cargar(F_LINEAS_CARRITO);
        for (int i = 1; i < linesL.size(); i++) {
            String row = linesL.get(i);
            if (row.isBlank()) continue;
            List<String> cols = CsvUtils.splitRow(row);
            int carritoId = Integer.parseInt(cols.get(0));
            LineaCarrito lc = LineaCarritoCsv.fromRow(row, idxProducto::get);
            Carrito carr = idxCarr.get(carritoId);
            if (carr != null) carr.getLineas().add(lc);
        }
        return carritos;
    }

    public void guardarCarritos(List<Carrito> carritos) throws DataPersistenceException {
        List<String> outC = new ArrayList<>();
        outC.add(CarritoCsv.header());
        for (Carrito c : carritos) outC.add(CarritoCsv.toRow(c));
        DataManager.guardar(F_CARRITOS, outC);

        List<String> outL = new ArrayList<>();
        outL.add(LineaCarritoCsv.header());
        for (Carrito c : carritos) {
            int carritoId = c.getId();
            for (LineaCarrito l : c.getLineas()) outL.add(LineaCarritoCsv.toRow(l, carritoId));
        }
        DataManager.guardar(F_LINEAS_CARRITO, outL);
    }

    // ====== COMPRAS + LÍNEAS ======
    public List<Compra> cargarCompras(Map<Integer, MetodoPago> idxMetodo, Map<Integer, Producto> idxProducto)
            throws DataPersistenceException {
        List<Compra> compras = new ArrayList<>();
        List<String> linesC = DataManager.cargar(F_COMPRAS);
        for (int i = 1; i < linesC.size(); i++) {
            String row = linesC.get(i);
            if (!row.isBlank()) compras.add(CompraCsv.fromRow(row, idxMetodo::get));
        }
        Map<Integer, Compra> idxCompra = indexById(compras, Compra::getId);

        List<String> linesL = DataManager.cargar(F_LINEAS_COMPRA);
        for (int i = 1; i < linesL.size(); i++) {
            String row = linesL.get(i);
            if (row.isBlank()) continue;
            List<String> cols = CsvUtils.splitRow(row);
            int compraId = Integer.parseInt(cols.get(0));
            LineaCompra lc = LineaCompraCsv.fromRow(row, idxProducto::get);
            Compra compra = idxCompra.get(compraId);
            if (compra != null) compra.getLineas().add(lc);
        }
        return compras;
    }

    public void guardarCompras(List<Compra> compras) throws DataPersistenceException {
        List<String> outC = new ArrayList<>();
        outC.add(CompraCsv.header());
        for (Compra c : compras) outC.add(CompraCsv.toRow(c));
        DataManager.guardar(F_COMPRAS, outC);

        List<String> outL = new ArrayList<>();
        outL.add(LineaCompraCsv.header());
        for (Compra c : compras) {
            int compraId = c.getId();
            for (LineaCompra l : c.getLineas()) outL.add(LineaCompraCsv.toRow(l, compraId));
        }
        DataManager.guardar(F_LINEAS_COMPRA, outL);
    }

    // ====== FÁBRICAS ======
    public List<Fabrica> cargarFabricas() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_FABRICAS);
        List<Fabrica> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(FabricaCsv.fromRow(row));
        }
        return out;
    }

    public void guardarFabricas(List<Fabrica> fabricas) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(FabricaCsv.header());
        for (Fabrica f : fabricas) out.add(FabricaCsv.toRow(f));
        DataManager.guardar(F_FABRICAS, out);
    }

    // ====== TRABAJADORES ESCLAVIZADOS ======
    public List<TrabajadorEsclavizado> cargarTrabajadores() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_TRABAJADORES);
        List<TrabajadorEsclavizado> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(TrabajadorEsclavizadoCsv.fromRow(row));
        }
        return out;
    }

    public void guardarTrabajadores(List<TrabajadorEsclavizado> lista) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(TrabajadorEsclavizadoCsv.header());
        for (TrabajadorEsclavizado t : lista) out.add(TrabajadorEsclavizadoCsv.toRow(t));
        DataManager.guardar(F_TRABAJADORES, out);
    }

    // ====== DESARROLLADORES / ADMINS ======
    public List<DesarrolladorProducto> cargarDesarrolladores() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_DESARROLLADORES);
        List<DesarrolladorProducto> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(DesarrolladorProductoCsv.fromRow(row));
        }
        return out;
    }

    public void guardarDesarrolladores(List<DesarrolladorProducto> lista) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(DesarrolladorProductoCsv.header());
        for (DesarrolladorProducto d : lista) out.add(DesarrolladorProductoCsv.toRow(d));
        DataManager.guardar(F_DESARROLLADORES, out);
    }

    public List<AdministradorUsuario> cargarAdminsUsuario() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_ADMIN_USU);
        List<AdministradorUsuario> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(AdministradorUsuarioCsv.fromRow(row));
        }
        return out;
    }

    public void guardarAdminsUsuario(List<AdministradorUsuario> lista) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(AdministradorUsuarioCsv.header());
        for (AdministradorUsuario a : lista) out.add(AdministradorUsuarioCsv.toRow(a));
        DataManager.guardar(F_ADMIN_USU, out);
    }

    public List<AdministradorContenido> cargarAdminsContenido() throws DataPersistenceException {
        List<String> lines = DataManager.cargar(F_ADMIN_CONT);
        List<AdministradorContenido> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) out.add(AdministradorContenidoCsv.fromRow(row));
        }
        return out;
    }

    public void guardarAdminsContenido(List<AdministradorContenido> lista) throws DataPersistenceException {
        List<String> out = new ArrayList<>();
        out.add(AdministradorContenidoCsv.header());
        for (AdministradorContenido a : lista) out.add(AdministradorContenidoCsv.toRow(a));
        DataManager.guardar(F_ADMIN_CONT, out);
    }

    // ====== CONSEJO SOMBRÍO + MIEMBROS ======
    public List<ConsejoSombrio> cargarConsejos(Map<Integer, Usuario> idxUsuario) throws DataPersistenceException {
        List<ConsejoSombrio> consejos = new ArrayList<>();
        List<String> lines = DataManager.cargar(F_CONSEJO);
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) consejos.add(ConsejoSombrioCsv.fromRow(row));
        }
        Map<Integer, ConsejoSombrio> idxConsejo = indexById(consejos, ConsejoSombrio::getId);

        List<String> linesM = DataManager.cargar(F_CONSEJO_MIEMBROS);
        for (int i = 1; i < linesM.size(); i++) {
            String row = linesM.get(i);
            if (row.isBlank()) continue;
            List<String> cols = CsvUtils.splitRow(row);
            int consejoId = Integer.parseInt(cols.get(0));
            int usuarioId = Integer.parseInt(cols.get(1));
            ConsejoSombrio c = idxConsejo.get(consejoId);
            Usuario u = idxUsuario.get(usuarioId);
            if (c != null && u != null) c.agregarMiembro(u);
        }
        return consejos;
    }

    public void guardarConsejos(List<ConsejoSombrio> consejos) throws DataPersistenceException {
        List<String> outC = new ArrayList<>();
        outC.add(ConsejoSombrioCsv.header());
        for (ConsejoSombrio c : consejos) outC.add(ConsejoSombrioCsv.toRow(c));
        DataManager.guardar(F_CONSEJO, outC);

        List<String> outM = new ArrayList<>();
        outM.add(ConsejoMiembrosCsv.header());
        for (ConsejoSombrio c : consejos) {
            int consejoId = c.getId();
            for (Usuario u : c.getMiembros()) outM.add(ConsejoMiembrosCsv.toRow(consejoId, u));
        }
        DataManager.guardar(F_CONSEJO_MIEMBROS, outM);
    }

    // ====== REGISTRO ESCLAVOS + RELACIÓN TRABAJADORES ======
    public List<RegistroEsclavos> cargarRegistros(Map<Integer, TrabajadorEsclavizado> idxTrab)
            throws DataPersistenceException {

        List<RegistroEsclavos> regs = new ArrayList<>();
        List<String> lines = DataManager.cargar(F_REGISTROS);
        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i);
            if (!row.isBlank()) regs.add(RegistroEsclavosCsv.fromRow(row));
        }
        // el id del registro es posicional (1..n)
        Map<Integer, RegistroEsclavos> idx = new HashMap<>();
        for (int i = 0; i < regs.size(); i++) idx.put(i + 1, regs.get(i));

        List<String> rel = DataManager.cargar(F_REG_TRAB);
        for (int i = 1; i < rel.size(); i++) {
            String row = rel.get(i);
            if (row.isBlank()) continue;
            List<String> cols = CsvUtils.splitRow(row);
            int regId  = Integer.parseInt(cols.get(0));
            int trabId = Integer.parseInt(cols.get(1));
            RegistroEsclavos r = idx.get(regId);
            TrabajadorEsclavizado t = idxTrab.get(trabId);
            if (r != null && t != null) r.agregarEsclavo(t);
        }
        return regs;
    }

    public void guardarRegistros(List<RegistroEsclavos> registros) throws DataPersistenceException {
        List<String> outR = new ArrayList<>();
        outR.add(RegistroEsclavosCsv.header());
        for (int i = 0; i < registros.size(); i++) {
            outR.add(RegistroEsclavosCsv.toRow(registros.get(i), i + 1));
        }
        DataManager.guardar(F_REGISTROS, outR);

        List<String> outRel = new ArrayList<>();
        outRel.add(RegistroTrabajadoresCsv.header());
        for (int i = 0; i < registros.size(); i++) {
            int regId = i + 1;
            for (TrabajadorEsclavizado t : registros.get(i).getTrabajadores()) {
                outRel.add(RegistroTrabajadoresCsv.toRow(regId, t));
            }
        }
        DataManager.guardar(F_REG_TRAB, outRel);
    }
}
