package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: kod kreskowy kiosku - czytnik przy wejściu na salę. */
class KioskCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "|| ||| | " + sale.number + " | ||| ||"
}
