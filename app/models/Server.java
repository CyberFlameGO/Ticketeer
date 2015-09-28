package models;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
import play.mvc.Controller;

import com.avaje.ebean.Query;

@Entity
public class Server extends Model {
    public Date expiration;
    @Id
    public long id;
    public String name;
    @ManyToOne(fetch = FetchType.LAZY)
    public User owner;
    public String secret;
    @Enumerated(EnumType.STRING)
    public SubscriptionType type = SubscriptionType.STANDARD;

    public Server(User owner, String name) {
        this.owner = owner;
        this.name = name;
        this.secret = UUID.randomUUID().toString().replace("-", "");
    }

    public Collection<Ticket> getAllTickets() {
        return Ticket.getByServer(this);
    }

    public Query<Ticket> getAllTicketsQuery() {
        return Ticket.getByServerQuery(this);
    }

    public boolean isVisibleFrom(User user) {
        return owner.equals(user);
    }

    public enum SubscriptionType {
        STANDARD;
    }

    public static Server byCredentials(String name, String secret) {
        return finder.where().eq("name", name).eq("secret", secret).findUnique();
    }

    public static Server byName(String name) {
        return finder.where().eq("name", name).findUnique();
    }

    public static Server byOwner(User user) {
        return finder.where().eq("owner", user).findUnique();
    }

    public static Server currentServer() {
        String[] parts = Controller.request().host().split("\\.");
        return parts.length < 3 || parts[0].equalsIgnoreCase("www") ? null : byName(parts[0]);
    }

    public static boolean hasOwner(User user) {
        return finder.where().eq("owner", user).findRowCount() != 0;
    }

    public static boolean isOnDefaultServer() {
        String[] parts = Controller.request().host().split("\\.");
        return parts.length < 3;
    }

    public static boolean serverExists(String name, String secret) {
        return !name.isEmpty() && !secret.isEmpty()
                && finder.where().eq("name", name).eq("secret", secret).findRowCount() > 0;
    }

    private static Model.Finder<Long, Server> finder = new Model.Finder<Long, Server>(Long.class, Server.class);
}
