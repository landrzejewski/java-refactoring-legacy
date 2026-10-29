package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: układ biletu w danym kanale. */
public interface TicketLayout {
    String ticket(Sale sale);
}
