package cz.gattserver.grass.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

@Controller
@RequestMapping("/ws/medic")
public class MedicResource {

	private static Logger logger = LoggerFactory.getLogger(MedicResource.class);

	@Autowired
	private MedicService medicService;

	@RequestMapping(value = "visit", headers = "Accept=application/json")
	@ResponseBody
	public List<ScheduledVisitTO> getInstitutions() {
		return medicService.getAllScheduledVisits();
	}

	public MedicResource() {
		logger.info("Medic resource online");
	}

}
