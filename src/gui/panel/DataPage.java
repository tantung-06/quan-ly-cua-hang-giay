package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import gui.UiFormats;
import gui.UiTheme;
import gui.module.ModuleDefinition;

public class DataPage extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ModuleDefinition definition;
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField search = new JTextField();
    private final JComboBox<String> statusFilter = new JComboBox<>();
    private final int statusColumn;
    private final JLabel resultCount = new JLabel(" ");
    private final JLabel status = new JLabel(" ");
    private final List<Object> loadedRows = new ArrayList<>();
    private SwingWorker<List<?>, Void> worker;

    public DataPage(ModuleDefinition definition) {
        super(new BorderLayout(0, 18));
        this.definition = definition;
        statusColumn = columnIndex(definition.columns, "Trạng thái");
        setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(definition.title);
        title.setFont(UiTheme.bold(24));
        title.setForeground(UiTheme.TEXT);
        JLabel subtitle = new JLabel(definition.description);
        subtitle.setFont(UiTheme.regular(13));
        subtitle.setForeground(UiTheme.MUTED);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        heading.add(title);
        heading.add(subtitle);

        model = new DefaultTableModel(definition.columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        styleTable(table);

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        search.setFont(UiTheme.regular(13));
        search.setForeground(UiTheme.TEXT);
        search.setBackground(Color.WHITE);
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.FIELD_BORDER),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        search.setPreferredSize(new Dimension(310, 40));
        search.setToolTipText("Tìm kiếm trong danh sách");
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                applyFilters();
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filter();
            }
        });
        JPanel searchTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchTools.setOpaque(false);
        searchTools.add(search);
        if (statusColumn >= 0) {
            statusFilter.setFont(UiTheme.regular(13));
            statusFilter.setForeground(UiTheme.TEXT);
            statusFilter.setBackground(Color.WHITE);
            statusFilter.setPreferredSize(new Dimension(180, 40));
            statusFilter.setToolTipText("Lọc theo trạng thái");
            statusFilter.addActionListener(event -> applyFilters());
            searchTools.add(statusFilter);
        }
        JButton refresh = new JButton("Làm mới");
        styleSecondaryButton(refresh);
        refresh.addActionListener(event -> refresh());
        JButton add = new JButton(definition.editor == null ? "Thêm" : definition.editor.createLabel());
        JButton edit = new JButton("Chỉnh sửa");
        JButton delete = new JButton("Xóa");
        JButton cancelTicket = new JButton(definition.editor == null ? "Hủy"
                : definition.editor.cancelLabel());
        JButton completeTicket = new JButton(definition.editor == null ? "Hoàn tất"
                : definition.editor.completeLabel());
        JButton viewTicket = new JButton(definition.editor == null ? "Xem"
                : definition.editor.viewLabel());
        styleSecondaryButton(add);
        styleSecondaryButton(edit);
        styleSecondaryButton(delete);
        styleSecondaryButton(cancelTicket);
        styleSecondaryButton(completeTicket);
        styleSecondaryButton(viewTicket);
        completeTicket.setForeground(UiTheme.GREEN);
        add.setEnabled(definition.editor != null && definition.editor.canCreate());
        edit.setEnabled(definition.editor != null && definition.editor.canEdit());
        delete.setEnabled(definition.editor != null && definition.editor.canDelete());
        cancelTicket.setEnabled(false);
        completeTicket.setEnabled(false);
        viewTicket.setEnabled(false);
        add.addActionListener(event -> editRecord(null));
        edit.addActionListener(event -> editSelected());
        delete.addActionListener(event -> deleteSelected());
        cancelTicket.addActionListener(event -> cancelSelected());
        completeTicket.addActionListener(event -> completeSelected());
        viewTicket.addActionListener(event -> viewSelected());
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                cancelTicket.setEnabled(hasCancellableSelection());
                completeTicket.setEnabled(hasCompletableSelection());
                viewTicket.setEnabled(hasViewableSelection());
            }
        });
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tools.setOpaque(false);
        if (definition.editor != null && definition.editor.canCreate()) {
            tools.add(add);
        }
        if (definition.editor != null) {
            if (definition.editor.canEdit()) {
                tools.add(edit);
            }
        }
        if (definition.editor != null && definition.editor.canDelete()) {
            tools.add(delete);
        }
        if (definition.editor != null && definition.editor.cancelLabel() != null) {
            tools.add(cancelTicket);
        }
        if (definition.editor != null && definition.editor.completeLabel() != null) {
            tools.add(completeTicket);
        }
        if (definition.editor != null && definition.editor.viewLabel() != null) {
            tools.add(viewTicket);
        }
        tools.add(refresh);
        toolbar.add(searchTools, BorderLayout.WEST);
        toolbar.add(tools, BorderLayout.EAST);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        resultCount.setFont(UiTheme.regular(12));
        resultCount.setForeground(UiTheme.MUTED);
        status.setFont(UiTheme.regular(12));
        status.setForeground(UiTheme.MUTED);
        bottom.add(resultCount, BorderLayout.WEST);
        bottom.add(status, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);
        add(heading, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    public final void refresh() {
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
        status.setForeground(UiTheme.MUTED);
        status.setText("Đang tải dữ liệu...");
        worker = new SwingWorker<List<?>, Void>() {
            @Override
            protected List<?> doInBackground() {
                return definition.loader.get();
            }

            @Override
            protected void done() {
                try {
                    List<?> items = get();
                    model.setRowCount(0);
                    loadedRows.clear();
                    loadedRows.addAll(items);
                    for (Object item : items) {
                        model.addRow(definition.rowMapper.apply(item));
                    }
                    updateStatusOptions();
                    status.setText(items.isEmpty() ? "Chưa có dữ liệu" : "Đã cập nhật");
                    status.setForeground(items.isEmpty() ? UiTheme.AMBER : UiTheme.GREEN);
                    updateCount();
                } catch (java.util.concurrent.CancellationException ignored) {
                    // A newer refresh has replaced this request.
                } catch (Exception ex) {
                    status.setForeground(UiTheme.DANGER);
                    status.setText("Không thể tải dữ liệu");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DataPage.this,
                            "Không thể tải dữ liệu cho mục này.\n" + ex.getMessage(),
                            "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateCount() {
        resultCount.setText(String.format("Hiển thị %d / %d bản ghi", table.getRowCount(), model.getRowCount()));
    }

    private void applyFilters() {
        String text = search.getText().trim();
        String selectedStatus = statusColumn >= 0 && statusFilter.getSelectedIndex() > 0
                ? (String) statusFilter.getSelectedItem()
                : null;
        if (text.isEmpty() && selectedStatus == null) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    if (selectedStatus != null
                            && !selectedStatus.equalsIgnoreCase(entry.getStringValue(statusColumn))) {
                        return false;
                    }
                    if (!text.isEmpty()) {
                        String query = text.toLowerCase(Locale.ROOT);
                        for (int column = 0; column < entry.getValueCount(); column++) {
                            if (entry.getStringValue(column).toLowerCase(Locale.ROOT).contains(query)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                }
            });
        }
        updateCount();
    }

    private void updateStatusOptions() {
        if (statusColumn < 0) {
            return;
        }
        String previous = (String) statusFilter.getSelectedItem();
        java.util.Set<String> values = new java.util.LinkedHashSet<>();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object value = model.getValueAt(row, statusColumn);
            if (value != null) {
                values.add(value.toString());
            }
        }
        statusFilter.removeAllItems();
        statusFilter.addItem("Tất cả trạng thái");
        for (String value : values) {
            statusFilter.addItem(value);
        }
        if (previous != null && values.contains(previous)) {
            statusFilter.setSelectedItem(previous);
        }
        applyFilters();
    }

    private static int columnIndex(String[] columns, String name) {
        for (int index = 0; index < columns.length; index++) {
            if (name.equals(columns[index])) {
                return index;
            }
        }
        return -1;
    }

    private void editSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showInvalid(this, "Vui lòng chọn một bản ghi để chỉnh sửa.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        if (modelRow >= 0 && modelRow < loadedRows.size()) {
            editRecord(loadedRows.get(modelRow));
        }
    }

    private boolean hasCancellableSelection() {
        if (definition.editor == null) {
            return false;
        }
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return false;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        return modelRow >= 0 && modelRow < loadedRows.size()
                && definition.editor.canCancel(loadedRows.get(modelRow));
    }

    private boolean hasViewableSelection() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return false;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        return modelRow >= 0 && modelRow < loadedRows.size()
                && definition.editor != null && definition.editor.viewLabel() != null;
    }

    private void viewSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showInvalid(this, "Vui lòng chọn phiếu cần xem.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        if (modelRow < 0 || modelRow >= loadedRows.size()) {
            return;
        }
        Object record = loadedRows.get(modelRow);
        status.setForeground(UiTheme.MUTED);
        status.setText("Đang tải chi tiết phiếu...");
        new SwingWorker<Object[][], Void>() {
            @Override
            protected Object[][] doInBackground() {
                return definition.editor.loadViewRows(record);
            }

            @Override
            protected void done() {
                try {
                    Object[][] rows = get();
                    status.setText("Đã tải chi tiết phiếu");
                    definition.editor.showView(DataPage.this, record, rows);
                } catch (Exception ex) {
                    status.setForeground(UiTheme.DANGER);
                    status.setText("Không thể tải chi tiết phiếu");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DataPage.this,
                            "Không thể tải chi tiết phiếu.\n" + ex.getMessage(),
                            "Lỗi tải chi tiết phiếu", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void cancelSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showInvalid(this, "Vui lòng chọn phiếu cần hủy.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        if (modelRow < 0 || modelRow >= loadedRows.size()) {
            return;
        }
        Object record = loadedRows.get(modelRow);
        if (!definition.editor.canCancel(record)) {
            showInvalid(this, "Phiếu đã chọn không thể hủy.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                definition.editor.cancelConfirmation(record),
                "Xác nhận hủy phiếu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        runMutation("Đang hủy phiếu...", "Không thể hủy phiếu",
                "Phiếu đã thay đổi trạng thái hoặc không thể hủy.",
                () -> definition.editor.cancel(record));
    }

    private boolean hasCompletableSelection() {
        if (definition.editor == null) {
            return false;
        }
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return false;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        return modelRow >= 0 && modelRow < loadedRows.size()
                && definition.editor.canComplete(loadedRows.get(modelRow));
    }

    private void completeSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showInvalid(this, "Vui lòng chọn phiếu đang chờ xử lý để hoàn tất.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        if (modelRow < 0 || modelRow >= loadedRows.size()) {
            return;
        }
        Object record = loadedRows.get(modelRow);
        if (!definition.editor.canComplete(record)) {
            showInvalid(this, "Chỉ có thể hoàn tất phiếu đang chờ xử lý.");
            return;
        }
        String action = definition.editor.completeLabel();
        String message = definition.editor.completeConfirmation();
        int answer = JOptionPane.showConfirmDialog(this, message,
                "Xác nhận hoàn tất phiếu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        String reason = "Kiểm tra chi tiết phiếu, trạng thái hiện tại";
        if ("Xác nhận xuất".equals(action) || "Thanh toán & xuất".equals(action)) {
            reason += ", tồn kho và điểm khách hàng";
        }
        runMutation("Đang cập nhật trạng thái và tồn kho...", "Không thể hoàn tất phiếu",
                "Không thể hoàn tất phiếu. " + reason + " rồi thử lại.",
                () -> definition.editor.complete(record));
    }

    private void editRecord(Object original) {
        Object changed = definition.editor.show(this, original);
        if (changed == null) {
            return;
        }
        runMutation("Đang lưu...", "Không thể lưu dữ liệu",
                "Dữ liệu không hợp lệ hoặc thao tác bị từ chối. Kiểm tra các trường và dữ liệu liên quan.",
                () -> definition.editor.save(changed, original == null));
    }

    private void deleteSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showInvalid(this, "Vui lòng chọn một bản ghi để xóa.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        if (modelRow < 0 || modelRow >= loadedRows.size()) {
            return;
        }
        Object record = loadedRows.get(modelRow);
        int answer = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa bản ghi đã chọn? Thao tác này không thể hoàn tác.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        runMutation("Đang xóa...", "Không thể xóa dữ liệu",
                "Không thể xóa bản ghi. Bản ghi có thể đang được sử dụng bởi dữ liệu khác.",
                () -> definition.editor.delete(record));
    }

    private void runMutation(String progress, String failureTitle, String failureMessage,
            Supplier<Boolean> operation) {
        status.setForeground(UiTheme.MUTED);
        status.setText(progress);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return operation.get();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        refresh();
                    } else {
                        status.setForeground(UiTheme.DANGER);
                        status.setText(failureTitle);
                        JOptionPane.showMessageDialog(DataPage.this, failureMessage,
                                failureTitle, JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    status.setForeground(UiTheme.DANGER);
                    status.setText(failureTitle);
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    boolean validationError = cause instanceof IllegalArgumentException;
                    if (!validationError) {
                        ex.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(DataPage.this,
                            validationError ? cause.getMessage() : failureMessage,
                            failureTitle, validationError ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private static void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(48);
        table.setFont(UiTheme.regular(13));
        table.setForeground(UiTheme.TEXT);
        table.setBackground(Color.WHITE);
        table.setGridColor(UiTheme.BORDER);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(UiTheme.PRIMARY_SOFT);
        table.setSelectionForeground(UiTheme.TEXT);
        table.setAutoCreateRowSorter(false);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 44));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable source, Object value, boolean selected,
                    boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focused, row, column);
                label.setFont(UiTheme.bold(11));
                label.setForeground(UiTheme.MUTED);
                label.setBackground(new Color(0xF9FAFB));
                label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
                return label;
            }
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable source, Object value, boolean selected,
                    boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focused, row, column);
                label.setFont(UiTheme.regular(13));
                label.setForeground(UiTheme.TEXT);
                label.setBackground(selected ? UiTheme.PRIMARY_SOFT : Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
                if (value instanceof Number && (source.getColumnName(column).contains("tiền")
                        || source.getColumnName(column).contains("Giá")
                        || source.getColumnName(column).contains("giảm"))) {
                    label.setText(UiFormats.currency(((Number) value).doubleValue()));
                }
                return label;
            }
        });
    }

    private static void styleSecondaryButton(JButton button) {
        button.setFont(UiTheme.bold(12));
        button.setForeground(UiTheme.TEXT);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.FIELD_BORDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
    }

    private static void showInvalid(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
    }
}