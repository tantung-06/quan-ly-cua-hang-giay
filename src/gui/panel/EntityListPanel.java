package gui.panel;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import gui.module.ModuleDefinition;

public class EntityListPanel extends DataPage {
    private static final long serialVersionUID = 1L;

    protected EntityListPanel(String title, String description, String[] columns,
            Supplier<List<?>> loader, Function<Object, Object[]> mapper) {
        super(new ModuleDefinition(title, description, columns, loader, mapper));
        refresh();
    }
}