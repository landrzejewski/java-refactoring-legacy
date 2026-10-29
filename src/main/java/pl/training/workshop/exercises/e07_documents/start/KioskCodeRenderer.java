package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: kod kreskowy kiosku - czytnik przy wejściu na salę. */
public final class KioskCodeRenderer implements CodeRenderer {
    @Override
    public String code(Sale sale) {
        return "|| ||| | " + sale.number() + " | ||| ||";
    }
}
