package controllers;

import models.Server;
import models.Ticket;
import models.TicketForm;
import models.Token;
import models.Token.TokenType;
import models.User;

import org.joda.time.DateTime;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class APIRoutes extends Controller {
    private static JsonNode addError(ObjectNode node, String string) {
        return node.set("error", TextNode.valueOf(string));
    }

    public static Result createTicket() {
        JsonNode json = request().body().asJson();
        ObjectNode response = new ObjectNode(MAPPER.getNodeFactory());
        if (json == null) {
            return badRequest(addError(response, Messages.get("api.json-missing")));
        }
        if (!verifyCredentials(json)) {
            return ok(addError(response, Messages.get("api.unauthorised")));
        }

        Form<TicketForm> form = TicketForm.form().bind(json);
        if (form.hasErrors()) {
            return badRequest(addError(response, Messages.get("api.create.invalid-parameters")));
        }
        TicketForm get = form.get();
        Token token = Token.findByToken(json.path("token").asText());
        if (token == null) {
            return ok(addError(response, Messages.get("api.unauthorised")));
        }
        Ticket ticket = get.serializeToTicket(token.getUser(), getServer(json));
        response.set("ticketURL", TextNode.valueOf(controllers.routes.AppRoutes.viewTicket(ticket.id).absoluteURL(request())));
        return ok(response);
    }

    private static Server getServer(JsonNode json) {
        String name = json.path("auth.name").asText(), secret = json.path("auth.secret").asText();
        return Server.byCredentials(name, secret);
    }

    public static Result userLogin() {
        JsonNode json = request().body().asJson();
        ObjectNode response = new ObjectNode(MAPPER.getNodeFactory());
        if (json == null) {
            return badRequest(addError(response, Messages.get("api.json-missing")));
        }
        if (!verifyCredentials(json)) {
            return ok(addError(response, Messages.get("api.unauthorised")));
        }

        String email = json.path("email").asText();
        String password = json.path("password").asText();
        User user = User.getByEmail(email);
        if (user == null) {
            user = User.create(email, password);
        } else if (!user.validatePassword(password)) {
            return ok(addError(response, Messages.get("api.login.incorrect-password")));
        }
        Token token = new Token(TokenType.USER_VALIDITY, DateTime.now().plusDays(1).toDate(), user);
        token.save();
        response.set("token", TextNode.valueOf(token.token));
        response.set("expiry", TextNode.valueOf(token.getExpirationString()));
        return ok(response);
    }

    private static boolean verifyCredentials(JsonNode json) {
        String name = json.path("auth.name").asText(), secret = json.path("auth.secret").asText();
        return Server.serverExists(name, secret);
    }

    public static Result verifySecret() {
        JsonNode json = request().body().asJson();
        ObjectNode response = new ObjectNode(MAPPER.getNodeFactory());
        if (json == null) {
            return badRequest(addError(response, Messages.get("api.json-missing")));
        }
        if (!verifyCredentials(json)) {
            return ok(addError(response, Messages.get("api.unauthorised")));
        }
        return ok(response);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
}
