package ubersuper.utils;
import ubersuper.utils.storage.DataStorage;

import java.util.ArrayList;

/**
 * Result of loading list from persistent storage.
 * <p>
 * This is a simple immutable holder returned by {@link DataStorage#load()} that
 * carries:
 * <ul>
 *   <li>{@code list}: the loaded list (possibly empty),</li>
 *   <li>{@code listSize}: number of objects in list successfully loaded,</li>
 *   <li>{@code skipped}: number of lines skipped due to parse/format errors.</li>
 * </ul>
 */
public class LoadedResult<T> {
    private final T list;
    private final int listSize;
    private final int skipped;

    /**
     * Creates a {@code LoadedResult}.
     *
     * @param list    the loaded task list (non-null)
     * @param listSize count of list successfully loaded into {@code list}
     * @param skipped  count of corrupted/unsupported lines skipped during load
     */
    public LoadedResult(T list, int listSize, int skipped) {
        this.list = list;
        this.listSize = listSize;
        this.skipped = skipped;
    }

    public int listSize() {
        return this.listSize;
    }

    public int skipped() {
        return this.skipped;
    }

    public T list() {
        return this.list;
    }

}

