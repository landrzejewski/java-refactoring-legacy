package pl.training.workshop.exercises.e07_documents.step1

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 1: kod QR aplikacji mobilnej. */
internal class MobileCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "<qr>" + sale.number + "</qr>"
}
