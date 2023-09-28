package cz.gattserver.grass.medic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.medic.domain.Physician;

public interface PhysicianRepository extends JpaRepository<Physician, Long>, PhysicianRepositoryCustom {

}