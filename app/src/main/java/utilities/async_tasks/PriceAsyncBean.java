package utilities.async_tasks;

import org.json.JSONArray;

import db.DatabaseHandler;

public class PriceAsyncBean {
    DatabaseHandler databaseHandler;
    JSONArray jsonArray;

    public PriceAsyncBean() {
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public void setDatabaseHandler(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
