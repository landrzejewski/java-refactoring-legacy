package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: kod QR aplikacji mobilnej. */
final class MobileCodeRenderer implements CodeRenderer {
    @Override
    public String code(Sale sale) {
        return "<qr>" + sale.number() + "</qr>";
    }
}
