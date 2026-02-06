package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Predicate;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

import cz.gattserver.grass.medic.interfaces.QMedicamentTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.LinkedHashSet;
import java.util.Set;

public class MedicamentRepositoryCustomImpl extends QuerydslRepositorySupport implements MedicamentRepositoryCustom {

    private final QMedicament m = QMedicament.medicament;
    private final QMedicalRecordMedicament mrm = QMedicalRecordMedicament.medicalRecordMedicament;

    public MedicamentRepositoryCustomImpl() {
        super(Medicament.class);
    }

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
        return new LinkedHashSet<>(
                from(m).where(createPredicate(filterTO)).select(new QMedicamentTO(m.id, m.name, m.tolerance))
                        .orderBy(m.name.asc()).fetch());
    }

    @Override
    public MedicamentTO findAndMapById(Long id) {
        return from(m).where(m.id.eq(id)).select(new QMedicamentTO(m.id, m.name, m.tolerance)).orderBy(m.name.asc())
                .fetchOne();
    }

    @Override
    public boolean isUsed(Long id) {
        return from(mrm).where(mrm.id.medicamentId.eq(id)).fetchCount() > 0;
    }
}