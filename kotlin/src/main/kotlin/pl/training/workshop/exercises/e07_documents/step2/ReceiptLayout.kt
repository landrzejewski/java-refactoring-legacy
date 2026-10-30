package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
interface ReceiptLayout {
    fun receipt(sale: Sale): String

    fun section(lines: List<String>): String
}
