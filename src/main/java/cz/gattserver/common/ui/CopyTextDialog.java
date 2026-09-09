package cz.gattserver.common.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import cz.gattserver.common.vaadin.HtmlDiv;

import java.io.Serial;
import java.util.UUID;

public class CopyTextDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = -4441925431173513610L;

    public CopyTextDialog(String value) {
        String id = UUID.randomUUID().toString();
        String checkId = "check-" + id;
        HtmlDiv text = new HtmlDiv(
                "<input style=\"width: inherit\" id=\"" + id + "\" value=\"" +value + "\"/>" +
                        "<br/><span id=\"" + checkId + "\" onload=''></span>");
        text.getStyle().set("width", "400px").set("text-align", "center").set("line-height", "30px")
                .set("color", "dodgerblue").set("font-weight", "bold");
        add(text);

        // musí mít mírný timeout, jinak bude referencovat ještě
        // nevykreslený element a dotaz podle ID bude null
        UI.getCurrent().getPage().executeJs(
                "setTimeout(function(){" + "document.getElementById(\"" + id + "\").select(); " +
                        "if (document.execCommand(\"copy\")) { " + "document.getElementById(\"" + checkId +
                        "\").innerHTML = \"URL zkopírováno do schránky\";" + "}" + "},10)");
    }
}
