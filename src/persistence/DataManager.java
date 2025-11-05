package persistence;

import Exceptions.DataPersistenceException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String DATA_FOLDER = "data/";

    static {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) folder.mkdirs();
    }

    public static void guardar(String fileName, List<String> lineas) throws DataPersistenceException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FOLDER + fileName))) {
            for (String linea : lineas) writer.println(linea);
        } catch (IOException e) {
            throw new DataPersistenceException("Error al guardar en " + fileName + ": " + e.getMessage());
        }
    }

    public static List<String> cargar(String fileName) throws DataPersistenceException {
        List<String> lineas = new ArrayList<>();
        File file = new File(DATA_FOLDER + fileName);
        if (!file.exists()) return lineas;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) lineas.add(linea);
        } catch (IOException e) {
            throw new DataPersistenceException("Error al leer " + fileName + ": " + e.getMessage());
        }

        return lineas;
    }
}