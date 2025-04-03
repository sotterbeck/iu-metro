package de.sotterbeck.iumetro.domain.common;

public class Color {

    private final int colorValue;

    private Color(int color) {
        this.colorValue = color;
    }

    public static Color ofValue(int color) {
        return new Color(color);
    }

    public static Color ofHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        int colorValue = Integer.parseInt(hex, 16);
        return new Color(colorValue);
    }

    public static boolean isValidHexColor(String colorString) {
        String regex = "^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";

        return colorString != null && colorString.matches(regex);
    }

    public int value() {
        return colorValue;
    }

    public String hex() {
        String hex = String.format("#%06X", (colorValue & 0xFFFFFF));
        return hex.toLowerCase();
    }

}
