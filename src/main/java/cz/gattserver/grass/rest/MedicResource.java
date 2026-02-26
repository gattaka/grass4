package cz.gattserver.grass.rest;

import java.util.List;

import cz.gattserver.grass.medic.interfaces.ScheduledVisitOverviewTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass.medic.service.MedicService;

@Slf4j
@Controller
@RequestMapping("/ws/medic")
public class MedicResource {

    private final MedicService medicService;

    public MedicResource(MedicService medicService) {
        log.info("Medic resource online");
        this.medicService = medicService;
    }

    @RequestMapping(value = "visit", headers = "Accept=application/json")
    @ResponseBody
    public List<ScheduledVisitOverviewTO> getInstitutions() {
        return medicService.getAllScheduledVisits();
    }

}