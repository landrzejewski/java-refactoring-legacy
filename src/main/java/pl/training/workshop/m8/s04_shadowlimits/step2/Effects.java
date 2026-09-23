package pl.training.workshop.m8.s04_shadowlimits.step2;

import pl.training.workshop.shared.Money;

/** Krok 2 (bez zmian): port efektów ubocznych nowej ścieżki. */
public interface Effects {
    void sendMail(String to, String text);

    void charge(String card, Money amount);

    void save(String row);
}
