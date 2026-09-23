package pl.training.workshop.m8.s09_codemod.start;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Start: migracja "ręczna z grepem". Lista miejsc powstaje z wyrażenia regularnego, a przepisanie
 * to zamiana tekstu dla czterech kombinacji literałów. Regex nie zna komentarzy, wywołań
 * rozbitych na kilka linii ani typu odbiorcy (HotelService też ma book z dwoma boolean).
 */
public final class BookCallCodemod {
    private static final Pattern OLD_CALL =
            Pattern.compile("\\bbook\\(.*,\\s*(true|false)\\s*,\\s*\\w+\\s*\\)");

    /** Źródła projektu (API) - wersja regexowa ich nie potrzebuje. */
    public BookCallCodemod(List<String> projectSources) {
    }

    public List<Integer> findLines(String source) {
        List<Integer> lines = new ArrayList<>();
        String[] text = source.split("\n", -1);
        for (int i = 0; i < text.length; i++) {
            if (OLD_CALL.matcher(text[i]).find()) {
                lines.add(i + 1);
            }
        }
        return lines;
    }

    public String rewrite(String source) {
        return source
                .replace(", true, true)", ", Channel.WEB, Glasses.OWN)")
                .replace(", true, false)", ", Channel.WEB, Glasses.RENTED)")
                .replace(", false, true)", ", Channel.BOX_OFFICE, Glasses.OWN)")
                .replace(", false, false)", ", Channel.BOX_OFFICE, Glasses.RENTED)")
                .replace("import cinema.BookingService;",
                        "import cinema.BookingService;\nimport cinema.Channel;\nimport cinema.Glasses;");
    }
}
