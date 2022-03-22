package dev.strongtino.soteria.database;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import dev.strongtino.soteria.Soteria;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Optional;

public interface DatabaseService {

    /**
     * Establishes a connection to the database
     *
     * @param credentials connection information
     * @return database service with a new established connection
     */
    DatabaseService connect(Credentials credentials);

    /**
     * Inserts a document to a collection
     *
     * @param collection where to insert the document
     * @param document to insert
     */
    void insert(String collection, Document document);

    /**
     * Updates a document inside a collection
     *
     * @param collection where the document to update is located
     * @param query to find the document with
     * @param document that will replace the one found
     * @return the result of the operation
     */
    UpdateResult update(String collection, Bson query, Document document);

    /**
     * Deletes a document from a collection
     *
     * @param collection where the document to delete is located
     * @param query to find the document with
     * @return the result of the operation
     */
    DeleteResult delete(String collection, Bson query);

    /**
     * Finds a document from a collection
     *
     * @param collection where the document to find is located
     * @param query to find the document with
     * @return an Optional with the found document if it exists in
     * the collection or an empty Optional if no document is found
     */
    Optional<Document> findOne(String collection, Bson query);

    /**
     * Fetches all data from a specific collection in the database
     *
     * @param collection from where to fetch all documents
     * @param query to find the documents with
     * @return found documents inside a list
     */
    List<Document> findAll(String collection, Bson query);

    /**
     * Fetches all data from a specific collection in the database
     *
     * @param collection from where to fetch all documents
     * @return found documents inside a list
     */
    List<Document> findAll(String collection);

    default String getDatabase() {
        return Soteria.APPLICATION_NAME.toLowerCase();
    }
}