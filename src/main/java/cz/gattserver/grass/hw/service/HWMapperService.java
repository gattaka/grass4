package cz.gattserver.grass.hw.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.interfaces.HWServiceNoteTO;
import cz.gattserver.grass.hw.model.domain.HWItem;
import cz.gattserver.grass.hw.model.domain.HWItemType;
import cz.gattserver.grass.hw.model.domain.HWServiceNote;

public interface HWMapperService {

	public HWItemTypeTO mapHWItemType(HWItemType e);

	public HWItemType mapHWItem(HWItemTypeTO dto);

	public Set<HWItemTypeTO> mapHWItemTypes(Collection<HWItemType> list);

	public HWServiceNoteTO mapServiceNote(HWServiceNote e);

	public List<HWServiceNoteTO> mapServiceNotes(Collection<HWServiceNote> list);

	public HWItemTO mapHWItem(HWItem e);

	public HWItemOverviewTO mapHWItemOverview(HWItem e);

	public List<HWItemOverviewTO> mapHWItems(Collection<HWItem> list);

}
