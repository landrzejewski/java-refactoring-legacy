package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: kod kreskowy kiosku - czytnik przy wejściu na salę. */
internal class KioskCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "|| ||| | " + sale.number + " | ||| ||"
}
