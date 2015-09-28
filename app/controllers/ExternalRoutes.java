package controllers;

import models.ForgotPassword;
import models.ResetPassword;
import models.Server;
import models.User;
import models.UserSignin;
import models.UserSignup;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import util.Util;

import com.edulify.modules.sitemap.SitemapItem;
import com.redfin.sitemapgenerator.ChangeFreq;

import controllers.ServerRequiredAction.ServerRequired;
import controllers.UserAuthenticator.Unauthenticated;

public class ExternalRoutes extends Controller {
    @Unauthenticated
    @SitemapItem(changefreq = ChangeFreq.MONTHLY)
    public static Result forgotPassword() {
        return ok(views.html.external.forgotPassword.render(ForgotPassword.form()));
    }

    @SitemapItem
    public static Result index() {
        if (Server.currentServer() != null)
            return AppRoutes.createTicket();
        if (!Server.isOnDefaultServer())
            return redirect(Util.HOST_DOMAIN_NAME);
        if (User.sessionUser() != null)
            return redirect(controllers.routes.AppRoutes.dashboard(null, 0));
        return ok(views.html.external.index.render(UserSignup.form()));
    }

    @ServerRequired(false)
    public static Result pricing() {
        return ok(views.html.external.pricing.render());
    }

    @Unauthenticated
    public static Result registerUser(String email, String token) {
        if (email.trim().isEmpty() || token.trim().isEmpty())
            return ok();
        User user = User.getByEmail(email);
        if (user == null)
            return ok();
        String message = user.registerFromToken(token);
        if (message != null) {
            return ok(views.html.external.verifyFailed.render(message));
        } else {
            return AppRoutes.createServer();
        }
    }

    @Unauthenticated
    public static Result resetPassword(String email, String token) {
        if (email.trim().isEmpty() || token.trim().isEmpty())
            return ok();
        User user = User.getByEmail(email);
        if (user == null)
            return ok();
        Form<ResetPassword> form = ResetPassword.form();
        form.data().put("email", email);
        form.data().put("token", token);
        return ok(views.html.external.resetPassword.render(form));
    }

    @Unauthenticated
    public static Result signin(String uri) {
        if (uri != null) {
            Controller.session("returnTo", uri);
        }
        return ok(views.html.external.signin.render(UserSignin.form()));
    }

    @Authenticated(UserAuthenticator.class)
    public static Result signout() {
        User session = User.sessionUser();
        if (session != null) {
            session.logout();
        }
        return redirect(controllers.routes.ExternalRoutes.index());
    }

    @Unauthenticated
    @SitemapItem(changefreq = ChangeFreq.MONTHLY)
    public static Result signup() {
        return ok(views.html.external.signup.render(UserSignup.form()));
    }

    @Unauthenticated
    public static Result submitForgotPassword() {
        Form<ForgotPassword> form = ForgotPassword.form().bindFromRequest();
        if (form.hasErrors())
            return ok(views.html.external.forgotPassword.render(form));
        User user = User.getByEmail(form.get().email);
        if (user != null) {
            user.sendResetPasswordEmail();
        }
        form.reject(new ValidationError("alert-success", Messages.get("forms.forgotpassword.success")));
        return ok(views.html.external.forgotPassword.render(form));
    }

    @Unauthenticated
    public static Result submitResetPassword() {
        Form<ResetPassword> form = ResetPassword.form().bindFromRequest();
        if (form.hasErrors())
            return ok(views.html.external.resetPassword.render(form));
        ResetPassword data = form.get();
        User user = User.getByEmail(data.email);
        if (user == null)
            return ok();
        String error = user.resetPassword(data.token, data.password);
        if (error != null) {
            form.globalErrors().add(new ValidationError("?", error));
            return ok(views.html.external.resetPassword.render(form));
        }
        user.login();
        return redirect(controllers.routes.ExternalRoutes.index());
    }

    public static Result submitSignin() {
        if (!User.isSessionAuthenticated()) {
            Form<UserSignin> form = UserSignin.form().bindFromRequest();
            if (form.hasErrors())
                return ok(views.html.external.signin.render(form));
            UserSignin data = form.get();
            User user = User.getByEmail(data.email);
            if (user == null || !user.validatePassword(data.password)) {
                form.reject(new ValidationError("", Messages.get("forms.signin.unknown-user")));
                return ok(views.html.external.signin.render(form));
            }
            user.login();
        }
        if (Controller.session("returnTo") != null && !Controller.session("returnTo").isEmpty()) {
            return redirect(Controller.session("returnTo"));
        }
        return redirect(controllers.routes.ExternalRoutes.index());
    }

    @Unauthenticated
    public static Result submitSignup() {
        play.data.Form<UserSignup> form = UserSignup.form().bindFromRequest();
        if (form.hasErrors())
            return ok(views.html.external.signup.render(form));
        User user = User.createFromData(form.get());
        if (user == null)
            return ok(views.html.external.signup.render(form));
        user.login();
        return redirect(controllers.routes.AppRoutes.dashboard(null, 0));
    }
}