package models;

import play.data.Form;
import play.data.validation.Constraints;

public class UserSignin {
    @Constraints.Required
    @Constraints.Email
    public String email;
    @Constraints.Required
    @Constraints.MinLength(6)
    public String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Form<UserSignin> form() {
        return Form.form(UserSignin.class);
    }
}