package net.ticketeer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

public class TicketeerUserAuthentication {
    private final Map<CommandSender, CachedAuthentication> loggedInUsers = Maps.newHashMap();

    public String getAuthToken(CommandSender sender) {
        return loggedInUsers.get(sender).token;
    }

    public boolean isLoggedIn(CommandSender sender) {
        CachedAuthentication auth = loggedInUsers.get(sender);
        return auth != null && auth.isValid();
    }

    public void login(CommandSender sender, JsonNode response) throws ParseException {
        String token = response.path("token").asText();
        Date expiry;
        expiry = TIME_FORMAT.parse(response.path("expiry").asText());
        loggedInUsers.put(sender, new CachedAuthentication(token, expiry));
    }

    private static class CachedAuthentication {
        public Date expiry;
        public String token;

        public CachedAuthentication(String token, Date expiry) {
            this.expiry = expiry;
            this.token = token;
        }

        public boolean isValid() {
            return new Date().before(expiry);
        }
    }

    private static DateFormat TIME_FORMAT = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss");
}
