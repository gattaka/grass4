package cz.gattserver.grass.hw.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouteConfiguration;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.ui.dialogs.HWItemChooseDialog;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;

public class UsedInChooser extends CustomField<HWItemOverviewTO> {

    private static final long serialVersionUID = -5660237108485881386L;

    private HWItemOverviewTO value;
    private HorizontalLayout layout;
    private Button clearBtn;
    private Button chooseUsedInBtn;
    private TextField nameField;

    public UsedInChooser(Long ignoreId) {
        setLabel("Je součástí");

        ComponentFactory componentFactory = new ComponentFactory();

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        add(layout);

        clearBtn = new Button("Odebrat", e -> clear());
        clearBtn.setIcon(VaadinIcon.CLOSE.create());
        clearBtn.setVisible(false);
        layout.add(clearBtn);

        chooseUsedInBtn = new Button("Vybrat", VaadinIcon.SEARCH.create(),
                e -> new HWItemChooseDialog(ignoreId, to -> setValue(to)).open());
        layout.add(chooseUsedInBtn);

        nameField = new TextField();
        nameField.setReadOnly(true);
        nameField.setWidthFull();
        componentFactory.attachLink(nameField, f -> UI.getCurrent().getPage()
                .open(RouteConfiguration.forApplicationScope().getUrl(HWItemPage.class, value.getId())));
        nameField.setVisible(false);
        layout.add(nameField);
    }

    private void refresh() {
        if (value == null) {
            nameField.clear();
            nameField.setVisible(false);
            clearBtn.setVisible(false);
            chooseUsedInBtn.setVisible(!isReadOnly());
        } else {
            nameField.setValue(value.getName());
            nameField.setVisible(true);
            clearBtn.setVisible(!isReadOnly());
            chooseUsedInBtn.setVisible(false);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        refresh();
    }

    @Override
    protected HWItemOverviewTO generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(HWItemOverviewTO value) {
        this.value = value;
        refresh();
    }
}