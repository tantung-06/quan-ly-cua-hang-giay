package gui.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.JPanel;

import gui.UiFormats;
import gui.UiTheme;

final class SalesChart extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int TOP = 12;
    private static final int BOTTOM = 30;
    private static final int RIGHT = 8;
    private static final Color BAR_COLOR = new Color(0x9CB9E8);
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter YEAR_LABEL = DateTimeFormatter.ofPattern("dd/MM/yy");

    private SalesPeriod period = SalesPeriod.lastSevenDays(LocalDate.now());
    private double[] revenueByBucket = new double[period.bucketCount];
    private int chartLeft;
    private int chartWidth;

    SalesChart() {
        setOpaque(false);
        setToolTipText("");
    }

    void setSales(SalesPeriod period, double[] buckets) {
        this.period = period;
        revenueByBucket = buckets.clone();
        repaint();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (chartWidth <= 0 || event.getX() < chartLeft || event.getX() >= chartLeft + chartWidth) {
            return null;
        }
        int index = (int) ((long) (event.getX() - chartLeft) * revenueByBucket.length / chartWidth);
        return period.bucketStart(index).format(SalesPeriod.DATE_FORMAT) + " – "
                + period.bucketEnd(index).format(SalesPeriod.DATE_FORMAT) + ": "
                + UiFormats.currency(revenueByBucket[index]);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double max = Arrays.stream(revenueByBucket).max().orElse(0);
            if (max == 0) {
                drawEmptyChart(g);
                return;
            }
            g.setFont(UiTheme.regular(10));
            FontMetrics metrics = g.getFontMetrics();
            chartLeft = Math.max(42, metrics.stringWidth(UiFormats.number(max)) + 10);
            chartWidth = Math.max(1, getWidth() - chartLeft - RIGHT);
            int height = Math.max(1, getHeight() - TOP - BOTTOM);
            drawGrid(g, metrics, height, max);
            drawBars(g, metrics, height, max);
        } finally {
            g.dispose();
        }
    }

    private void drawEmptyChart(Graphics2D g) {
        chartLeft = 0;
        chartWidth = 0;
        String message = "Không có doanh thu trong khoảng thời gian này.";
        g.setFont(UiTheme.regular(12));
        g.setColor(UiTheme.MUTED);
        int x = Math.max(0, (getWidth() - g.getFontMetrics().stringWidth(message)) / 2);
        g.drawString(message, x, getHeight() / 2);
    }

    private void drawGrid(Graphics2D g, FontMetrics metrics, int height, double max) {
        g.setStroke(new BasicStroke(1));
        for (int line = 0; line <= 3; line++) {
            int y = TOP + height - line * height / 3;
            g.setColor(UiTheme.BORDER);
            g.drawLine(chartLeft, y, chartLeft + chartWidth, y);
            String amount = UiFormats.number(max * line / 3);
            g.setColor(UiTheme.MUTED);
            g.drawString(amount, Math.max(0, chartLeft - metrics.stringWidth(amount) - 7), y + 4);
        }
    }

    private void drawBars(Graphics2D g, FontMetrics metrics, int height, double max) {
        double slot = (double) chartWidth / revenueByBucket.length;
        DateTimeFormatter labels = period.start.getYear() == period.end.getYear() ? DAY_LABEL : YEAR_LABEL;
        int lastLabelEnd = -1;
        for (int index = 0; index < revenueByBucket.length; index++) {
            int barHeight = (int) Math.round(height * revenueByBucket[index] / max);
            int barWidth = Math.max(1, Math.min(28, (int) (slot / 2)));
            int x = (int) (chartLeft + index * slot + (slot - barWidth) / 2);
            int y = TOP + height - barHeight;
            g.setColor(index == revenueByBucket.length - 1 ? UiTheme.PRIMARY : BAR_COLOR);
            g.fillRoundRect(x, y, barWidth, barHeight, 7, 7);

            String day = period.bucketStart(index).format(labels);
            int labelX = Math.max(chartLeft, Math.min(getWidth() - metrics.stringWidth(day),
                    x + (barWidth - metrics.stringWidth(day)) / 2));
            if (labelX > lastLabelEnd + 8) {
                g.setColor(UiTheme.MUTED);
                g.drawString(day, labelX, TOP + height + 20);
                lastLabelEnd = labelX + metrics.stringWidth(day);
            }
        }
    }
}
