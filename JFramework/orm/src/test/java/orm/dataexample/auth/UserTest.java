package orm.dataexample.auth;

import it.richkmeli.jframework.orm.annotation.Id;

public class UserTest {
    // public for REFLECTION
    @Id
    private String email;
    private String password;
    private Boolean admin;

    public UserTest(String email, String password, Boolean admin) {
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public UserTest(String email, String password) {
        this.email = email;
        this.password = password;
        this.admin = false;
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
}
