package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DownloadDialog extends WebDialog {

    @Serial
    private static final long serialVersionUID = -8528508905719270401L;

    public DownloadDialog(String caption, Supplier<DownloadResponse> downloadResponseSupplier,
                          Consumer<DialogCloseActionEvent> onClose) {
        super(caption);
        setWidth(300, Unit.PIXELS);
        addDialogCloseActionListener(onClose::accept);
        Anchor link = new Anchor(DownloadHandler.fromInputStream(e -> downloadResponseSupplier.get()), "Stáhnout");
        link.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        link.setTarget("_blank");
        layout.add(link);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(componentFactory.createDialogStornoLayout(e -> close()));
    }
}