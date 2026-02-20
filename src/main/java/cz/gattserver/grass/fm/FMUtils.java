package cz.gattserver.grass.fm;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass.fm.interfaces.FMItemTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class FMUtils {

    public static Long getFileSize(Path path) throws IOException {
        if (!Files.isDirectory(path)) return Files.size(path);
        try (Stream<Path> stream = Files.list(path)) {
            Long sum = 0L;
            for (Iterator<Path> it = stream.iterator(); it.hasNext(); )
                sum += getFileSize(it.next());
            return sum;
        }
    }

    public static FMItemTO mapPathToItem(Path path, Path currentAbsolutePath) {
        Long numericSize = null;
        String size;
        try {
            numericSize = getFileSize(path);
            size = path.normalize().startsWith(currentAbsolutePath) ?
                    HumanBytesSizeFormatter.format(numericSize, true) : "";
        } catch (IOException e) {
            size = "n/a";
        }
        LocalDateTime lastModified = null;
        try {
            LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            // zůstane null
        }
        return new FMItemTO(path.getFileName().toString(), Files.isDirectory(path), numericSize, size, lastModified);
    }

    private static int compareByQuery(FMItemTO to1, FMItemTO to2, List<QuerySortOrder> list) {
        int result;
        if (list != null) {
            for (QuerySortOrder o : list) {
                int direction = SortDirection.ASCENDING == o.getDirection() ? 1 : -1;
                switch (o.getSorted()) {
                    default:
                    case "name":
                        result = to1.name().compareTo(to2.name());
                        if (result != 0) return result * direction;
                        break;
                    case "size":
                        if (to1.numericSize() == null) {
                            if (to2.numericSize() == null) {
                                result = 0;
                            } else {
                                result = -1;
                            }
                        } else {
                            result = to1.numericSize().compareTo(to2.numericSize());
                        }
                        if (result != 0) return result * direction;
                        break;
                    case "lastModified":
                        result = to1.lastModified().compareTo(to2.lastModified());
                        if (result != 0) return result * direction;
                        break;
                }
            }
        }
        return to1.name().compareTo(to2.name());
    }

    public static int sortFile(FMItemTO to1, FMItemTO to2, List<QuerySortOrder> list) {
        if (to1.directory()) {
            if (to2.directory()) return compareByQuery(to1, to2, list);
            return -1;
        } else {
            if (to2.directory()) return 1;
            return compareByQuery(to1, to2, list);
        }
    }
}
