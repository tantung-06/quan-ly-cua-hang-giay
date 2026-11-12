package gui;

import java.text.NumberFormat;
import java.util.Locale;

public final class UiFormats {
    private static final Locale VIETNAMESE = Locale.forLanguageTag("vi-VN");

    private UiFormats() {
    }

    public static String number(double amount) {
        return NumberFormat.getNumberInstance(VIETNAMESE).format(amount);
    }

    public static String currency(double amount) {
        return number(amount) + " ₫";
    }
}