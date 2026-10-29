package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: układ biletu w danym kanale. */
public interface TicketLayout {
    String ticket(Sale sale);
}
