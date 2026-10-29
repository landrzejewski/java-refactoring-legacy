package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.PrintOptions;
import pl.training.workshop.exercises.e07_documents.Sale;

/**
 * Krok 2: składa dokument z rdzenia i opcjonalnych sekcji. Kolejność owijania jest kontraktem
 * (VAT, promocja, stopka lojalnościowa) i jest zapisana tylko tutaj.
 */
public final class DocumentComposer {
    private final DocumentFactory family;

    public DocumentComposer(DocumentFactory family) {
        this.family = family;
    }

    public Document compose(Sale sale, PrintOptions options) {
        ReceiptLayout layout = family.receiptLayout();
        Document document = new ChannelDocument(sale, family);
        if (options.withVat()) {
            document = new VatSection(document, layout, sale);
        }
        if (options.withPromo()) {
            document = new PromoBanner(document, layout);
        }
        if (options.withLoyaltyFooter()) {
            document = new LoyaltyFooter(document, layout, sale);
        }
        return document;
    }
}
