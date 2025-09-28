package HotelApp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTime {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // Format Timestamp -> String
    public static String format(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(DISPLAY_FORMATTER);
    }

    // Format LocalDateTime -> String
    public static String format(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DISPLAY_FORMATTER);
    }

    // Parse String -> LocalDateTime
    public static LocalDateTime parse(String s) {
        if (s == null || s.isBlank()) return null;

        try {
            // Thử parse DB format trước
            return LocalDateTime.parse(s, DB_FORMATTER);
        } catch (DateTimeParseException e) {
            // Fallback: parse theo display format
            try {
                return LocalDateTime.parse(s, DISPLAY_FORMATTER);
            } catch (DateTimeParseException ex) {
                System.err.println("Invalid datetime: " + s);
                return null;
            }
        }
    }

    // Parse String -> Timestamp
    public static Timestamp parseTimestamp(String s) {
        LocalDateTime dt = parse(s);
        return dt == null ? null : Timestamp.valueOf(dt);
    }
}
