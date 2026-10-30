package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: kod kontrolny do odczytu przez bileterkę. */
internal class BoxOfficeCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "Kod kontrolny: #" + sale.number + "#"
}
