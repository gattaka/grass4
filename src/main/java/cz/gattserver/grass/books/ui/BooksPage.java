package cz.gattserver.grass.books.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.books.facades.BooksService;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.common.ui.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.Strong;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@PageTitle("Knihy")
@Route(value = "books", layout = MainView.class)
public class BooksPage extends Div implements HasUrlParameter<String> {

    private transient SecurityService securityService;
    private transient BooksService booksService;

    private Image image;
    private Div dataDiv;
    private Div imageDiv;

    private Grid<BookOverviewTO> grid;
    private BookOverviewTO filterTO;
    private BookTO choosenBook;

    private CallbackDataProvider<BookOverviewTO, BookOverviewTO> dataProvider;

    public BooksPage(SecurityService securityService, BooksService booksService) {
        this.securityService = securityService;
        this.booksService = booksService;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        filterTO = new BookOverviewTO();
        grid = createGrid(filterTO);
        UIUtils.applyGrassDefaultStyle(grid);
        layout.add(grid);

        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) showDetail(findById(e.getFirstSelectedItem().get().getId()));
            else showDetail(null);
        });

        Div contentLayout = new Div();
        contentLayout.setSizeFull();
        contentLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        contentLayout.setId("books-div");
        layout.add(contentLayout);

        imageDiv = new Div();
        imageDiv.setId("books-image-div");
        contentLayout.add(imageDiv);

        // musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
        image = ImageIcon.BUBBLE_16_ICON.createImage("icon");
        image.setVisible(false);
        imageDiv.add(image);

        dataDiv = new Div();
        dataDiv.setId("books-data-div");
        contentLayout.add(dataDiv);

        Div btnLayout = componentFactory.createButtonLayout();
        btnLayout.setVisible(getSecurityService().getCurrentUser().getRoles().contains(CoreRole.ADMIN));
        layout.add(btnLayout);

        populateBtnLayout(btnLayout);

        if (parameter != null) {
            URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
            selectBook(identifier.getId());
        }
    }

    protected Grid<BookOverviewTO> createGrid(final BookOverviewTO filterTO) {
        final Grid<BookOverviewTO> grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setWidthFull();
        grid.setHeight("400px");

        Column<BookOverviewTO> authorColumn =
                grid.addColumn(BookOverviewTO::getAuthor).setHeader("Autor").setFlexGrow(50).setAutoWidth(true)
                        .setSortProperty("author");
        Column<BookOverviewTO> nameColumn =
                grid.addColumn(BookOverviewTO::getName).setHeader("Název").setFlexGrow(50).setAutoWidth(true)
                        .setSortProperty("name");
        grid.addColumn(new ComponentRenderer<>(to -> {
            RatingStars rs = new RatingStars();
            rs.setValue(to.getRating());
            rs.setReadOnly(true);
            rs.setSize("15px");
            return rs;
        })).setHeader("Hodnocení").setAutoWidth(true).setSortProperty("rating");

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Autor
        UIUtils.addHeaderTextField(filteringHeader.getCell(authorColumn), e -> {
            filterTO.setAuthor(e.getValue());
            dataProvider.refreshAll();
        });

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
            filterTO.setName(e.getValue());
            dataProvider.refreshAll();
        });

        FetchCallback<BookOverviewTO, BookOverviewTO> fetchCallback =
                q -> getBooksFacade().getBooks(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        CountCallback<BookOverviewTO, BookOverviewTO> countCallback = q -> getBooksFacade().countBooks(filterTO);
        dataProvider = DataProvider.fromFilteringCallbacks(fetchCallback, countCallback);
        grid.setDataProvider(dataProvider);

        return grid;
    }

    protected void populateBtnLayout(Div btnLayout) {
        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton(event -> new BookDialog(to -> {
            to = getBooksFacade().saveBook(to);
            showDetail(to);
            dataProvider.refreshAll();
        }).open()));

        btnLayout.add(componentFactory.createEditGridButton(event -> new BookDialog(choosenBook, to -> {
            to = getBooksFacade().saveBook(to);
            showDetail(to);
            dataProvider.refreshItem(to);
        }).open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (BookOverviewTO s : items)
                getBooksFacade().deleteBook(s.getId());
            dataProvider.refreshAll();
            showDetail(null);
        }, grid));
    }

    protected void populateDetail(Div dataLayout) {
        H2 nameLabel = new H2(choosenBook.getName());
        dataLayout.add(nameLabel);

        dataLayout.add(imageDiv);

        RatingStars rs = new RatingStars();
        rs.setValue(choosenBook.getRating());
        rs.setReadOnly(true);
        dataLayout.add(rs);

        Div infoLayout = new Div();
        infoLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        dataLayout.add(infoLayout);

        infoLayout.add(new Strong("Autor"));
        infoLayout.add(new Breakline());
        infoLayout.add(choosenBook.getAuthor());
        infoLayout.add(new Breakline());
        infoLayout.add(new Breakline());
        infoLayout.add(new Strong("Vydáno"));
        infoLayout.add(new Breakline());
        infoLayout.add(choosenBook.getYear());
        infoLayout.add(new Breakline());
        infoLayout.add(new Breakline());

        HtmlDiv description = new HtmlDiv(choosenBook.getDescription().replaceAll("\n", "<br/>"));
        dataLayout.add(description);
    }

    protected SecurityService getSecurityService() {
        if (securityService == null) securityService = SpringContextHelper.getBean(SecurityService.class);
        return securityService;
    }

    protected BooksService getBooksFacade() {
        if (booksService == null) booksService = SpringContextHelper.getBean(BooksService.class);
        return booksService;
    }

    public void selectBook(Long id) {
        BookOverviewTO to = new BookOverviewTO();
        to.setId(id);
        grid.select(to);
    }

    protected void showDetail(BookTO choosenBook) {
        this.choosenBook = choosenBook;
        dataDiv.removeAll();
        if (choosenBook == null) {
            // TODO
            // String currentURL = request.getContextRoot() + "/" +
            // getBooksPageFactory().getPageName();
            // UI.getCurrent().getRouter().
            // Page.getCurrent().pushState(currentURL);
        } else {
            byte[] co = choosenBook.getImage();
            if (co != null) {
                // https://vaadin.com/forum/thread/260778
                String name = choosenBook.getName() +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                image.setVisible(true);
                image.setSrc(DownloadHandler.fromInputStream(
                        e -> new DownloadResponse(new ByteArrayInputStream(co), name, null, -1)));
            } else {
                image.setVisible(false);
            }

            populateDetail(dataDiv);

            // TODO
            // String currentURL;
            // try {
            // currentURL = request.getContextRoot() + "/" +
            // getBooksPageFactory().getPageName() + "/"
            // + +choosenBook.getId() + "-" +
            // URLEncoder.encode(choosenBook.getName(), "UTF-8");
            // Page.getCurrent().pushState(currentURL);
            // } catch (UnsupportedEncodingException e) {
            // logger.error("UnsupportedEncodingException in URL", e);
            // }
        }
    }

    protected BookTO findById(Long id) {
        return getBooksFacade().getBookById(id);
    }
}
