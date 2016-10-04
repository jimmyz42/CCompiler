package edu.mit.compilers;

import java.util.List;

public class Util {
    public static String joinStrings(List<?> items) {
        if (items.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(items.get(0).toString());
        for (int i = 1; i < items.size(); i++) {
            sb.append(", ");
            sb.append(items.get(i).toString());
        }
        return sb.toString();
    }
}
