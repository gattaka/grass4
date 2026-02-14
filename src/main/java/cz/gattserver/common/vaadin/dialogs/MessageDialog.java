package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.streams.DownloadHandler;

public class MessageDialog extends WebDialog {

    protected TextArea detailsArea;

    /**
     * @param labelCaption obsah zprávy v okně
     * @param image        ikona okna
     */
    public MessageDialog(String title, String labelCaption, Image image) {
        this(title, labelCaption, null, image);
    }

    /**
     * @param title        nadpis dle události
     * @param labelCaption obsah zprávy v okně
     * @param image        *            ikona okna
     */
    public MessageDialog(String title, String labelCaption, String details, Component image) {
        super(title);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        addComponent(horizontalLayout);

        horizontalLayout.add(image);
        horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, image);

        Div msgLabel = new Div(labelCaption);
        msgLabel.setSizeUndefined();
        horizontalLayout.add(msgLabel);

        createDetails(details);

        Button proceedButton = new Button("OK", event -> close());

        addComponent(proceedButton);
        setComponentAlignment(proceedButton, Alignment.END);
    }

    protected void createDetails(String details) {
        if (details != null) {
            detailsArea = new TextArea();
            detailsArea.setValue(details);
            detailsArea.setEnabled(true);
            detailsArea.setReadOnly(true);
            detailsArea.setWidthFull();
            detailsArea.setHeight("200px");
            addComponent(detailsArea);
        }
    }
}