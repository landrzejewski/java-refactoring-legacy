package pl.training.workshop.m3.s11_dip.step3.app;

/**
 * Krok 3: port wyjściowy należy do polityki i nazywa JEJ potrzebę
 * ("powiadom klienta"), a nie możliwości technologii (brak MIME, hosta, kodów SMTP).
 * Niepowodzenie: {@link IllegalStateException} - bez szczegółów protokołu w kontrakcie.
 */
public interface CustomerNotifier {
    void notifyCustomer(String email, String message);
}
