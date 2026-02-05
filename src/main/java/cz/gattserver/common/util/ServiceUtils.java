package cz.gattserver.common.util;

import cz.gattserver.common.Identifiable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ServiceUtils {

    /**
     * Prochází nový set položek a porovnává ho s existujícím setem. Chybějící položky v
     * setu smaže přes callback a vrátí pouze ty, které jsou v novém navíc oproti existujícímu.
     *
     * @param newSet        set závislých položek
     * @param existingSet      stávající set závislých položek v DB
     * @param toDeleteSetConsumer funkce na smazání již nepřítomných položek (jsou v dependentDBSet, ale ne
     *                            dependentSet)
     * @param <D>                 typ závislé položky
     * @return nové položky, které jsou v dependentSet, ale ne v dependentDBSet
     */
    public static <D> Set<D> processDependentSetAndDeleteMissing(Set<D> newSet, Set<D> existingSet,
                                                                 Consumer<Set<D>> toDeleteSetConsumer) {
        Set<D> newItems = newSet;
        if (newSet != null) {
            Set<D> toDeleteSet =
                    existingSet.stream().filter(r -> !newSet.contains(r)).collect(Collectors.toSet());
            if (!toDeleteSet.isEmpty()) toDeleteSetConsumer.accept(toDeleteSet);
            newItems = newSet.stream().filter(r -> !existingSet.contains(r)).collect(Collectors.toSet());
        }
        return newItems;
    }
}
