package pl.training.workshop.m3.s05_shadowlimits.step3;

import pl.training.workshop.shared.Money;

/** Krok 3 (bez zmian): port efektów ubocznych - używany tylko przy wykonaniu planu. */
public interface Effects {
    void sendMail(String to, String text);

    void charge(String card, Money amount);

    void save(String row);
}
