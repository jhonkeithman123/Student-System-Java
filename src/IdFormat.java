public class IdFormat {
    public static boolean isValid(String id) {
        return id != null && id.matches("^\\d{4}-\\d{5}-[A-Za-z]{2}-\\d{1}$");
    }
    public static String format(String raw) {
        // keep alphanumerics
        StringBuilder cleaned = new StringBuilder();
        for (char c : raw.toCharArray()) if (Character.isLetterOrDigit(c)) cleaned.append(c);
        String d = cleaned.toString();
        // insert hyphens at 4, 9, 11
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<d.length() && i<12;i++) {
            if (i==4 || i==9 || i==11) sb.append("-");
            sb.append(d.charAt(i));
        }
        return sb.toString();
    }
}
