package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: kod do kontroli biletu (kreskowy, QR albo kontrolny), zależny od kanału. */
public interface CodeRenderer {
    String code(Sale sale);
}
