package org.apache.karaf.branding.webconsole;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.json.JSONException;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Created by heathkesler on 9/22/14.
 */
public class BrandingPlugin extends AbstractWebConsolePlugin {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(BrandingPlugin.class);

    public static final String NAME = "branding";
    public static final String LABEL = "Branding";
    private ClassLoader classLoader;
    private String cellarJs = "/cellar/res/ui/cellar.js";

    public void start() {
        this.classLoader = this.getClass().getClassLoader();
        this.LOGGER.info("{} plugin activated", LABEL);
    }

    public void stop() {
        this.LOGGER.info("{} plugin deactivated", LABEL);
        super.deactivate();
    }

    @Override
    public String getLabel() {
        return NAME;
    }

    @Override
    public String getTitle() {
        return LABEL;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = false;

        final String action = req.getParameter("action");
        final String node = req.getParameter("node");
        final String group = req.getParameter("group");
        final String id = req.getParameter("id");

        if (action == null) {
            success = true;
        } else if (action.equals("createGroup")) {
            success = true;
        } else if (action.equals("deleteGroup")) {
            success = true;
        }

        if (success) {
            // let's wait a little bit to give the framework time
            // to process our request
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                // ignore
            }
            this.renderJSON(resp, null);
        } else {
            super.doPost(req, resp);
        }
    }

    @Override
    protected void renderContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get request info from request attribute
        final PrintWriter pw = response.getWriter();

        String appRoot = (String) request.getAttribute("org.apache.felix.webconsole.internal.servlet.OsgiManager.appRoot");
        final String featuresScriptTag = "<script src='" + appRoot + this.cellarJs
                + "' language='JavaScript'></script>";
        pw.println(featuresScriptTag);

        pw.println("<script type='text/javascript'>");
        pw.println("// <![CDATA[");
        pw.println("var imgRoot = '" + appRoot + "/res/imgs';");
        pw.println("// ]]>");
        pw.println("</script>");

        pw.println("<div id='plugin_content'/>");

        pw.println("<script type='text/javascript'>");
        pw.println("// <![CDATA[");
        pw.print("renderGroups( ");
        writeJSON(pw);
        pw.println(" )");
        pw.println("// ]]>");
        pw.println("</script>");
    }

    protected URL getResource(String path) {
        path = path.substring(NAME.length() + 1);
        if (path == null || path.isEmpty()) {
            return null;
        }
        URL url = this.classLoader.getResource(path);
        if (url != null) {
            InputStream ins = null;
            try {
                ins = url.openStream();
                if (ins == null) {
                    this.LOGGER.error("failed to open {}", url);
                    url = null;
                }
            } catch (IOException e) {
                this.LOGGER.error(e.getMessage(), e);
                url = null;
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        this.LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        return url;
    }

    private void renderJSON(final HttpServletResponse response, final String feature) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final PrintWriter pw = response.getWriter();
        writeJSON(pw);
    }

    private void writeJSON(final PrintWriter pw) throws IOException {

    }

    private void action(JSONWriter jw, boolean enabled, String op, String title, String image) throws JSONException {
        jw.object();
        jw.key("enabled").value(enabled);
        jw.key("op").value(op);
        jw.key("title").value(title);
        jw.key("image").value(image);
        jw.endObject();
    }


}
