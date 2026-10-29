package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: kod do kontroli biletu (kreskowy, QR albo kontrolny), zależny od kanału. */
public interface CodeRenderer {
    String code(Sale sale);
}
