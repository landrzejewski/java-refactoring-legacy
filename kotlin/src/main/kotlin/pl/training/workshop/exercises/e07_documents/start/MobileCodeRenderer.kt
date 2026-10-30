package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: kod QR aplikacji mobilnej. */
class MobileCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "<qr>" + sale.number + "</qr>"
}
