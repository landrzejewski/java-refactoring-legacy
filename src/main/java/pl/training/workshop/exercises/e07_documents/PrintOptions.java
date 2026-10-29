package pl.training.workshop.exercises.e07_documents;

/** Stabilny kontrakt sceny: opcjonalne sekcje dokumentu, które klient może dołączyć. */
public record PrintOptions(boolean withVat, boolean withPromo, boolean withLoyaltyFooter) {
    public static final PrintOptions NONE = new PrintOptions(false, false, false);
    public static final PrintOptions ALL = new PrintOptions(true, true, true);
}
