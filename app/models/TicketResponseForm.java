package models;

import play.data.Form;
import play.data.validation.Constraints;

public class TicketResponseForm {
    @Constraints.Required
    @Constraints.MaxLength(50000)
    public String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Form<TicketResponseForm> form() {
        return Form.form(TicketResponseForm.class);
    }
}
