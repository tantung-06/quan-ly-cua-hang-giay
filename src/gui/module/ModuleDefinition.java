package gui.module;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModuleDefinition {
    public final String title;
    public final String description;
    public final String[] columns;
    public final Supplier<List<?>> loader;
    public final Function<Object, Object[]> rowMapper;
    public RecordEditor editor;

    public ModuleDefinition(String title, String description, String[] columns,
            Supplier<List<?>> loader, Function<Object, Object[]> rowMapper) {
        this.title = title;
        this.description = description;
        this.columns = columns.clone();
        this.loader = loader;
        this.rowMapper = rowMapper;
    }
}