package cz.gattserver.grass.articles.model.domain;

import java.util.Comparator;

public class ExecutedInOrderComparator<T extends ExecutedInOrder> implements Comparator<T> {

    @Override
    public int compare(ExecutedInOrder o1, ExecutedInOrder o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (o2 == null) {
                return 1;
            } else {
                return o1.getExecutionOrder().compareTo(o2.getExecutionOrder());
            }
        }
    }

}
