package pl.training.workshop.exercises.e03_notifications.start;

import java.util.ArrayList;
import java.util.List;

/**
 * Start: bramki wysylkowe kina (e-mail, SMS, push) i dziennik repertuaru w jednym miejscu.
 * To "urzadzenia" sceny - nie refaktoryzujemy ich, test czyta z nich kolejnosc efektow.
 */
public final class Journal {
    private final List<String> lines = new ArrayList<>();

    public void email(String to, String subject, String body) {
        lines.add("EMAIL " + to + " | " + subject + " | " + body);
    }

    public void sms(String number, String text) {
        lines.add("SMS " + number + " | " + text);
    }

    public void push(String token, String title, String body) {
        lines.add("PUSH " + token + " | " + title + " | " + body);
    }

    public void note(String line) {
        lines.add(line);
    }

    public List<String> lines() {
        return List.copyOf(lines);
    }
}
