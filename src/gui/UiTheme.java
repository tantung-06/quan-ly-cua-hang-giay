package gui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class UiTheme {
    public static final java.awt.Color BACKGROUND = new java.awt.Color(0xF5F7FB);
    public static final java.awt.Color BORDER = new java.awt.Color(0xE6EAF0);
    public static final java.awt.Color FIELD_BORDER = new java.awt.Color(0xD0D5DD);
    public static final java.awt.Color TEXT = new java.awt.Color(0x172033);
    public static final java.awt.Color MUTED = new java.awt.Color(0x000000);
    public static final java.awt.Color PRIMARY = new java.awt.Color(0x174EA6);
    public static final java.awt.Color PRIMARY_SOFT = new java.awt.Color(0xEAF2FF);
    public static final java.awt.Color BLUE = new java.awt.Color(0x2563EB);
    public static final java.awt.Color DANGER = new java.awt.Color(0xC33C54);
    public static final java.awt.Color GREEN = new java.awt.Color(0x16845B);
    public static final java.awt.Color AMBER = new java.awt.Color(0xB76E00);

    private static final String FONT_FAMILY = chooseFont();

    private UiTheme() {
    }

    public static Font regular(float size) {
        return new Font(FONT_FAMILY, Font.PLAIN, 1).deriveFont(size);
    }

    public static Font bold(float size) {
        return new Font(FONT_FAMILY, Font.BOLD, 1).deriveFont(size);
    }

    private static String chooseFont() {
        Set<String> installed = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String family : new String[] { "Segoe UI", "Inter", "Roboto", "Arial" }) {
            if (installed.contains(family)) {
                return family;
            }
        }
        return Font.SANS_SERIF;
    }
}