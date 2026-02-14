package cz.gattserver.common.stlviewer;

import java.util.UUID;
import java.util.function.Consumer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.function.SerializableConsumer;

import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.util.UIUtils;
import tools.jackson.databind.JsonNode;

public class STLViewer extends Div {

	private static final String JS_PATH = "stl_viewer/";
	private static final String STL_VIEWER_INSTANCE_JS_VAR = "$.stlViewerInstance";

	private boolean stlViewerInitialized = false;

	public STLViewer(Consumer<STLViewer> afterLoad) {
		PendingJavaScriptResult result = UIUtils.loadJS(new JScriptItem(JS_PATH + "stl_viewer.min.js"));
		if (afterLoad != null)
			result.then(json -> afterLoad.accept(this));

		setId("stlcont-" + UUID.randomUUID());
	}

	public void show(String url) {
		String modelDefinition = "{filename: \"" + url + "\", "
				+ "animation: {delta: {rotationy: 1, msec: 5000, loop: true}}, "
				+ "color: \"#286708\", view_edges: false}";
		String js;
		if (!stlViewerInitialized) {
			String relativePath = UIUtils.getContextPath() + "/" + JS_PATH;
			js = STL_VIEWER_INSTANCE_JS_VAR + " = new StlViewer(document.getElementById(\"" + getId().get()
					+ "\"), { load_three_files: \"" + relativePath + "\", models: [ " + modelDefinition + "] });";
			stlViewerInitialized = true;
		} else {
			js = STL_VIEWER_INSTANCE_JS_VAR + ".clean(); " + STL_VIEWER_INSTANCE_JS_VAR + ".add_model("
					+ modelDefinition + ");";
		}
		UI.getCurrent().getPage().executeJs(js);
	}
}
