package models;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.Token.TokenType;

import org.joda.time.DateTime;

import play.db.ebean.Model;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import util.Util;

import com.lambdaworks.crypto.SCryptUtil;

@Entity
public class User extends Model {
    public String displayName;
    public String email;
    @Id
    public long id;
    public String passwordHash;
    public Date signupDate;
    public boolean verifiedEmail = false;

    public User(String email, String password) {
        this.email = displayName = email;
        this.passwordHash = SCryptUtil.scrypt(password, 16384, 8, 1);
        this.signupDate = new Date();
    }

    public Server getCurrentServer() {
        return Server.byOwner(this);
    }

    public String getNameForTicket() {
        if (displayName != null && !displayName.isEmpty()) {
            return displayName;
        }
        return email == null ? "" : email;
    }

    private String getResetPasswordLink() {
        Token token = new Token(TokenType.RESET_PASSWORD, DateTime.now().plusDays(7).toDate(), this);
        token.save();
        return controllers.routes.ExternalRoutes.resetPassword(email, token.token).absoluteURL(Controller.request());
    }

    public void login() {
        Controller.session(User.SESSION_KEY, email);
    }

    public void logout() {
        Controller.session().remove(User.SESSION_KEY);
    }

    public boolean ownsServer() {
        return Server.hasOwner(this);
    }

    public String registerFromToken(String raw) {
        Token token = Token.findByUserToken(this, raw);
        if (token == null || token.type != TokenType.SIGNUP)
            return Messages.get("errors.invalidtoken");
        if (!token.isValid()) {
            token.delete();
            delete();
            return Messages.get("errors.expiredtoken");
        }
        verifiedEmail = true;
        token.delete();
        save();
        login();
        return null;
    }

    public String resetPassword(String raw, String password) {
        Token token = Token.findByUserToken(this, raw);
        if (token == null || token.type != TokenType.RESET_PASSWORD)
            return Messages.get("errors.invalidtoken");
        if (!token.isValid()) {
            token.delete();
            return Messages.get("errors.expiredtoken");
        }
        updatePassword(password);
        token.delete();
        save();
        return null;
    }

    public void sendPasswordConfirmationEmail(String pwd) {
        Email mail = new Email();
        mail.setSubject("Generated password and account information");
        mail.addTo(email);
        mail.setFrom("Ticketeer <noreply@" + Util.DOMAIN_NAME + ">");
        mail.setBodyText("A temporary password has been generated for you: " + pwd
                + "\nYou can reset your password straight away at the following link: " + getResetPasswordLink());
    }

    public void sendResetPasswordEmail() {
        Email mail = new Email();
        mail.setSubject("Reset password");
        mail.addTo(email);
        mail.setFrom("Ticketeer <noreply@" + Util.DOMAIN_NAME + ">");
        mail.setBodyText("A password reset has been requested for your account.\nIf this request was from you, please use the following link: "
                + getResetPasswordLink());
    }

    public void sendVerifyEmail() {
        Token token = new Token(TokenType.SIGNUP, DateTime.now().plusDays(7).toDate(), this);
        token.save();

        Email mail = new Email();
        mail.setSubject("Complete signup");
        mail.addTo(email);
        mail.setFrom("Ticketeer <noreply@" + Util.DOMAIN_NAME + ">");
        mail.setBodyText("Verify your email here to complete your signup: "
                + controllers.routes.ExternalRoutes.registerUser(email, token.token).absoluteURL(Controller.request()));
        MailerPlugin.send(mail);
    }

    public void setDisplayName(String name) {
        if (name != null && name.isEmpty()) {
            name = null;
        }
        this.displayName = name;
        save();
    }

    public void updatePassword(String rawPassword) {
        String password = SCryptUtil.scrypt(rawPassword, 16384, 8, 1);
        passwordHash = password;
        save();
    }

    public boolean validatePassword(String password) {
        return SCryptUtil.check(password, passwordHash);
    }

    public static User create(String email, String password) {
        User user = new User(email, password);
        user.save();
        return user;
    }

    public static User createFromData(UserSignup form) {
        if (User.userExists(form.email)) {
            return null;
        }
        String email = form.email;
        String password = form.password;
        User user = new User(email, password);
        user.save();
        return user;
    }

    public static User generateUser(String signupEmail) {
        String randomPassword = Util.generateRandomPassword();
        User user = new User(signupEmail, randomPassword);
        user.sendPasswordConfirmationEmail(randomPassword);
        user.login();
        return user;
    }

    public static User getByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static boolean isSessionAuthenticated() {
        return sessionUser() != null;
    }

    public static User sessionUser() {
        Map<String, Object> args = Controller.ctx().args;
        return (User) args.computeIfAbsent("user", new Function<String, Object>() {
            @Override
            public Object apply(String k) {
                return getByEmail(Controller.session(SESSION_KEY));
            }
        });
    }

    public static boolean userExists(String email) {
        return find.where().eq("email", email).findRowCount() == 1;
    }

    private static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);
    private static final String SESSION_KEY = "username";
}
