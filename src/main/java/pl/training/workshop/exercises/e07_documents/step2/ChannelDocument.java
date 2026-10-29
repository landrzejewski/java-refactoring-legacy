package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: rdzeń dokumentu - bilet, kod i paragon z jednej rodziny, bez żadnej opcji. */
public record ChannelDocument(Sale sale, DocumentFactory family) implements Document {
    @Override
    public String render() {
        return family.ticketLayout().ticket(sale) + "\n"
                + family.codeRenderer().code(sale) + "\n"
                + family.receiptLayout().receipt(sale);
    }
}
