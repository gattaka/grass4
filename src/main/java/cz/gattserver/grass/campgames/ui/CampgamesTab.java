package cz.gattserver.grass.campgames.ui;

import java.util.Arrays;
import java.util.Set;

import cz.gattserver.grass.campgames.CampgamesRole;
import cz.gattserver.grass.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.campgames.ui.dialogs.CampgameCreateDialog;
import cz.gattserver.grass.campgames.ui.dialogs.CampgameDetailDialog;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;

import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.dialogs.ErrorDialog;

public class CampgamesTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	private static final String NAME_BIND = "nameBind";
	private static final String PLAYERS_BIND = "playersBind";
	private static final String PREPARATIONTIME_BIND = "preparationTimeBind";
	private static final String PLAYTIME_BIND = "playTimeBind";

	@Autowired
	private CampgamesService campgamesService;

	private Grid<CampgameOverviewTO> grid;
	private TokenField keywordsFilter;

	private CampgameFilterTO filterDTO;

	public CampgamesTab() {
		SpringContextHelper.inject(this);

		filterDTO = new CampgameFilterTO();

		// Filtr na klíčová slova
		keywordsFilter = new TokenField(campgamesService.getAllCampgameKeywordNames());
		keywordsFilter.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		keywordsFilter.setPlaceholder("Filtrovat dle klíčových slov");
		keywordsFilter.getInputField().setWidth("200px");
		keywordsFilter.addTokenAddListener(token -> populate());
		keywordsFilter.addTokenRemoveListener(e -> populate());
		keywordsFilter.setAllowNewItems(false);
		add(keywordsFilter);

		// Tabulka her
		grid = new Grid<>();
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidthFull();

		Column<CampgameOverviewTO> nameColumn = grid.addColumn(CampgameOverviewTO::getName).setKey(NAME_BIND)
				.setHeader("Název").setWidth("180px").setFlexGrow(0);
		Column<CampgameOverviewTO> playersColumn = grid.addColumn(CampgameOverviewTO::getPlayers).setKey(PLAYERS_BIND)
				.setHeader("Hráčů").setWidth("280px").setFlexGrow(0);
		Column<CampgameOverviewTO> playTimeColumn = grid.addColumn(CampgameOverviewTO::getPlayTime)
				.setKey(PLAYTIME_BIND).setHeader("Délka hry").setWidth("280px").setFlexGrow(0);
		Column<CampgameOverviewTO> prepTimeColumn = grid.addColumn(CampgameOverviewTO::getPreparationTime)
				.setKey(PREPARATIONTIME_BIND).setHeader("Délka přípravy");
		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
			filterDTO.setName(e.getValue());
			populate();
		});

		// Hráčů
		UIUtils.addHeaderTextField(filteringHeader.getCell(playersColumn), e -> {
			filterDTO.setPlayers(e.getValue());
			populate();
		});

		// Délka hry
		UIUtils.addHeaderTextField(filteringHeader.getCell(playTimeColumn), e -> {
			filterDTO.setPlayTime(e.getValue());
			populate();
		});

		// Délka přípravy
		UIUtils.addHeaderTextField(filteringHeader.getCell(prepTimeColumn), e -> {
			filterDTO.setPreparationTime(e.getValue());
			populate();
		});

		populate();
		grid.sort(Arrays.asList(new GridSortOrder<CampgameOverviewTO>(nameColumn, SortDirection.ASCENDING)));
		grid.addItemClickListener(event -> {
			if (event.getClickCount() > 2)
				openDetailWindow(event.getItem().getId());
		});
		add(grid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		boolean editor = SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CampgamesRole.CAMPGAME_EDITOR);

		// Založení nové hry
		CreateButton newCampgameBtn = new CreateButton("Založit novou hru", e -> openItemWindow(null));
		buttonLayout.add(newCampgameBtn);
		newCampgameBtn.setVisible(editor);

		// Zobrazení detailů hry
		GridButton<CampgameOverviewTO> detailsBtn = new GridButton<>("Detail",
				e -> openDetailWindow(grid.getSelectedItems().iterator().next().getId()), grid);
		detailsBtn.setIcon(new Image(ImageIcon.CLIPBOARD_16_ICON.createResource(), "Detail"));
		buttonLayout.add(detailsBtn);

		// Oprava údajů existující hry
		ModifyGridButton<CampgameOverviewTO> fixBtn = new ModifyGridButton<>("Upravit", e -> openItemWindow(e), grid);
		buttonLayout.add(fixBtn);
		fixBtn.setVisible(editor);

		// Smazání hry
		DeleteGridButton<CampgameOverviewTO> deleteBtn = new DeleteGridButton<>("Smazat", e -> openDeleteWindow(),
				grid);
		deleteBtn.setEnabled(false);
		buttonLayout.add(deleteBtn);
		deleteBtn.setVisible(editor);
	}

	private void populate() {
		Set<String> types = keywordsFilter.getValues();
		filterDTO.setKeywords(types);

		FetchCallback<CampgameOverviewTO, Void> fetchCallback = q -> campgamesService.getCampgames(filterDTO,
				q.getOffset(), q.getLimit(), QuerydslUtil.transformOrdering(q.getSortOrders(), column -> {
					switch (column) {
					case NAME_BIND:
						return "name";
					case PLAYERS_BIND:
						return "players";
					case PLAYTIME_BIND:
						return "playTime";
					case PREPARATIONTIME_BIND:
						return "preparationTime";
					default:
						return column;
					}
				})).stream();
		CountCallback<CampgameOverviewTO, Void> countCallback = q -> campgamesService.countCampgames(filterDTO);
		grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
	}

	private void openItemWindow(CampgameOverviewTO to) {
		CampgameTO campgame = null;
		if (to != null) {
			if (grid.getSelectedItems().isEmpty())
				return;
			campgame = campgamesService.getCampgame(to.getId());
		}
		new CampgameCreateDialog(campgame == null ? null : campgame.getId()) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(CampgameTO dto) {
				CampgameOverviewTO filterTO = new CampgameOverviewTO();
				filterTO.setId(dto.getId());
				// select musí neintuitivně být dřív než refresh, jinak se do
				// tabulky zobrazí prázdný řádek
				grid.select(filterTO);
				grid.getDataProvider().refreshAll();
			}
		}.open();
	}

	private void openDetailWindow(Long id) {
		new CampgameDetailDialog(id).setChangeListener(this::populate).open();
	}

	private void openDeleteWindow() {
		if (grid.getSelectedItems().isEmpty())
			return;
		CampgameOverviewTO to = grid.getSelectedItems().iterator().next();
		try {
			campgamesService.deleteCampgame(to.getId());
			grid.getDataProvider().refreshAll();
		} catch (Exception ex) {
			new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
		}
	}

}
