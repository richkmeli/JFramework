package it.richkmeli.jframework.network.tcp.server.http.payload.response;

/**
 * Code:
 * 1000 --> success
 * 2000 --> generic error
 * 21xx --> account error
 * 22xx --> database error
 * 23xx --> crypto error
 * 24xx --> session error
 * <p>
 * second digit = service type
 * third and last digit = error code
 */

public class BaseStatusCode {
    public static final StatusCode SUCCESS = new StatusCode(1000, "");
    public static final StatusCode GENERIC_ERROR = new StatusCode(2000, "");

    public static final StatusCode SECURE_CONNECTION = new StatusCode(2300, "Secure Connection error");

    public static final StatusCode JFRAMEWORK_SESSIONID_ERROR = new StatusCode(2400, "JFramework Session ID Error. Login Required.");

    public static final StatusCode DB_ERROR = new StatusCode(2500, "Database Error"); // TODO cambiare in 2200, controllare che in RMC non siano
    public static final StatusCode DB_FIELD_EMPTY = new StatusCode(2501, "Field empty in Database");

}



