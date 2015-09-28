package controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import models.User;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

public class UserAuthenticator extends Security.Authenticator {
    @Override
    public String getUsername(Context ctx) {
        String username = super.getUsername(ctx);
        if (!User.userExists(username))
            return null;
        return super.getUsername(ctx);
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(controllers.routes.ExternalRoutes.signin(ctx.request().path()));
    }

    @With(UnauthenticatedAction.class)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Unauthenticated {
    }

    public static class UnauthenticatedAction extends Action<Unauthenticated> {
        @Override
        public Promise<Result> call(Context ctx) throws Throwable {
            if (ctx.request().username() != null)
                return Promise.<Result> pure(redirect(controllers.routes.AppRoutes.dashboard(null, 0)));
            return delegate.call(ctx);
        }
    }
}
