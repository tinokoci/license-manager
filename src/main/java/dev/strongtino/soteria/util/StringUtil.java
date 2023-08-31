package dev.strongtino.soteria.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static <T> List<List<T>> sliceList(List<T> list, int size) {
        List<List<T>> parts = new ArrayList<>();

        for (int i = 0; i < list.size(); i += size) {
            parts.add(new ArrayList<>(list.subList(i, Math.min(list.size(), i + size))));
        }
        return parts;
    }

    public static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatInteger(int number) {
        return String.format("%,d", number).replace(".", ",");
    }
}
