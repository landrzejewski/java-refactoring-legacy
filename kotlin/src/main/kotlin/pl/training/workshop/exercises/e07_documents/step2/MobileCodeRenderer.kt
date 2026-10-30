package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: kod QR aplikacji mobilnej. */
internal class MobileCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "<qr>" + sale.number + "</qr>"
}
