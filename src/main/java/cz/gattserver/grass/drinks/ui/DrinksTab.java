package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.drinks.facades.DrinksFacade;
import cz.gattserver.grass.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.common.vaadin.ImageIcon;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class DrinksTab<T extends DrinkTO, O extends DrinkOverviewTO> extends Div {

    private static final long serialVersionUID = 594189301140808163L;

    private transient SecurityService securityService;
    private transient PageFactory drinksPageFactory;
    private transient DrinksFacade drinksFacade;

    private Image image;
    private Div dataLayout;

    protected Grid<O> grid;
    protected O filterTO;
    protected T choosenDrink;

    private HeaderRow filteringHeader;

    public DrinksTab() {
        SpringContextHelper.inject(this);

        filterTO = createNewOverviewTO();
        grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        configureGrid(grid, filterTO);

        populate();

        Div contentLayout = new Div();
        contentLayout.setSizeFull();
        contentLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        contentLayout.setId("drinks-div");
        add(contentLayout);

        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                showDetail(findById(e.getFirstSelectedItem().get().getId()));
                contentLayout.setVisible(true);
            } else {
                showDetail(null);
                contentLayout.setVisible(false);
            }
        });

        // musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
        image = ImageIcon.BUBBLE_16_ICON.createImage("icon");
        image.setVisible(false);
        Div imageLayout = new Div(image);
        imageLayout.setId("drinks-image-div");
        contentLayout.add(imageLayout);

        dataLayout = new Div();
        dataLayout.setWidthFull();
        dataLayout.setId("drinks-data-div");
        contentLayout.add(dataLayout);

        if (getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
            ButtonLayout btnLayout = new ButtonLayout();
            add(btnLayout);
            populateBtnLayout(btnLayout);
        }
    }

    protected HeaderRow getHeaderRow() {
        if (filteringHeader == null) filteringHeader = grid.appendHeaderRow();
        return filteringHeader;
    }

    protected void addNameColumn(Grid<O> grid) {
        // Název
        Column<O> nameColumn = grid.addColumn(O::getName).setHeader("Název").setSortProperty("name").setFlexGrow(100);
        UIUtils.addHeaderTextField(getHeaderRow().getCell(nameColumn), e -> {
            filterTO.setName(e.getValue());
            populate();
        });
    }

    protected void addCountryColumn(Grid<O> grid) {
        // Země původu
        Column<O> countryColumn = grid.addColumn(O::getCountry).setHeader("Země").setSortProperty("country");
        UIUtils.addHeaderTextField(getHeaderRow().getCell(countryColumn), e -> {
            filterTO.setCountry(e.getValue());
            populate();
        });
    }

    protected void addAlcoholColumn(Grid<O> grid) {
        Column<O> alcoholColumn = grid.addColumn(
                        new NumberRenderer<O>(O::getAlcohol, NumberFormat.getNumberInstance(new Locale("cs", "CZ")), null))
                .setHeader("%").setWidth("50px").setFlexGrow(0).setSortProperty("alcohol");
        UIUtils.addHeaderTextField(getHeaderRow().getCell(alcoholColumn), e -> {
            filterTO.setAlcohol(Double.parseDouble(e.getValue()));
            populate();
        });
    }

    protected void addRatingStarsColumn(Grid<O> grid) {
        grid.addColumn(new ComponentRenderer<RatingStars, O>(to -> {
            RatingStars rs = new RatingStars();
            rs.setValue(to.getRating());
            rs.setReadOnly(true);
            rs.setSize("15px");
            return rs;
        })).setHeader("Hodnocení").setWidth("90px").setFlexGrow(0).setSortProperty("rating");
    }

    protected SecurityService getSecurityService() {
        if (securityService == null) securityService = SpringContextHelper.getBean(SecurityService.class);
        return securityService;
    }

    protected PageFactory getDrinksPageFactory() {
        if (drinksPageFactory == null)
            drinksPageFactory = (PageFactory) SpringContextHelper.getBean("drinksPageFactory");
        return drinksPageFactory;
    }

    protected DrinksFacade getDrinksFacade() {
        if (drinksFacade == null) drinksFacade = SpringContextHelper.getBean(DrinksFacade.class);
        return drinksFacade;
    }

    public void selectDrink(Long id) {
        O to = createNewOverviewTO();
        to.setId(id);
        grid.select(to);
    }

    protected void showDetail(T choosenDrink) {
        this.choosenDrink = choosenDrink;
        dataLayout.removeAll();
        if (choosenDrink == null) {
            image.setVisible(false);
            // TODO
            // String currentURL = request.getContextRoot() + "/" +
            // getDrinksPageFactory().getPageName();
            // Page.getCurrent().pushState(currentURL);
        } else {
            byte[] co = choosenDrink.getImage();
            if (co != null) {
                // https://vaadin.com/forum/thread/260778
                String name = choosenDrink.getName() +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                image.setVisible(true);
                try {
                    image.setSrc(DownloadHandler.fromInputStream(
                            e -> new DownloadResponse(new ByteArrayInputStream(co), name, null, -1)));
                } catch (Exception e) {
                    image.setVisible(false);
                    new ErrorDialog("Foto k nápoji \"" + name + "\" se nezdařilo zobrazit: " + e.getMessage());
                }
            } else {
                image.setVisible(false);
            }

            populateDetail(dataLayout);

            // TODO
            // String currentURL;
            // try {
            // currentURL = request.getContextRoot() + "/" +
            // getDrinksPageFactory().getPageName() + "/" + getURLPath()
            // + "/" + choosenDrink.getId() + "-" +
            // URLEncoder.encode(choosenDrink.getName(), "UTF-8");
            // Page.getCurrent().pushState(currentURL);
            // } catch (UnsupportedEncodingException e) {
            // logger.error("UnsupportedEncodingException in URL", e);
            // }
        }

    }

    protected void populateDetail(Div dataLayout) {
        H2 nameLabel = new H2(getItemHeader());
        dataLayout.add(nameLabel);

        RatingStars rs = new RatingStars();
        rs.setValue(choosenDrink.getRating());
        rs.setReadOnly(true);
        dataLayout.add(rs);

        Div propertiesDiv = new Div();
        propertiesDiv.setId("drinks-properties-div");
        propertiesDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        dataLayout.add(propertiesDiv);

        String[] headers = getPropertiesHeaders();
        String[] properties = getProperties();
        if (headers.length != properties.length)
            throw new IllegalStateException("Drink properties array must have same length as headers array");

        for (int i = 0; i < headers.length; i++)
            propertiesDiv.add(new Div(new Strong(headers[i] + ":"), new Breakline(),
                    new Text(StringUtils.isBlank(properties[i]) ? "-" : properties[i])));

        HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
        description.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        description.setSizeFull();
        dataLayout.add(description);
    }

    protected abstract String getItemHeader();

    protected abstract String[] getPropertiesHeaders();

    protected abstract String[] getProperties();

    protected abstract O createNewOverviewTO();

    protected abstract void configureGrid(Grid<O> grid, O filterTO);

    protected abstract void populate();

    protected abstract void populateBtnLayout(ButtonLayout btnLayout);

    protected abstract String getURLPath();

    protected abstract T findById(Long id);

}
