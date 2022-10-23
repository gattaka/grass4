package cz.gattserver.grass.core.ui.dialogs;

import java.text.DecimalFormat;

import cz.gattserver.grass.core.ui.components.BaseProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.server.Command;

import cz.gattserver.web.common.ui.dialogs.WebDialog;

public class ProgressDialog extends WebDialog {

	private static final Logger logger = LoggerFactory.getLogger(ProgressDialog.class);

	private static final long serialVersionUID = 2779568469991016255L;
	private static DecimalFormat myFormatter = new DecimalFormat("##0.0");

	private BaseProgressBar progressBar;
	private Label progressItemLabel;
	private Label descriptionLabel;
	private UI ui;

	public static void runInUI(Command r, UI ui) {
		if (ui.getSession() == null) {
			logger.warn("UI nemá session");
			r.execute();
		} else {
			ui.access(r);
		}
	}

	@Override
	public void close() {
		runInUI(() -> {
			ui.setPollInterval(-1);
			ProgressDialog.super.close();
		});
	}

	public void runInUI(Command r) {
		ProgressDialog.runInUI(r, ui);
	}

	public void indicateProgress(String msg) {
		progressBar.increaseProgress();
		progressItemLabel.setText(myFormatter.format(progressBar.getProgress() * 100) + "%");
		descriptionLabel.setText(msg);
	}

	public ProgressDialog setTotal(int total) {
		progressBar.setTotal(total);
		return this;
	}

	public int getTotal() {
		return progressBar.getTotal();
	}

	public ProgressDialog() {
		this.ui = UI.getCurrent();

		ui.setPollInterval(200);

		setWidth("300px");

		VerticalLayout processWindowLayout = new VerticalLayout();
		processWindowLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		addComponent(processWindowLayout);

		processWindowLayout.setPadding(true);
		processWindowLayout.setSpacing(true);
		processWindowLayout.setSizeFull();

		progressItemLabel = new Label("0.0%");
		progressItemLabel.setWidth(null);

		progressBar = new BaseProgressBar();
		progressBar.setIndeterminate(false);
		progressBar.setValue(0f);

		descriptionLabel = new Label();
		descriptionLabel.setWidth(null);

		processWindowLayout.add(progressItemLabel);
		processWindowLayout.add(progressBar);
		processWindowLayout.add(descriptionLabel);

		open();
	}

}
