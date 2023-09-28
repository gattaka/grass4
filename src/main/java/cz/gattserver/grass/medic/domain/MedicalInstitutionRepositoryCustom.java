package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.OrderSpecifier;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import java.util.List;

public interface MedicalInstitutionRepositoryCustom {

	List<MedicalInstitution> findList(MedicalInstitutionTO filterTO);
}
