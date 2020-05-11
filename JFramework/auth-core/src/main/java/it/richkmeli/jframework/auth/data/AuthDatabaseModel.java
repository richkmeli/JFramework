package it.richkmeli.jframework.auth.data;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.auth.data.exception.AuthDatabaseException;

import java.util.List;

public interface AuthDatabaseModel {

    List<User> getAllUsers() throws AuthDatabaseException;

    boolean addUser(User user) throws AuthDatabaseException;

    boolean removeUser(String email) throws AuthDatabaseException, ModelException;

    boolean isUserPresent(String email) throws AuthDatabaseException, ModelException;

    boolean editPassword(String email, String pass) throws AuthDatabaseException, ModelException;

    boolean editAdmin(String email, Boolean isAdmin) throws AuthDatabaseException, ModelException;

    boolean checkPassword(String email, String pass) throws AuthDatabaseException, ModelException;

    boolean isAdmin(String email) throws AuthDatabaseException, ModelException;

}
