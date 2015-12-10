package framework;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created with IntelliJ IDEA.
 * User: jhasmanyquiroz
 * Date: 12/9/15
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBManager {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private static DBManager instance;

    private DBManager() {
        initialize();
    }

    public static DBManager getInstance() {
        if (instance == null)
            instance = new DBManager();
        return instance;
    }

    public void quitMongoDB() {
        mongoClient.close();
    }

    private void initialize() {
        mongoClient = new MongoClient(new MongoClientURI(CredentialsManager.getInstance().getMongoServer()));
        database = mongoClient.getDatabase(CredentialsManager.getInstance().getMongoDataBase());
    }
}