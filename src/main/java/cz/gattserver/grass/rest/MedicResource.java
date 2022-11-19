package cz.gattserver.grass.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass.medic.facade.MedicFacade;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

@Controller
@RequestMapping("/medic")
public class MedicResource {

	private static Logger logger = LoggerFactory.getLogger(MedicResource.class);

	@Autowired
	private MedicFacade medicFacade;

	@RequestMapping(value = "visit", headers = "Accept=application/json")
	@ResponseBody
	public List<ScheduledVisitTO> getInstitutions() {
		return medicFacade.getAllScheduledVisits();
	}

	public MedicResource() {
		logger.info("Medic resource online");
	}

}
