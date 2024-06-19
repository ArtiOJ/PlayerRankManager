package org.test.playerrankmanager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class MongoServer {

    private MongoCollection<Document> collection;

    // Constructor to initialize the connection to the database and collection
    public MongoServer(String connectionString, String dbName, String collectionName) {
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    // Function to add a new document
    public void addDocument(String uuidValue, int xpValue, String nameValue) {
        Document document = new Document("uuid", uuidValue)
                .append("xp", xpValue)
                .append("name", nameValue);
        collection.insertOne(document);
    }

    // Function to remove a document
    public void removeDocument(String uuidValue) {
        collection.deleteOne(Filters.eq("uuid", uuidValue));
    }

    // Function to modify the xp value in a document
    public void updateXp(String uuidValue, int newXpValue) {
        collection.updateOne(Filters.eq("uuid", uuidValue), new Document("$set", new Document("xp", newXpValue)));
    }

    // Function to fetch a document by UUID
    public Document fetchDocumentByUuid(String uuidValue) {
        return collection.find(Filters.eq("uuid", uuidValue)).first();
    }

    // Function to get the XP value by UUID
    public Integer getXpByUuid(String uuidValue) {
        Document document = fetchDocumentByUuid(uuidValue);
        if (document != null) {
            return document.getInteger("xp");
        }
        return null; // Return null if document is not found
    }

    // Function to add XP to a document by UUID
    public void addXp(String uuidValue, int xpToAdd) {
        Document document = fetchDocumentByUuid(uuidValue);
        if (document != null) {
            int currentXp = document.getInteger("xp");
            int newXp = currentXp + xpToAdd;
            updateXp(uuidValue, newXp);
        } else {
            System.out.println("Document with UUID " + uuidValue + " not found.");
        }
    }
}


