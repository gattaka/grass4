package cz.gattserver.common.stlviewer;

import java.io.Serial;
import java.util.UUID;
import java.util.function.Consumer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.PendingJavaScriptResult;

import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class STLViewer extends Div {

    @Serial
    private static final long serialVersionUID = -359486933044737350L;

    private static final String JS_PATH = "stl_viewer/";
    private static final String STL_VIEWER_INSTANCE_JS_VAR = "$.stlViewerInstance";

    private boolean stlViewerInitialized = false;

    public STLViewer(Consumer<STLViewer> afterLoad) {
        PendingJavaScriptResult result = UIUtils.loadJS(new JScriptItem(JS_PATH + "stl_viewer.min.js"));
        if (afterLoad != null) result.then(json -> afterLoad.accept(this));

        setId("stlcont-" + UUID.randomUUID());
    }

    public void show(String url) {
        String modelDefinition =
                "{filename: \"" + url + "\", " + "animation: {delta: {rotationy: 1, msec: 5000, loop: true}}, " +
                        "color: \"#286708\", view_edges: false}";
        String js;
        if (!stlViewerInitialized) {
            String relativePath = UIUtils.getContextPath() + "/" + JS_PATH;
            js = STL_VIEWER_INSTANCE_JS_VAR + " = new StlViewer(this, { load_three_files: \"" + relativePath +
                    "\", models: [ " + modelDefinition + "] });";
            stlViewerInitialized = true;
        } else {
            js = STL_VIEWER_INSTANCE_JS_VAR + ".clean(); " + STL_VIEWER_INSTANCE_JS_VAR + ".add_model(" +
                    modelDefinition + ");";
        }
        getElement().executeJs(js);
    }
}
