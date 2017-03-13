package act_site;

import act.app.ActionContext;
import act.app.App;
import act.controller.ParamNames;
import act.handler.builtin.controller.FastRequestHandler;
import act.view.RenderTemplate;
import org.osgl.http.H;
import org.osgl.util.S;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;

/**
 * Responsible for loading the Markdown documents
 */
@Singleton
public class DocLoader extends FastRequestHandler {

    @Inject
    public DocLoader(App app) {
        app.router().addMapping(H.Method.GET, "/doc", this);
    }

    @Override
    public void handle(ActionContext context) {
        String path = context.paramVal(ParamNames.PATH);
        RenderTemplate result = _handle(path, context);
        result.apply(context);
    }

    private RenderTemplate _handle(String path, ActionContext context) {
        Locale locale = context.locale(true);
        String newPath = null;
        if (path.toLowerCase().startsWith("/release_notes")) {
            newPath = "https://raw.githubusercontent.com/actframework/act-doc/master/RELEASE_NOTES.md";
        } else {
            String sLocale = locale.getLanguage();
            String lang = ("zh".equals(sLocale)) ? "cn" : "en";
            StringBuilder sb = S.builder("https://raw.githubusercontent.com/actframework/act-doc/master/").append(lang);
            if (path.contains("#")) {
                String[] pa = path.split("#");
                String pa1 = pa[0];
                sb.append(pa1);
                if (!pa1.endsWith(".md")) {
                    sb.append(".md");
                }
                sb.append("#").append(pa[1]);
            } else {
                sb.append(path);
                if (!path.endsWith(".md")) {
                    sb.append(".md");
                }
            }
            newPath = sb.toString();
        }
        context.renderArg("docPath", newPath);
        context.templatePath("/doc.html");
        return RenderTemplate.get();
    }

    @Override
    public boolean supportPartialPath() {
        return true;
    }
}
