package models;

import play.data.Form;
import play.data.validation.Constraints;

public class TicketForm {
    @Constraints.Required
    @Constraints.MaxLength(50000)
    public String content;
    @Constraints.Email
    @Constraints.MaxLength(200)
    public String email;
    @Constraints.Required
    @Constraints.MaxLength(200)
    public String title;

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public Ticket serializeToTicket(User user, final Server server) {
        if (user == null) {
            user = User.generateUser(email);
        }
        Ticket ticket = new Ticket(server, user, title);
        ticket.save();
        TicketResponse resp = new TicketResponse(ticket, user, content);
        resp.save();
        return ticket;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Form<TicketForm> form() {
        return Form.form(TicketForm.class);
    }
}
