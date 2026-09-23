package pl.training.workshop.m6.s14_adapter;

/** Preferowany kontrakt kina: wynik płatności niezależny od dostawcy. */
public record PaymentResult(boolean accepted, String transactionId, String declineCode) {
    public static PaymentResult accepted(String transactionId) {
        return new PaymentResult(true, transactionId, null);
    }

    public static PaymentResult declined(String code) {
        return new PaymentResult(false, null, code);
    }
}
