package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Predicate;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import cz.gattserver.grass.medic.interfaces.QMedicalInstitutionTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class MedicalInstitutionRepositoryCustomImpl extends QuerydslRepositorySupport
        implements MedicalInstitutionRepositoryCustom {

    private final QMedicalInstitution i = QMedicalInstitution.medicalInstitution;

    public MedicalInstitutionRepositoryCustomImpl() {
        super(MedicalInstitution.class);
    }

    private Predicate createPredicate(MedicalInstitutionTO filterTO) {
        PredicateBuilder builder = new PredicateBuilder();
        if (filterTO != null) {
            builder.iLike(i.address, filterTO.getAddress());
            builder.iLike(i.name, filterTO.getName());
            builder.iLike(i.web, filterTO.getWeb());
            builder.iLike(i.hours, filterTO.getHours());
        }
        return builder.getBuilder();
    }

    @Override
    public List<MedicalInstitutionTO> findByFilter(MedicalInstitutionTO filterTO) {
        return from(i).select(new QMedicalInstitutionTO(i.id, i.name, i.address, i.hours, i.web))
                .where(createPredicate(filterTO)).orderBy(i.name.desc()).fetch();
    }

    @Override
    public MedicalInstitutionTO findAndMapById(Long id) {
        return from(i).select(new QMedicalInstitutionTO(i.id, i.name, i.address, i.hours, i.web)).where(i.id.eq(id))
                .fetchOne();
    }
}