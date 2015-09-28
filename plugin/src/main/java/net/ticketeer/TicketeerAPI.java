package net.ticketeer;

import java.text.ParseException;

import net.ticketeer.command.CommandMessages;
import net.ticketeer.util.Translator;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class TicketeerAPI {
    private String API_DOMAIN = "http://ticketeer.net/api/v1";
    private String CREATE;
    private String DOMAIN_NAME = "ticketeer.net";
    private String LOGIN_USER;
    private String name;
    private String secret;
    private final TicketeerUserAuthentication userAuth = new TicketeerUserAuthentication();
    private boolean verified;
    private String VERIFY;

    private ObjectNode addCredentials(ObjectNode node) {
        node.set("auth.name", TextNode.valueOf(name));
        node.set("auth.secret", TextNode.valueOf(secret));
        return node;
    }

    public void configure(FileConfiguration config) {
        setCredentials(config.getString("name"), config.getString("secret"));
        API_DOMAIN = config.getString("domains.api", "http://ticketeer.net/api/v1");
        DOMAIN_NAME = config.getString("domains.name", "ticketeer.net");
        VERIFY = API_DOMAIN + "/verify";
        LOGIN_USER = API_DOMAIN + "/user/login";
        CREATE = API_DOMAIN + "/ticket/create";
    }

    public String getNewTicketURL(CommandSender sender) {
        String base = "http://" + name + "." + DOMAIN_NAME + "/create-ticket";
        if (sender instanceof Player) {
            /*try {
                base += "?mcu=" + URLEncoder.encode(sender.getName(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }*/
        }
        return base;
    }

    public boolean isLoggedIn(CommandSender sender) {
        return userAuth.isLoggedIn(sender);
    }

    public boolean isVerified() {
        return verified;
    }

    public String loginUser(CommandSender sender, String email, String password) {
        ObjectNode request = new ObjectNode(MAPPER.getNodeFactory());
        request.set("email", TextNode.valueOf(email));
        request.set("password", TextNode.valueOf(password));
        JsonNode response = post(LOGIN_USER, addCredentials(request));
        if (response.isMissingNode()) {
            return Translator.translate(CommandMessages.UNABLE_TO_CONTACT_TICKETEER);
        } else if (!response.path("error").asText().isEmpty()) {
            return response.path("error").asText();
        }
        try {
            userAuth.login(sender, response);
        } catch (ParseException e) {
            return Translator.translate(CommandMessages.DATE_PARSE_ERROR);
        }
        return "";
    }

    public void setCredentials(String name, String secret) {
        this.name = name == null ? "" : name;
        this.secret = secret == null ? "" : secret;
    }

    public String submitTicket(CommandSender sender, String title, String content) {
        if (!isLoggedIn(sender))
            return "";
        ObjectNode request = new ObjectNode(MAPPER.getNodeFactory());
        request.set("token", TextNode.valueOf(userAuth.getAuthToken(sender)));
        request.set("title", TextNode.valueOf(title));
        request.set("content", TextNode.valueOf(content));
        JsonNode response = post(CREATE, addCredentials(request));
        return response.path("ticketURL").asText();
    }

    public boolean verifyServerCredentials() {
        if (name.isEmpty() || secret.isEmpty())
            return false;
        JsonNode response = post(VERIFY, addCredentials(MAPPER.createObjectNode()));
        verified = !response.isMissingNode() && response.path("error").asText().isEmpty();
        return verified;
    }

    private static JsonNode post(String url, JsonNode contents) {
        try {
            return MAPPER.readTree(Request.Post(url).bodyString(contents.toString(), ContentType.APPLICATION_JSON)
                    .connectTimeout(10000).execute().returnContent().asString());
        } catch (Exception e) {
        }
        return MissingNode.getInstance();
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
}
