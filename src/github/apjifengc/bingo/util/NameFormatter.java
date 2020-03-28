package github.apjifengc.bingo.util;

public class NameFormatter {
    static public String toNameFormat(String string) {
        StringBuilder sb = new StringBuilder();
        boolean isUpperCase = true;
        for (int i=0;i<string.length();i++) {
            if (string.charAt(i) == '_') {
                sb.append(" ");
                isUpperCase = true;
            } else if (isUpperCase) {
                sb.append(string.toUpperCase().charAt(i));
                isUpperCase = false;
            } else {
                sb.append(string.toLowerCase().charAt(i));
            }
        }
        return sb.toString();
    }
}
