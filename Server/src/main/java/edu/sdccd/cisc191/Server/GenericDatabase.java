package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

/**
 * A generic abstract class to provide file-based persistence and database interaction for
 * Spring Data JPA repositories. It supports reading from and writing to a JSON file, and
 * includes hooks for initializing default entities if the database is empty.
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 * @param <R>  the JPA repository type for the entity
 */
public abstract class GenericDatabase<T, ID, R extends JpaRepository<T, ID>> {

    /**
     * The Spring Data JPA repository used for database operations.
     */
    protected final R repository;

    /**
     * The class object representing the entity type.
     */
    protected final Class<T> entityClass;

    /**
     * Constructs a new {@code GenericDatabase} with the given repository and entity class.
     *
     * @param repository  the JPA repository for the entity
     * @param entityClass the entity class type
     */
    protected GenericDatabase(R repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    /**
     * Retrieves or creates the JSON file used to persist entity data.
     *
     * @return the file reference
     * @throws RuntimeException if file creation fails
     */
    protected File getOrCreateDatabaseFile() {
        String projectDir = System.getProperty("user.dir");
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

    /**
     * Loads data from the JSON file into the repository if the database is empty.
     * If the file is not found or parsing fails, it initializes default entities.
     *
     * @throws Exception if initialization or file parsing fails
     */
    public void loadOrInitializeDatabase() throws Exception {
        if (repository.count() == 0) {
            File file = getOrCreateDatabaseFile();
            System.out.println("Loading from: " + file.getAbsolutePath());
            if (file.exists()) {
                System.out.println("Contents:\n" + Files.readString(file.toPath()));
                try {
                    ObjectMapper objectMapper = new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    /**
     * Saves the current repository data to the JSON file.
     * Initializes any lazy-loaded collections before serialization.
     */
    @Transactional
    public void saveToFile() {
        System.out.println("Save to file method triggered");
        try (Writer writer = new FileWriter(getOrCreateDatabaseFile())) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<T> entities = repository.findAll();
            entities.forEach(entity -> {
                if (entity != null) {
                    org.hibernate.Hibernate.initialize(entity);
                }
            });
            objectMapper.writeValue(writer, entities);
            System.out.println(getEntityName() + " Database saved to file: " + getOrCreateDatabaseFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all entities from the repository.
     *
     * @return a list of all entities
     */
    public List<T> getAll() {
        return repository.findAll();
    }

    /**
     * Saves the given entity to the repository.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    public T save(T entity) {
        return repository.save(entity);
    }

    /**
     * Deletes the specified entity from the repository.
     *
     * @param entity the entity to delete
     */
    public void delete(T entity) {
        repository.delete(entity);
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id the ID of the entity
     * @return the entity if found, otherwise {@code null}
     */
    //TODO: findByID is never used, either delete or implement
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Returns the number of entities in the repository.
     *
     * @return the total entity count
     */
    //TODO: getSize is never used, either delete or implement
    public long getSize() {
        return repository.count();
    }

    /**
     * Abstract method to initialize the repository with default entities.
     *
     * @throws Exception if initialization fails
     */
    protected abstract void initializeDefaultEntities() throws Exception;

    /**
     * Abstract method to specify the filename for the JSON data.
     *
     * @return the filename to use
     */
    protected abstract String getFileName();

    /**
     * Abstract method to provide a name label for the entity type.
     *
     * @return the name of the entity
     */
    protected abstract String getEntityName();
}
