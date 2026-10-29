package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: kod kreskowy kiosku - czytnik przy wejściu na salę. */
final class KioskCodeRenderer implements CodeRenderer {
    @Override
    public String code(Sale sale) {
        return "|| ||| | " + sale.number() + " | ||| ||";
    }
}
