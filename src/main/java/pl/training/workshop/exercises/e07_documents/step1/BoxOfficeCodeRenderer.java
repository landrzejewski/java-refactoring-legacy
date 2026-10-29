package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: kod kontrolny do odczytu przez bileterkę. */
final class BoxOfficeCodeRenderer implements CodeRenderer {
    @Override
    public String code(Sale sale) {
        return "Kod kontrolny: #" + sale.number() + "#";
    }
}
