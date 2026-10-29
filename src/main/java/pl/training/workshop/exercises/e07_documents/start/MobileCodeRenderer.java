package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: kod QR aplikacji mobilnej. */
public final class MobileCodeRenderer implements CodeRenderer {
    @Override
    public String code(Sale sale) {
        return "<qr>" + sale.number() + "</qr>";
    }
}
