package pl.training.module4.structural.decorator;

import java.util.Scanner;

/**
 * Czyta jedną linię ze standardowego wejścia. Gdy wejście jest puste lub
 * zamknięte, zwraca {@link #DEFAULT_TEXT}, dzięki czemu przykład działa też
 * nieinteraktywnie (tryb {@code all}, testy, CI).
 */
public class SystemInReader implements Reader {

    public static final String DEFAULT_TEXT = "Hello Decorator Pattern";

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String getText() {
        return scanner.hasNextLine() ? scanner.nextLine() : DEFAULT_TEXT;
    }

}
