package pl.training.workshop.exercises.e07_documents.start;

import java.util.List;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
public interface ReceiptLayout {
    String receipt(Sale sale);

    String section(List<String> lines);
}
