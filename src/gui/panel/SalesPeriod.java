package gui.panel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import dto.HoaDon;

final class SalesPeriod {
    private static final int MAX_CHART_BARS = 31;
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    final LocalDate start;
    final LocalDate end;
    final long daysPerBucket;
    final int bucketCount;

    SalesPeriod(LocalDate start, LocalDate end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Từ ngày không được lớn hơn Đến ngày.");
        }
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        daysPerBucket = Math.max(1, (days + MAX_CHART_BARS - 1) / MAX_CHART_BARS);
        bucketCount = (int) ((days + daysPerBucket - 1) / daysPerBucket);
    }

    static SalesPeriod lastSevenDays(LocalDate today) {
        return new SalesPeriod(today.minusDays(6), today);
    }

    static SalesPeriod parse(String start, String end) {
        try {
            return new SalesPeriod(LocalDate.parse(start.trim(), DATE_FORMAT),
                    LocalDate.parse(end.trim(), DATE_FORMAT));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Vui lòng nhập ngày hợp lệ theo định dạng dd/MM/yyyy.");
        }
    }

    LocalDate bucketStart(int index) {
        return start.plusDays(index * daysPerBucket);
    }

    LocalDate bucketEnd(int index) {
        LocalDate first = bucketStart(index);
        return first.plusDays(Math.min(daysPerBucket - 1, ChronoUnit.DAYS.between(first, end)));
    }

    Result summarize(List<HoaDon> sales) {
        List<HoaDon> filtered = new ArrayList<>();
        double revenue = 0;
        int completed = 0;
        double[] buckets = new double[bucketCount];
        for (HoaDon sale : sales) {
            LocalDate date = sale.getNgayLap();
            if (date == null || date.isBefore(start) || date.isAfter(end)) {
                continue;
            }
            filtered.add(sale);
            if ("DA_XUAT".equals(sale.getTrangThai())) {
                revenue += sale.getTongTien();
                completed++;
                int index = (int) (ChronoUnit.DAYS.between(start, date) / daysPerBucket);
                buckets[index] += sale.getTongTien();
            }
        }
        filtered.sort(Comparator.comparing(HoaDon::getNgayLap).reversed()
                .thenComparing(HoaDon::getMaHoaDon, Comparator.nullsLast(Comparator.reverseOrder())));
        return new Result(filtered, revenue, completed, buckets);
    }

    static final class Result {
        final List<HoaDon> sales;
        final double revenue;
        final int completed;
        final double[] buckets;

        Result(List<HoaDon> sales, double revenue, int completed, double[] buckets) {
            this.sales = List.copyOf(sales);
            this.revenue = revenue;
            this.completed = completed;
            this.buckets = buckets;
        }
    }
}
