//package it.richkmeli.jframework.database;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public interface DatabaseModel {
//    void init() throws DatabaseException;
//
//    void init(String database) throws DatabaseException;
//
//    Connection connect() throws DatabaseException;
//
//    void disconnect(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws DatabaseException;
//
//    boolean execute(String string) throws DatabaseException;
//}
