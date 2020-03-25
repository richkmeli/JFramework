package it.richkmeli.jframework.auth;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.orm.DatabaseException;

import java.util.List;

public interface AuthModel {

    List<User> getAllUsers() throws DatabaseException;

    boolean addUser(User user) throws DatabaseException;

    boolean removeUser(String email) throws DatabaseException, ModelException;

    boolean isUserPresent(String email) throws DatabaseException, ModelException;

    boolean editPassword(String email, String pass) throws DatabaseException, ModelException;

    boolean editAdmin(String email, Boolean isAdmin) throws DatabaseException, ModelException;

    boolean checkPassword(String email, String pass) throws DatabaseException, ModelException;

    boolean isAdmin(String email) throws DatabaseException, ModelException;

}
