package app.server.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-based implementation of the IDao interface, storing User objects.
 */
public class MyDMFileImpl<T> implements IDao<T> {
    private static final Logger logger = Logger.getLogger(MyDMFileImpl.class.getName());
    private final String filePath;

    public MyDMFileImpl(String filePath) {
        this.filePath = filePath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T find(String id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<T> objects = (List<T>) ois.readObject();
            // We'll rely on obj.toString().contains(id) or override equals if needed
            for (T obj : objects) {
                if (obj.toString().contains(id)) {
                    return obj;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error reading from file: " + filePath, e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean save(T entity) {
        List<T> objects = new ArrayList<>();

        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                objects = (List<T>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Error loading existing data from " + filePath, e);
            }
        }

        // Add new entity
        objects.add(entity);

        // Save all objects
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(objects);
            logger.info("Saved entity to " + filePath);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to file: " + filePath, e);
            return false;
        }
    }
}
