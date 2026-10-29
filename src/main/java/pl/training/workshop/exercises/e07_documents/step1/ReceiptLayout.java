package pl.training.workshop.exercises.e07_documents.step1;

import java.util.List;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
public interface ReceiptLayout {
    String receipt(Sale sale);

    String section(List<String> lines);
}
