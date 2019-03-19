package it.richkmeli.jframework.auth;

import com.sun.tools.internal.ws.processor.model.ModelException;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.database.DatabaseManager;
import it.richkmeli.jframework.database.DatabaseModel;

import java.util.List;

public interface AuthModel extends DatabaseModel {

    public List<User> refreshUser() throws ModelException;

    public boolean addUser(User user) throws ModelException;

    public boolean removeUser(String email) throws ModelException;

    public boolean isUserPresent(String email) throws ModelException;

    public boolean editPassword(String email, String pass) throws ModelException;

    public boolean editAdmin(String email, Boolean isAdmin) throws ModelException;

    public boolean checkPassword(String email, String pass) throws ModelException;

    public boolean isAdmin(String email) throws ModelException;

}
