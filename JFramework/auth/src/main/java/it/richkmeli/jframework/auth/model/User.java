package it.richkmeli.jframework.auth.model;


import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.orm.annotation.Id;
import it.richkmeli.jframework.util.regex.RegexManager;
import it.richkmeli.jframework.util.regex.exception.RegexException;

public class User {
    @Id
    private String email;
    private String password;
    private Boolean admin;

    public User(String email, String password, Boolean admin) throws ModelException {
        checkUserIntegrity(email, password, admin);
        this.email = email;
        this.password = password;
        this.admin = admin;
    }


    public User(String email, String password) throws ModelException {
        this(email, password, false);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public static void checkUserIntegrity(String email, String password, Boolean admin) throws ModelException {
        try {
            RegexManager.checkEmailIntegrity(email);
        } catch (RegexException e) {
            throw new ModelException("Email is not valid");
        }
        // ...
    }

}
