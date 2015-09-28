package controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import models.Server;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.With;
import util.Util;
import controllers.ServerRequiredAction.ServerRequired;

public class ServerRequiredAction extends Action<ServerRequired> {
    @Override
    public Promise<Result> call(Context ctx) throws Throwable {
        if (configuration.value() && Server.currentServer() == null) {
            return Promise.pure(redirect(controllers.routes.ExternalRoutes.index()));
        } else if (!configuration.value() && !Server.isOnDefaultServer()) {
            return Promise.pure(redirect(Util.HOST_DOMAIN_NAME + ctx.request().path()));
        }
        return delegate.call(ctx);
    }

    @With(ServerRequiredAction.class)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ServerRequired {
        boolean value() default true;
    }
}
