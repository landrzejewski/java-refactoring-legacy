package pl.training.workshop.exercises.e07_documents

/** Stabilny kontrakt sceny: opcjonalne sekcje dokumentu, które klient może dołączyć. */
data class PrintOptions(val withVat: Boolean, val withPromo: Boolean, val withLoyaltyFooter: Boolean) {
    companion object {
        val NONE = PrintOptions(false, false, false)
        val ALL = PrintOptions(true, true, true)
    }
}
