package cz.gattserver.grass.hw.ui;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemState;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HWUIUtils {

	private static final String NAME_QUERY_TOKEN = "n";
	private static final String SUPERVIZED_FOR_QUERY_TOKEN = "sf";
	private static final String STATE_QUERY_TOKEN = "s";
	private static final String USED_IN_QUERY_TOKEN = "ui";
	private static final String TYPE_QUERY_TOKEN = "t";

	public static ImageIcon chooseImageIcon(HWItemOverviewTO to) {
		if (to.getState() == null)
			return null;
		switch (to.getState()) {
			case FIXED:
				return ImageIcon.INFO_16_ICON;
			case FAULTY:
				return ImageIcon.WARNING_16_ICON;
			case BROKEN:
				return ImageIcon.DELETE_16_ICON;
			case DISASSEMBLED:
				return ImageIcon.TRASH_16_ICON;
			case NOT_USED:
				return ImageIcon.CLOCK_16_ICON;
			case NEW:
			default:
				return null;
		}
	}

	public static Map<String, String> processFilterToQuery(HWFilterTO filterTO) {
		Map<String, String> filterQuery = new HashMap<>();
		if (StringUtils.isNotBlank(filterTO.getName()))
			filterQuery.put(NAME_QUERY_TOKEN, filterTO.getName());
		if (StringUtils.isNotBlank(filterTO.getSupervizedFor()))
			filterQuery.put(SUPERVIZED_FOR_QUERY_TOKEN, filterTO.getSupervizedFor());
		if (filterTO.getState() != null)
			filterQuery.put(STATE_QUERY_TOKEN, filterTO.getState().name());
		if (StringUtils.isNotBlank(filterTO.getUsedIn()))
			filterQuery.put(USED_IN_QUERY_TOKEN, filterTO.getUsedIn());

		if (filterTO.getTypes() != null) {
			int i = 0;
			for (String type : filterTO.getTypes()) {
				i++;
				filterQuery.put(TYPE_QUERY_TOKEN + i, type);
			}
		}
		return filterQuery;
	}

	public static HWFilterTO processToQueryFilter(Map<String, List<String>> parametersMap) {
		HWFilterTO filterTO = new HWFilterTO();

		List<String> types = new ArrayList<>();
		for (String key : parametersMap.keySet()) {
			List<String> values = parametersMap.get(key);
			if (NAME_QUERY_TOKEN.equals(key)) {
				filterTO.setName(values.get(0));
			} else if (SUPERVIZED_FOR_QUERY_TOKEN.equals(key)) {
				filterTO.setSupervizedFor(values.get(0));
			} else if (STATE_QUERY_TOKEN.equals(key)) {
				try {
					filterTO.setState(HWItemState.valueOf(values.get(0)));
				} catch (IllegalArgumentException e) {
					// chybná neexistující konstanta
				}
			} else if (USED_IN_QUERY_TOKEN.equals(key)) {
				filterTO.setUsedIn(values.get(0));
			} else if (key.startsWith(TYPE_QUERY_TOKEN)) {
				types.add(values.get(0));
			}
		}
		if (!types.isEmpty())
			filterTO.setTypes(types);

		return filterTO;
	}
}
