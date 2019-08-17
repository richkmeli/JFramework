package it.richkmeli.jframework.auth.model;


import it.richkmeli.jframework.orm.annotation.Id;

public class User {
    // public for REFLECTION
    @Id
    public String email;
    public String password;
    public Boolean isAdmin;

    public User(String email, String password, Boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.isAdmin = false;
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

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
