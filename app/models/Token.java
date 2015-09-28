package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

import play.db.ebean.Model;

@Entity
public class Token extends Model {
    public Date expiration;
    @Id
    public long id;
    public String token;
    @Enumerated(EnumType.STRING)
    public TokenType type;
    @ManyToOne(fetch = FetchType.LAZY)
    public User user;

    public Token(TokenType type, Date expiration) {
        this(type, expiration, null);
    }

    public Token(TokenType type, Date expiration, User user) {
        this.type = type;
        this.expiration = expiration;
        this.user = user;
        this.token = UUID.randomUUID().toString().replace("-", "");
    }

    public String getExpirationString() {
        return TIME_FORMAT.format(expiration);
    }

    public User getUser() {
        return user;
    }

    public boolean isValid() {
        return DateTime.now().isBefore(expiration.getTime());
    }

    public enum TokenType {
        RESET_PASSWORD,
        SIGNUP,
        USER_VALIDITY;
    }

    public static Token findByToken(String token) {
        return find.where().eq("token", token).findUnique();
    }

    public static Token findByUserToken(User user, String token) {
        return find.where().eq("user", user).eq("token", token).findUnique();
    }

    private static final Model.Finder<Long, Token> find = new Model.Finder<Long, Token>(Long.class, Token.class);
    private static DateFormat TIME_FORMAT = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss");
}
