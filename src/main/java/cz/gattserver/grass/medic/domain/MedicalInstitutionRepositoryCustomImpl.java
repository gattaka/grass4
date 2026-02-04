package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import cz.gattserver.grass.medic.interfaces.QMedicalInstitutionTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class MedicalInstitutionRepositoryCustomImpl implements MedicalInstitutionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private Predicate createPredicate(MedicalInstitutionTO filterTO) {
        QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
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
        JPAQuery<MedicalInstitution> query = new JPAQuery<>(entityManager);
        QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
        return query.select(new QMedicalInstitutionTO(i.id, i.name, i.address, i.hours, i.web)).from(i)
                .where(createPredicate(filterTO)).orderBy(i.name.desc()).fetch();
    }

    @Override
    public MedicalInstitutionTO findAndMapById(Long id) {
        JPAQuery<MedicalInstitution> query = new JPAQuery<>(entityManager);
        QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
        return query.select(new QMedicalInstitutionTO(i.id, i.name, i.address, i.hours, i.web)).from(i)
                .where(i.id.eq(id)).fetchOne();
    }
}