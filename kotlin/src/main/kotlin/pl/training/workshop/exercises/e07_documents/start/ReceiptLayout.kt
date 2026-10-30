package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
interface ReceiptLayout {
    fun receipt(sale: Sale): String

    fun section(lines: List<String>): String
}
