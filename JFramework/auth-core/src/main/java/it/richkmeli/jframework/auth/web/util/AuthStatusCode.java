package it.richkmeli.jframework.auth.web.util;


import it.richkmeli.jframework.network.tcp.server.http.payload.response.BaseStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;

/**
 * 1000 --> success
 * 2000 --> generic error
 * 21xx --> account error
 * 22xx --> database error
 * 23xx --> crypto error
 * 24xx --> session error
 */

public class AuthStatusCode extends BaseStatusCode {
    public static final StatusCode NOT_LOGGED = new StatusCode(2100, "User is not logged");
    public static final StatusCode ALREADY_LOGGED = new StatusCode(2101, "Already logged in");
    public static final StatusCode MISSING_FIELD = new StatusCode(2102, "Check input fields");
    public static final StatusCode WRONG_PASSWORD = new StatusCode(2103, "Wrong password");
    public static final StatusCode ACCOUNT_NOT_FOUND = new StatusCode(2104, "Account not found");
    public static final StatusCode ALREADY_REGISTERED = new StatusCode(2105, "Email already registered");
    public static final StatusCode NOT_AUTHORIZED = new StatusCode(2106, "The current user is not authorized");
    public static final StatusCode MODEL_ERROR = new StatusCode(2107, "Info are not valid");
}

