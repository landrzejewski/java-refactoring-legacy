package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: układ biletu w danym kanale. */
public interface TicketLayout {
    String ticket(Sale sale);
}
