package cz.gattserver.grass.hw.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.interfaces.HWServiceNoteTO;
import cz.gattserver.grass.hw.model.HWItem;
import cz.gattserver.grass.hw.model.HWType;
import cz.gattserver.grass.hw.model.HWServiceNote;

public interface HWMapperService {

	public HWTypeTO mapHWItemType(HWType e);

	public HWType mapHWItem(HWTypeTO dto);

	public Set<HWTypeTO> mapHWItemTypes(Collection<HWType> list);

	public HWServiceNoteTO mapServiceNote(HWServiceNote e);

	public List<HWServiceNoteTO> mapServiceNotes(Collection<HWServiceNote> list);

	public HWItemTO mapHWItem(HWItem e);

	public HWItemOverviewTO mapHWItemOverview(HWItem e);

	public List<HWItemOverviewTO> mapHWItems(Collection<HWItem> list);

}
