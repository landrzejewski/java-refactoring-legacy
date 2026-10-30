package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: kod kontrolny do odczytu przez bileterkę. */
class BoxOfficeCodeRenderer : CodeRenderer {
    override fun code(sale: Sale): String = "Kod kontrolny: #" + sale.number + "#"
}
