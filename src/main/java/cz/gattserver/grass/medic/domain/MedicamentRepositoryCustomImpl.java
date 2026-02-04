package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

import cz.gattserver.grass.medic.interfaces.QMedicamentTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.LinkedHashSet;
import java.util.Set;

public class MedicamentRepositoryCustomImpl implements MedicamentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final QMedicament m = QMedicament.medicament;

    private Predicate createPredicate(MedicamentTO filterTO) {
        PredicateBuilder builder = new PredicateBuilder();
        if (filterTO != null) {
            builder.iLike(m.name, filterTO.getName());
            builder.iLike(m.tolerance, filterTO.getTolerance());
        }
        return builder.getBuilder();
    }

    @Override
    public Set<MedicamentTO> findByFilter(MedicamentTO filterTO) {
        JPAQuery<MedicamentTO> query = new JPAQuery<>(entityManager);
        return new LinkedHashSet<>(
                query.from(m).where(createPredicate(filterTO)).select(new QMedicamentTO(m.id, m.name, m.tolerance))
                        .orderBy(m.name.asc()).fetch());
    }

    @Override
    public MedicamentTO findAndMapById(Long id) {
        JPAQuery<MedicamentTO> query = new JPAQuery<>(entityManager);
        return query.from(m).where(m.id.eq(id)).select(new QMedicamentTO(m.id, m.name, m.tolerance))
                .orderBy(m.name.asc()).fetchOne();
    }
}