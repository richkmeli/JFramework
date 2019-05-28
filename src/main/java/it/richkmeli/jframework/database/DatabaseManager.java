package it.richkmeli.jframework.database;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.util.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ResourceBundle;

public class DatabaseManager implements DatabaseModel {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    protected String schemaName;
    protected String tableName;
    protected String table;

    @Override
    public void init() throws DatabaseException {
        init(null);
    }

    @Override
    public void init(String database) throws DatabaseException {

        loadConfigurationProperties(database);

        try {
            createSchema(schemaName);
            dbUrl += schemaName;

            createTables(tableName + table);
        } catch (DatabaseException e) {
            throw new DatabaseException(e);
        }
    }

    private void loadConfigurationProperties() throws DatabaseException {
        loadConfigurationProperties(null);
    }

    // TODO fai gestione con altro file configurazione, se presente non guarda quello di default dentro jframework

    private void loadConfigurationProperties(String databaseParam) throws DatabaseException {
        ResourceBundle resource = ResourceBundle.getBundle("configuration");
        String dbClass = null;

        // default db
        String database = "mysql";
        if (databaseParam == null) {
            database = resource.getString("database");
        }
        //Logger.i("DatabaseManager, loadConfigurationProperties, database: " + database);

        dbUsername = resource.getString("database." + database + ".username");
        dbPassword = resource.getString("database." + database + ".password");
        dbUrl = resource.getString("database." + database + ".url");
        dbClass = resource.getString("database." + database + ".class");
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    private void loadConfigurationProperties(String dbUsername, String dbPassword, String dbUrl, String dbClass) throws DatabaseException {
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Connection connect() throws DatabaseException {
        //Logger.i("DatabaseManager, connect. dbUrl: " + dbUrl);

        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void disconnect(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws DatabaseException {
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } catch (Exception e1) {        // null value of ResultSet in addDevice, removeDevice...
        }
        try {
            preparedStatement.close();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        try {
            connection.close();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    private boolean createSchema(String schema) {
        //String schemaSQL = "CREATE SCHEMA " + schema;
        String schemaSQL = "CREATE SCHEMA IF NOT EXISTS " + schema;
        //TODO fai gestione quando gia presente
        try {
            execute(schemaSQL);
        } catch (DatabaseException e) {
            Logger.e("DatabaseManager, createSchema", e);
        }
        return true;
    }

    private boolean createTables(String table) throws DatabaseException {
        //String tableSQL = "CREATE TABLE " + table;
        String tableSQL = "CREATE TABLE IF NOT EXISTS " + table;
        //TODO fai gestione quando gia presente
        try {
            execute(tableSQL);
        } catch (DatabaseException e) {
            Logger.e("DatabaseManager, createTables", e);
        }
        return true;
    }

    public boolean execute(String string) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(string);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            Logger.e("DatabaseManager, execute", e);
            return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }


    // TODO has to be tested
    public <T> boolean add(T type) throws DatabaseException {
        return add(type, null);
    }

    // TODO has to be tested
    public <T> boolean add(T type, DBManagerAction dbManagerAction) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if (dbManagerAction != null) {
            type = (T) dbManagerAction.action(type);
        }

        try {
            connection = connect();

            // create SQL string
            StringBuilder sql = new StringBuilder("INSERT IGNORE INTO " + tableName + " (");
            int i = 0;
            int numberOfFiels = type.getClass().getDeclaredFields().length;
            for (Field field : type.getClass().getDeclaredFields()) {
                Type fieldType = field.getGenericType();
                String fieldName = field.getName();
                sql.append(fieldName + ((++i < numberOfFiels) ? ", " : ") VALUES ("));
            }
            for (int i1 = 0; i1 < numberOfFiels; i1++) {
                sql.append("?" + ((i1 < numberOfFiels - 1) ? "," : ""));
            }
            sql.append(")");

            preparedStatement = connection.prepareStatement(sql.toString());

            // insert values in preparedStatement
            int parameterIndex = 1;
            for (Field field : type.getClass().getDeclaredFields()) {
                Type fieldType = field.getGenericType();
                String fieldName = field.getName();
                //Logger.i("Type: " + fieldType + ", Name: " + fieldName + ", Value: " + field.get(type).toString());
                switch (fieldType.getTypeName()) {
                    case "java.lang.String":
                        preparedStatement.setString(parameterIndex, field.get(type).toString());
                        break;
                    case "java.lang.Boolean":
                        //preparedStatement.setBoolean(parameterIndex, field.getBoolean(type));
                        preparedStatement.setBoolean(parameterIndex, new Boolean(field.get(type).toString()));
                        break;
                    default:
                        Logger.e("DatabaseManager, REFLECTION, type not mapped, type: " + fieldType);
                        break;
                }
                parameterIndex++;
            }
            //Logger.i(preparedStatement.toString());
            preparedStatement.executeUpdate();

        } catch (SQLException | IllegalAccessException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
            //return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

}