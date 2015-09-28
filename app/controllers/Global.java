package controllers;

import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import util.Util;

import com.edulify.modules.sitemap.SitemapJob;

public class Global extends GlobalSettings {
    @Override
    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] { CSRFFilter.class, GzipFilter.class };
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(RequestHeader request) {
        return F.Promise.pure(Controller.notFound(views.html.external.notFound.render()));
    }

    @Override
    public void onStart(Application app) {
        Util.configure(app.configuration());
        SitemapJob.startSitemapGenerator();
    }
}