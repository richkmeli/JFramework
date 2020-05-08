package it.richkmeli.jframework.auth.web.util;

import it.richkmeli.jframework.auth.AuthDatabaseJframeworkManager;
import it.richkmeli.jframework.auth.AuthDatabaseModel;
import it.richkmeli.jframework.orm.DatabaseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AuthServletJframeworkManager extends AuthServletManager {


    public AuthServletJframeworkManager(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public AuthServletJframeworkManager(AuthServletManager authServletManager) {
        super(authServletManager);
    }

    @Override
    protected AuthDatabaseModel getAuthDatabaseManagerInstance() throws DatabaseException {
        return new AuthDatabaseJframeworkManager();
    }
}
