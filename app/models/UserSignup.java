package models;

import java.util.List;

import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import util.Util;

import com.google.common.collect.Lists;

public class UserSignup {
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

    public void setPassword(String pw) {
        this.password = pw;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = Lists.newArrayList();
        if (User.userExists(email)) {
            errors.add(new ValidationError("email", Messages.get("errors.signup.emailtaken")));
        }
        if (!Util.isValidPassword(password)) {
            errors.add(new ValidationError("password", Messages.get("errors.signup.passwordinvalid")));
        }
        return errors.size() == 0 ? null : errors;
    }

    public static Form<UserSignup> form() {
        return Form.form(UserSignup.class);
    }
}
