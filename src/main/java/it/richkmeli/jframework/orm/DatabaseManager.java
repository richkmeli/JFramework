package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.orm.annotation.Id;
import it.richkmeli.jframework.util.Logger;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseManager {
    private String dbUrl;
    protected String dbtype;
    private String dbUsername;
    private String dbPassword;
    protected String schemaName;
    protected String tableName;
    protected String table;

    protected void init() throws DatabaseException {
        init(null);
    }

    protected void init(String database) throws DatabaseException {

        loadConfigurationProperties(database);

        try {
            if ("mysql".equalsIgnoreCase(dbtype)) {
                createSchema(schemaName);
            }
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
        String database = resource.getString("database");//"mysql";
        if (databaseParam != null) {
            database = databaseParam;
        }
        //Logger.info("DatabaseManager, loadConfigurationProperties, database: " + database);

        dbUsername = resource.getString("database." + database + ".username");
        dbPassword = resource.getString("database." + database + ".password");
        dbUrl = resource.getString("database." + database + ".url");
        dbtype = resource.getString("database." + database + ".dbtype");
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

    protected Connection connect() throws DatabaseException {
        //Logger.info("DatabaseManager, connect. dbUrl: " + dbUrl);
        try {
            if ("derby".equalsIgnoreCase(dbtype)) {
                return DriverManager.getConnection(dbUrl + ";create=true", dbUsername, dbPassword);
            } else {
                return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    protected void disconnect(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws DatabaseException {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } catch (Exception e1) {        // null value of ResultSet in addDevice, removeDevice...
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        try {

            /*if ("derby".equalsIgnoreCase(dbtype)) {
                DriverManager.getConnection(dbUrl + ";shutdown=true");
            } else {*/
            if (connection != null) {
                connection.close();
            }
            //}
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    private void createSchema(String schema) {
        String schemaSQL = "";//"CREATE SCHEMA " + schema;
        if ("derby".equalsIgnoreCase(dbtype)) {
            schemaSQL = "CREATE SCHEMA " + schema;
        } else {
            schemaSQL = "CREATE SCHEMA IF NOT EXISTS " + schema;
        }

        //TODO fai gestione quando gia presente
        try {
            execute(schemaSQL);
        } catch (DatabaseException e) {
            Logger.error("DatabaseManager, createSchema", e);
        }
    }

    private void createTables(String table) throws DatabaseException {
        String tableSQL = "";//"CREATE TABLE " + table;
        if ("derby".equalsIgnoreCase(dbtype)) {
            tableSQL = "CREATE TABLE " + table;
        } else {
            tableSQL = "CREATE TABLE IF NOT EXISTS " + table;
        }

        //TODO fai gestione quando gia presente
        try {
            execute(tableSQL);
        } catch (DatabaseException e) {
            // TODO gestione derby
            if (e.getMessage().contains("already exists in Schema")) {
                // skipping
            } else {
                Logger.error("DatabaseManager, createTables", e);
            }
        }
    }

    private void execute(String string) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(string);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
            //Logger.error("DatabaseManager, execute", e);
            //return false;
        }
        disconnect(connection, preparedStatement, null);
    }

    // CRUD: Create, read, update and delete
    protected <T> boolean create(T type) throws DatabaseException {
        return create(type, null);
    }

    protected <T> T read(T type) throws DatabaseException {
        return read(type, null);
    }

    protected <T> T read(T type, DBManagerAction dbManagerAction) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if (dbManagerAction != null) {
            type = (T) dbManagerAction.action(type);
        }
        ResultSet resultSet = null;
        T elem = null;
        try {
            connection = connect();

            // create SQL string
            StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");

            // search primary key
            Field field = null;
            field = getPrimaryKey(type);
            if (field != null) {

                sql.append(field.getName()).append(" = ?");
                preparedStatement = connection.prepareStatement(sql.toString());

                preparedStatement = addAttributeToPreparedStatement(preparedStatement,
                        1, type, field);

                resultSet = preparedStatement.executeQuery();
                List<T> list = getListFromResultSet(type.getClass(), resultSet);
                if (!list.isEmpty()) {
                    elem = (T) list.get(0);
                } else {
                    disconnect(connection, preparedStatement, resultSet);
                    Logger.error("No " + type.getClass().getName() + " found with this " + field.getName() + " (PrimaryKey)");
                    //throw new DatabaseException("No " + type.getClass().getName() + " found with this " + field.getName() + " (PrimaryKey)");
                }
            } else {
                disconnect(connection, preparedStatement, null);
                throw new DatabaseException("ORM, Reflection: PrimaryKey not found");
                //return false;
            }
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
            //return false;
        }
        disconnect(connection, preparedStatement, resultSet);
        return elem;
    }

    protected <T> List<T> readAll(Class clazz) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> list = null;
        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName);
            resultSet = preparedStatement.executeQuery();
            list = getListFromResultSet(clazz, resultSet);

        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
        }
        disconnect(connection, preparedStatement, null);
        return list;
    }

    private <T> List<T> getListFromResultSet(Class clazz, ResultSet resultSet) throws DatabaseException, SQLException {
        List<T> list = new ArrayList<T>();
        try {
            // search class types
            //Class<?>[] cc = new Class[clazz.getFields().length];
            List<Class<?>> cc = new ArrayList<>();
            //int fieldN = 0;
            for (Field f : clazz.getDeclaredFields()) {
                if (!f.isSynthetic()) {
                    cc.add(f.getType());
                    //cc[fieldN++] = f.getType();
                }
            }
            // convert to an array of Class
            Class<?>[] classArray = new Class<?>[cc.size()];
            int i = 0;
            for (Class c : cc) {
                classArray[i++] = c;
            }

            // search constructor
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getConstructor(classArray);
            } catch (NoSuchMethodException e) {
                disconnect(null, null, resultSet);
                throw new DatabaseException("ORM, Reflection: constructor not found. Fields found (" + cc.size() + "): " + cc.toString());
            }
            if (constructor == null) {
                disconnect(null, null, resultSet);
                throw new DatabaseException("ORM, Reflection: constructor is null");
            }

            // create a new instance of generic object
            while (resultSet.next()) {
                List<Object> objList = new ArrayList<>();
                for (Field field : clazz.getDeclaredFields()) {
                    // handle add of transient field by coverage tools
                    if (!Modifier.isTransient(field.getModifiers())) {

                        Type fieldType = field.getGenericType();
                        switch (fieldType.getTypeName()) {
                            case "java.lang.String":
                                String values = resultSet.getString(field.getName());
                                objList.add(values);
                                break;
                            case "java.lang.Boolean":
                                Boolean valueb = resultSet.getBoolean(field.getName());
                                objList.add(valueb);
                                break;
                            default:
                                Logger.error("ORM, Reflection: type not mapped, type: " + fieldType);
                                break;
                        }
                    }
                }
                T obj = constructor.newInstance(objList.toArray());
                list.add(obj);
            }

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            disconnect(null, null, resultSet);
            throw new DatabaseException(e);
        }
        disconnect(null, null, resultSet);
        return list;
    }

   /*   protected <T> boolean update(T type) throws DatabaseException {
          return update(type, null);
      }*/

    protected <T> boolean delete(T type) throws DatabaseException {
        return delete(type, null);
    }

    protected <T> boolean delete(T type, DBManagerAction dbManagerAction) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if (dbManagerAction != null) {
            type = (T) dbManagerAction.action(type);
        }

        try {
            connection = connect();

            // create SQL string
            StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " WHERE ");

            // search primary key
            Field field = null;
            field = getPrimaryKey(type);
            if (field != null) {

                sql.append(field.getName()).append(" = ?");
                preparedStatement = connection.prepareStatement(sql.toString());

                preparedStatement = addAttributeToPreparedStatement(preparedStatement,
                        1, type, field);

                try {
                    preparedStatement.executeUpdate();
                    // TODO gestione derby
                } catch (DerbySQLIntegrityConstraintViolationException e) {
                    //Logger.info(e.getMessage());
                }

            } else {
                disconnect(connection, preparedStatement, null);
                throw new DatabaseException("ORM, Reflection: PrimaryKey not found");
                //return false;
            }
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
            //return false;
        }

        disconnect(connection, preparedStatement, null);
        return true;
    }

    private <T> Field getPrimaryKey(T type) {
        Field field = null;
        for (Field field2 : type.getClass().getDeclaredFields()) {
            Annotation annotation = field2.getAnnotation(Id.class);
            if (annotation != null) {
                field = field2;
            }
        }
        return field;
    }


    protected <T> boolean create(T type, DBManagerAction dbManagerAction) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if (dbManagerAction != null) {
            type = (T) dbManagerAction.action(type);
        }

        try {
            connection = connect();

            // create SQL string
            StringBuilder sql = new StringBuilder("INSERT " + ("mysql".equalsIgnoreCase(dbtype) ? "IGNORE" : "") + " INTO " + tableName + " (");
            int i = 0;
            // count transient fields
            int numberOfFieldTransient = 0;
            for (Field field : type.getClass().getDeclaredFields()) {
                if (Modifier.isTransient(field.getModifiers())) {
                    numberOfFieldTransient++;
                }
            }

            int numberOfFiels = type.getClass().getDeclaredFields().length - numberOfFieldTransient;
            for (Field field : type.getClass().getDeclaredFields()) {
                //Type fieldType = field.getGenericType();
                String fieldName = field.getName();
                // handle add of transient field by coverage tools
                if (!Modifier.isTransient(field.getModifiers())) {
                    sql.append(fieldName).append((++i < numberOfFiels) ? ", " : "");
                }
            }
            sql.append(") VALUES (");
            for (int i1 = 0; i1 < numberOfFiels; i1++) {
                sql.append("?").append((i1 < numberOfFiels - 1) ? "," : "");
            }
            sql.append(")");

            preparedStatement = connection.prepareStatement(sql.toString());

            // insert values in preparedStatement
            int parameterIndex = 1;
            for (Field field : type.getClass().getDeclaredFields()) {
                // handle add of transient field by coverage tools
                if (!Modifier.isTransient(field.getModifiers())) {

                    preparedStatement = addAttributeToPreparedStatement(preparedStatement,
                            parameterIndex, type, field);

                    parameterIndex++;
                }
            }
            //Logger.info(preparedStatement.toString());

            try {
                preparedStatement.executeUpdate();

                // TODO gestione derby
            } catch (DerbySQLIntegrityConstraintViolationException e) {
                //Logger.info(e.getMessage());
            }

        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
            //return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

    private <T> PreparedStatement addAttributeToPreparedStatement(PreparedStatement preparedStatement,
                                                                  int parameterIndex,
                                                                  T type,
                                                                  Field field) throws SQLException, DatabaseException {
        PreparedStatement ps = preparedStatement;
        Type fieldType = field.getGenericType();
        Class clazz = type.getClass();

        //search getter
        Method getter = null;
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get") &&
                    method.getName().toUpperCase().contains(field.getName().toUpperCase())) {
                getter = method;
            }
        }
        if (getter == null) {
            throw new DatabaseException("ORM, Reflection: getter for '" + field.getName() + "' not found in class '" + type.getClass() + "'");
        }

        try {
            //String fieldName = field.getName();
            //Logger.info("Type: " + fieldType + ", Name: " + fieldName + ", Value: " + field.get(type).toString());
            switch (fieldType.getTypeName()) {
                case "java.lang.String":
                    //ps.setString(parameterIndex, field.get(type).toString());
                    ps.setString(parameterIndex, (String) getter.invoke(type));
                    break;
                case "java.lang.Boolean":
                    //preparedStatement.setBoolean(parameterIndex, field.getBoolean(type));
                    //ps.setBoolean(parameterIndex, Boolean.parseBoolean(field.get(type).toString()));
                    ps.setBoolean(parameterIndex, (Boolean) getter.invoke(type));
                    break;
                default:
                    Logger.error("ORM, Reflection: type not mapped, type: " + fieldType);
                    break;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatabaseException("ORM, Reflection: error invoking getter", e);
        }
        return ps;
    }
}