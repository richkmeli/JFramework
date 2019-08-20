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

        try {
            execute(tableSQL);
        } catch (DatabaseException e) {
            // for Derby DB
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
            StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName);

            // search primary key
            preparedStatement = createPreparedStatementWithPrimaryKey(connection, sql, type);

            resultSet = preparedStatement.executeQuery();
            List<T> list = getListFromResultSet(type.getClass(), resultSet);
            if (!list.isEmpty()) {
                elem = (T) list.get(0);
            } else {
                disconnect(connection, preparedStatement, resultSet);
                Logger.error("No " + type.getClass().getName() + " found with this (PrimaryKey)");
                //throw new DatabaseException("No " + type.getClass().getName() + " found with this " + field.getName() + " (PrimaryKey)");
            }

        } catch (
                SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException(e);
            //return false;
        }

        disconnect(connection, preparedStatement, resultSet);
        return elem;
    }

    private <T> PreparedStatement createPreparedStatementWithPrimaryKey(Connection connection,
                                                                        StringBuilder sql, T type) throws SQLException, DatabaseException {
        PreparedStatement preparedStatement = null;

        List<Field> valorizedFields = new ArrayList<>();
        boolean update = false;
        if (sql.toString().contains("#SET#")) {
            update = true;
            sql.replace(sql.indexOf("#SET#"), sql.indexOf("#SET#") + 5, "SET");
        }
        // search valorized fields in "type" passed as parameter, which they aren't part of the primaryKey
        if (update) {
            for (Field field2 : type.getClass().getDeclaredFields()) {
                Annotation annotation = field2.getAnnotation(Id.class);
                if (annotation == null) {
                    Method getter = searchFieldGetter(type, field2);
                    try {
                        Object o = getter.invoke(type);
                        if (o != null && !Modifier.isTransient(field2.getModifiers())) {
                            valorizedFields.add(field2);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        disconnect(connection, null, null);
                        throw new DatabaseException("ORM, Reflection: error invoking getter of " + field2.getName());
                    }
                }
            }
            // add to sql string the fields to be updated
            int i = 0;
            for (Field field : valorizedFields) {
                sql.append(field.getName()).append(" = ?").append((++i < valorizedFields.size()) ? ", " : "");
            }
        }

        sql.append(" WHERE ");
        List<Field> primaryKey = getPrimaryKey(type);
        if (!primaryKey.isEmpty()) {
            int i = 1;
            for (Field field : primaryKey) {
                sql.append(field.getName()).append(" = ?");
                if (i++ < primaryKey.size()) {
                    sql.append(" AND ");
                }
            }

            preparedStatement = connection.prepareStatement(sql.toString());

            i = 1;
            if (update) {
                for (Field field : valorizedFields) {
                    preparedStatement = addAttributeToPreparedStatement(preparedStatement,
                            i++, type, field);
                }
            }

            for (Field field : primaryKey) {
                preparedStatement = addAttributeToPreparedStatement(preparedStatement,
                        i++, type, field);
            }
        } else {
            disconnect(connection, preparedStatement, null);
            throw new DatabaseException("ORM, Reflection: PrimaryKey not found");
            //return false;
        }
        return preparedStatement;
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

    private <T> List<T> getListFromResultSet(Class clazz, ResultSet resultSet) throws
            DatabaseException, SQLException {
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
                                String valueS = resultSet.getString(field.getName());
                                objList.add(valueS);
                                break;
                            case "java.lang.Boolean":
                                Boolean valueB = resultSet.getBoolean(field.getName());
                                objList.add(valueB);
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

    protected <T> boolean update(T type) throws DatabaseException {
        return update(type, null);
    }

    protected <T> boolean update(T type, DBManagerAction dbManagerAction) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if (dbManagerAction != null) {
            type = (T) dbManagerAction.action(type);
        }

        try {
            connection = connect();
            // create SQL string
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " #SET# ");
            // search primary key
            preparedStatement = createPreparedStatementWithPrimaryKey(connection, sql, type);

            try {
                preparedStatement.executeUpdate();
                // for Derby DB
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
            StringBuilder sql = new StringBuilder("DELETE FROM " + tableName);

            // search primary key
            preparedStatement = createPreparedStatementWithPrimaryKey(connection, sql, type);

            try {
                preparedStatement.executeUpdate();
                // for Derby DB
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

    private <T> List<Field> getPrimaryKey(T type) {
        List<Field> primaryKey = new ArrayList<>();
        for (Field field2 : type.getClass().getDeclaredFields()) {
            Annotation annotation = field2.getAnnotation(Id.class);
            if (annotation != null) {
                primaryKey.add(field2);
            }
        }
        return primaryKey;
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

                // for Derby DB
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
        Method getter = searchFieldGetter(type, field);

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

    private <T> Method searchFieldGetter(T type, Field field) throws DatabaseException {
        //search getter
        Method getter = null;
        for (Method method : type.getClass().getMethods()) {
            if (method.getName().startsWith("get") &&
                    method.getName().toUpperCase().contains(field.getName().toUpperCase())) {
                getter = method;
            }
        }
        if (getter == null) {
            throw new DatabaseException("ORM, Reflection: getter for '" + field.getName() + "' not found in class '" + type.getClass() + "'");
        }
        return getter;
    }

    private <T> Method searchFieldSetter(T type, Field field) throws DatabaseException {
        //search getter
        Method setter = null;
        for (Method method : type.getClass().getMethods()) {
            if (method.getName().startsWith("set") &&
                    method.getName().toUpperCase().contains(field.getName().toUpperCase())) {
                setter = method;
            }
        }
        if (setter == null) {
            throw new DatabaseException("ORM, Reflection: setter for '" + field.getName() + "' not found in class '" + type.getClass() + "'");
        }
        return setter;
    }
}