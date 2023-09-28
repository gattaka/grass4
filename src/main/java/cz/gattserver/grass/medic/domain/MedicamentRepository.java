package cz.gattserver.grass.medic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.medic.domain.Medicament;

public interface MedicamentRepository extends JpaRepository<Medicament, Long>, MedicamentRepositoryCustom {

}