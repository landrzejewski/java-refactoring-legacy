package pl.training.workshop.exercises.e07_documents.step2;

import java.util.List;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
public interface ReceiptLayout {
    String receipt(Sale sale);

    String section(List<String> lines);
}
