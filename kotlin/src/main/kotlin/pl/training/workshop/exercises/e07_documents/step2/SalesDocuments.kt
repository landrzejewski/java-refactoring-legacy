package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Channel
import pl.training.workshop.exercises.e07_documents.PrintOptions
import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/**
 * Krok 2: punkt wejścia zna tylko walidację, wybór rodziny i składanie dokumentu.
 * Nie ma tu ani jednego `when` po kanale ani obliczeń żadnej sekcji.
 */
class SalesDocuments {
    fun print(sale: Sale?, channel: Channel?, options: PrintOptions?): String {
        if (sale == null) {
            throw IllegalArgumentException("sale must not be null")
        }
        if (channel == null) {
            throw IllegalArgumentException("channel must not be null")
        }
        if (options == null) {
            throw IllegalArgumentException("options must not be null")
        }
        if (sale.ticketPrice < Money.ZERO) {
            throw IllegalArgumentException("ticket price must not be negative")
        }
        if (sale.barPrice < Money.ZERO) {
            throw IllegalArgumentException("bar price must not be negative")
        }
        return DocumentComposer(DocumentFactories.forChannel(channel)).compose(sale, options).render()
    }
}
