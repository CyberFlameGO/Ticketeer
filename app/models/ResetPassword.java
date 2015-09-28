package models;

import java.util.List;

import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import util.Util;

import com.google.common.collect.Lists;

public class ResetPassword {
    @Constraints.Required
    public String email;
    @Constraints.Required
    @Constraints.MinLength(6)
    public String password;
    @Constraints.Required
    public String token;

    public List<ValidationError> validate() {
        List<ValidationError> errors = Lists.newArrayList();
        if (!Util.isValidPassword(password)) {
            errors.add(new ValidationError("password", Messages.get("errors.signup.passwordinvalid")));
        }
        return errors.size() == 0 ? null : errors;
    }

    public static Form<ResetPassword> form() {
        return Form.form(ResetPassword.class);
    }
}
