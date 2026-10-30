package pl.training.workshop.exercises.e07_documents.step1

import pl.training.workshop.exercises.e07_documents.Sale

/**
 * Krok 1: skleja trzy części dokumentu pobrane z jednej rodziny. Nie da się już podać
 * biletu kioskowego z kodem QR - konstruktor przyjmuje zestaw, a nie trzy luźne części.
 */
class DocumentComposer(private val family: DocumentFactory) {
    fun compose(sale: Sale): String = family.ticketLayout().ticket(sale) + "\n" +
        family.codeRenderer().code(sale) + "\n" +
        family.receiptLayout().receipt(sale)
}
