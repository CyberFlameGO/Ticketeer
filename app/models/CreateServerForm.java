package models;

import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

public class CreateServerForm {
    @Constraints.Required
    @Constraints.Pattern(value = "[a-zA-Z1-9]+", message = "format.alphanumeric.error")
    public String serverName;

    public void setServerName(String name) {
        this.serverName = name;
    }

    public ValidationError validate() {
        if (Server.byName(serverName) != null)
            return new ValidationError("serverName", "Server name already taken");
        return null;
    }

    public static Form<CreateServerForm> form() {
        return Form.form(CreateServerForm.class);
    }
}
