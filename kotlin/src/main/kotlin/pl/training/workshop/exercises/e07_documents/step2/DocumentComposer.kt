package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.PrintOptions
import pl.training.workshop.exercises.e07_documents.Sale

/**
 * Krok 2: składa dokument z rdzenia i opcjonalnych sekcji. Kolejność owijania jest kontraktem
 * (VAT, promocja, stopka lojalnościowa) i jest zapisana tylko tutaj.
 */
class DocumentComposer(private val family: DocumentFactory) {
    fun compose(sale: Sale, options: PrintOptions): Document {
        val layout = family.receiptLayout()
        var document: Document = ChannelDocument(sale, family)
        if (options.withVat) {
            document = VatSection(document, layout, sale)
        }
        if (options.withPromo) {
            document = PromoBanner(document, layout)
        }
        if (options.withLoyaltyFooter) {
            document = LoyaltyFooter(document, layout, sale)
        }
        return document
    }
}
