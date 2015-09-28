package controllers;

import java.util.Map;

import models.CreateServerForm;
import models.Server;
import models.Ticket;
import models.TicketForm;
import models.TicketResponse;
import models.TicketResponseForm;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import com.avaje.ebean.Page;
import com.avaje.ebean.Query;

import controllers.ServerRequiredAction.ServerRequired;

public class AppRoutes extends Controller {
    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result changeUsername() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String name = form.get("displayname");
        User.sessionUser().setDisplayName(name);
        return ok();
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result closeTicket(Long id) {
        Ticket ticket = Ticket.byId(id);
        if (ticket == null || !ticket.isClosableBy(User.sessionUser()))
            return badRequest();
        ticket.close();
        return ok();
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result createServer() {
        if (User.sessionUser().ownsServer())
            return dashboard(null, 0);
        return ok(views.html.app.createServer.render(CreateServerForm.form()));
    }

    @ServerRequired()
    public static Result createTicket() {
        Form<TicketForm> form = TicketForm.form();
        return ok(views.html.app.createTicket.render(form));
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result dashboard(final String query, int page) {
        User sessionUser = User.sessionUser();
        Server current = sessionUser.getCurrentServer();
        Page<Ticket> tickets = filterTickets(
                current == null ? Ticket.getByParticipantQuery(sessionUser) : current.getAllTicketsQuery(), query, page);
        return ok(views.html.app.ticketDashboard.render(tickets, query));
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result editResponse(Long id) {
        TicketResponse response = TicketResponse.byId(id);
        Map<String, String[]> formData = request().body().asFormUrlEncoded();
        if (response == null || !response.isEditableBy(User.sessionUser()) || formData == null
                || !formData.containsKey("content"))
            return badRequest();
        String[] content = formData.get("content");
        if (content.length == 0 || content[0] == null)
            return badRequest();
        response.setBody(content[0]);
        response.save();
        return ok();
    }

    private static Page<Ticket> filterTickets(Query<Ticket> dbQuery, String query, int page) {
        dbQuery.where().eq("open", true);
        if (query != null) {
            dbQuery.where().disjunction().contains("submitter.email", query).contains("title", query)
                    .contains("responses.rawText", query);
        }
        return dbQuery.findPagingList(20).getPage(page);
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result removeTicketResponse(Long id) {
        TicketResponse response = TicketResponse.byId(id);
        if (response == null || !response.isEditableBy(User.sessionUser()))
            return badRequest();
        response.delete();
        return ok();
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result settings() {
        return ok(views.html.app.settings.render());
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result submitCreateServer() {
        Form<CreateServerForm> f = CreateServerForm.form().bindFromRequest();
        if (f.hasErrors())
            return ok(views.html.app.createServer.render(f));
        Server server = new Server(User.sessionUser(), f.get().serverName);
        server.save();
        return ok(views.html.app.serverCreated.render(server));
    }

    @ServerRequired()
    public static Result submitTicket() {
        Form<TicketForm> form = TicketForm.form().bindFromRequest();
        if (User.sessionUser() == null) {
            String email = form.apply("email").value();
            if (email == null || email.isEmpty()) {
                form.reject(new ValidationError("email", Messages.get("forms.ticket.contact-email-required")));
            } else if (User.userExists(email)) {
                form.reject(new ValidationError("email", Messages.get("forms.ticket.contact-email-taken")));
            }
        }
        if (form.hasErrors())
            return ok(views.html.app.createTicket.render(form));
        Server server = Server.currentServer();
        if (server == null)
            return badRequest();
        Ticket ticket = form.get().serializeToTicket(User.sessionUser(), server);
        return redirect(controllers.routes.AppRoutes.viewTicket(ticket.id));
    }

    public static Result submitTicketResponse(Long id) {
        Ticket ticket = Ticket.byId(id);
        if (ticket == null)
            return notFound();
        Form<TicketResponseForm> form = TicketResponseForm.form().bindFromRequest();
        if (!ticket.isAcceptingResponses()) {
            form.reject(Messages.get("forms.response.ticket-closed"));
        }
        if (form.hasErrors())
            return ok(views.html.app.viewTicket.render(ticket, ticket.getOrderedResponses(), form));
        ticket.addResponse(User.sessionUser(), form.get().content);
        return redirect(controllers.routes.AppRoutes.viewTicket(id));
    }

    @Authenticated(UserAuthenticator.class)
    @ServerRequired(false)
    public static Result userTickets(final String query, int page) {
        Page<Ticket> tickets = filterTickets(Ticket.getByParticipantQuery(User.sessionUser()), query, page);
        return ok(views.html.app.userTickets.render(tickets, query));
    }

    public static Result viewTicket(Long id) {
        return viewTicket(Ticket.byId(id));
    }

    public static Result viewTicket(Ticket ticket) {
        if (ticket == null || !ticket.isVisibleFrom(User.sessionUser()))
            return notFound();
        return ok(views.html.app.viewTicket.render(ticket, ticket.getOrderedResponses(), TicketResponseForm.form()));
    }
}