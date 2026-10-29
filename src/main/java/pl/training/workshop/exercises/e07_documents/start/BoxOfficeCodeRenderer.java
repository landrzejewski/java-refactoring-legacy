package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: kod kontrolny do odczytu przez bileterkę. */
public final class BoxOfficeCodeRenderer implements CodeRenderer {
    @Override
    public String code(Sale sale) {
        return "Kod kontrolny: #" + sale.number() + "#";
    }
}
