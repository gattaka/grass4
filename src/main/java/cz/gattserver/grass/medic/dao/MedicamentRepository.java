package cz.gattserver.grass.medic.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.medic.domain.Medicament;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

}
