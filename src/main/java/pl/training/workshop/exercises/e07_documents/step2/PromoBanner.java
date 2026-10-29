package pl.training.workshop.exercises.e07_documents.step2;

import java.util.List;

/** Krok 2: baner promocyjny - nie potrzebuje danych sprzedaży, tylko układu kanału. */
public record PromoBanner(Document inner, ReceiptLayout layout) implements Document {
    @Override
    public String render() {
        return inner.render() + "\n" + layout.section(List.of("Kod KINO10: -10% na nastepny bilet online"));
    }
}
