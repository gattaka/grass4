package cz.gattserver.grass.hw.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.hw.model.domain.HWServiceNote;

public interface HWServiceNoteRepository extends JpaRepository<HWServiceNote, Long> {

}
