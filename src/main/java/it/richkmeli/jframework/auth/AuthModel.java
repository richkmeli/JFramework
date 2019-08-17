package it.richkmeli.jframework.auth;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.orm.DatabaseException;

import java.util.List;

public interface AuthModel {

    List<User> getAllUsers() throws DatabaseException;

    boolean addUser(User user) throws DatabaseException;

    boolean removeUser(String email) throws DatabaseException;

    boolean isUserPresent(String email) throws DatabaseException;

    boolean editPassword(String email, String pass) throws DatabaseException;

    boolean editAdmin(String email, Boolean isAdmin) throws DatabaseException;

    boolean checkPassword(String email, String pass) throws DatabaseException;

    boolean isAdmin(String email) throws DatabaseException;

}
