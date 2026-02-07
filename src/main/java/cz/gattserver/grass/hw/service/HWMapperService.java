package cz.gattserver.grass.hw.service;

import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.model.HWType;

public interface HWMapperService {

	 HWType mapHWItem(HWTypeTO to);
}