package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: kod do kontroli biletu (kreskowy, QR albo kontrolny), zależny od kanału. */
fun interface CodeRenderer {
    fun code(sale: Sale): String
}
