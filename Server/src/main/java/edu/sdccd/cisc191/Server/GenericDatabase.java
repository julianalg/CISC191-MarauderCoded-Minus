package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

public abstract class GenericDatabase<T, ID, R extends JpaRepository<T, ID>> {

    protected final R repository;
    protected final Class<T> entityClass;

    protected GenericDatabase(R repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    protected File getOrCreateDatabaseFile() {
        // Get the project root directory
        String projectDir = System.getProperty("user.dir");
        // Construct the path to src/main/resources
        String resourcePath = projectDir + "/Server/src/main/resources/" + getFileName();
        System.out.println("Resource path: " + resourcePath);
        
        File file = new File(resourcePath);
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create database file at " + resourcePath, e);
        }
    }
    public void loadOrInitializeDatabase() throws Exception {
        if (repository.count() == 0) {
            File file = getOrCreateDatabaseFile();
            System.out.println("Loading from: " + file.getAbsolutePath());
            if (file.exists()) {
                System.out.println("Contents:\n" + Files.readString(file.toPath()));
                try {
                    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, entityClass);

                    List<T> entities = objectMapper.readValue(file, listType);
                    System.out.println("Loaded " + entities.size() + " " + getEntityName() + " entities from file.");
                    repository.saveAll(entities);
                    System.out.println(getEntityName() + " Database loaded from file.");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to load " + getEntityName() + " Database from file. Initializing with default data.");
                    initializeDefaultEntities();
                }
            } else {
                System.out.println(getEntityName() + " Database file not found. Initializing with default data.");
                initializeDefaultEntities();
            }
        }
    }

    public void saveToFile() {
        System.out.println("Save to file method triggered");
        try (Writer writer = new FileWriter(getOrCreateDatabaseFile())) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<T> entities = repository.findAll();
            objectMapper.writeValue(writer, entities);
            System.out.println(getEntityName() + " Database saved to file: " + getOrCreateDatabaseFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public void delete(T entity) {
        repository.delete(entity);
    }

    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    public long getSize() {
        return repository.count();
    }

    protected abstract void initializeDefaultEntities() throws Exception;
    protected abstract String getFileName();
    protected abstract String getEntityName();
}