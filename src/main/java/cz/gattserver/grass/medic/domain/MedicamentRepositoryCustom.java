package cz.gattserver.grass.medic.domain;

import cz.gattserver.grass.medic.interfaces.MedicamentTO;

import java.util.Set;

public interface MedicamentRepositoryCustom {

    Set<MedicamentTO> findByFilter(MedicamentTO filterTO);

    MedicamentTO findAndMapById(Long id);

    boolean isUsed(Long id);
}