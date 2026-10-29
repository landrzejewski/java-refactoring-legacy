package pl.training.workshop.exercises.e01_import.step2;

/**
 * Krok 2: produkt metody wytwórczej - rozbiór jednego wiersza danego formatu. Parser ma własne życie:
 * można go przetestować bez importera i użyć np. w podglądzie wiersza w edytorze repertuaru.
 */
public interface LineParser {
    boolean isComment(String line);

    RawScreening parse(String line, int number);
}
