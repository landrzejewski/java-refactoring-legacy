package pl.training.workshop.m8.s04_shadowlimits.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: port efektów ubocznych - nowa ścieżka nie sięga już bezpośrednio do infrastruktury. */
public interface Effects {
    void sendMail(String to, String text);

    void charge(String card, Money amount);

    void save(String row);
}
