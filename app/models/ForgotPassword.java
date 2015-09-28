package models;

import play.data.Form;
import play.data.validation.Constraints.Required;

public class ForgotPassword {
    @Required
    public String email;

    public static Form<ForgotPassword> form() {
        return Form.form(ForgotPassword.class);
    }
}
