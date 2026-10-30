package pl.training.workshop.exercises.e07_documents.step2

/** Krok 2: baner promocyjny - nie potrzebuje danych sprzedaży, tylko układu kanału. */
data class PromoBanner(val inner: Document, val layout: ReceiptLayout) : Document {
    override fun render(): String = inner.render() + "\n" + layout.section(listOf("Kod KINO10: -10% na nastepny bilet online"))
}
