package cz.gattserver.grass.hw.service.impl;

import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.model.HWType;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.hw.service.HWMapperService;

@Component
public class HWMapperServiceImpl implements HWMapperService {

	public HWType mapHWItem(HWTypeTO to) {
		if (to == null)
			return null;

		HWType e = new HWType();
		e.setId(to.getId());
		e.setName(to.getName());
		return e;
	}
}