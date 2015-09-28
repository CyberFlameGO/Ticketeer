package controllers;

import java.net.MalformedURLException;

import play.Play;

import com.edulify.modules.sitemap.UrlProvider;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

public class SigninProvider implements UrlProvider {
    @Override
    public void addUrlsTo(WebSitemapGenerator generator) {
        String baseUrl = Play.application().configuration().getString("sitemap.baseUrl");
        WebSitemapUrl url;
        try {
            url = new WebSitemapUrl.Options(String.format("%s%s", baseUrl, controllers.routes.ExternalRoutes.signin(null).url()))
                    .changeFreq(ChangeFreq.WEEKLY).priority(0.5).build();
            generator.addUrl(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
