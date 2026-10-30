package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: rdzeń dokumentu - bilet, kod i paragon z jednej rodziny, bez żadnej opcji. */
data class ChannelDocument(val sale: Sale, val family: DocumentFactory) : Document {
    override fun render(): String = family.ticketLayout().ticket(sale) + "\n" +
        family.codeRenderer().code(sale) + "\n" +
        family.receiptLayout().receipt(sale)
}
