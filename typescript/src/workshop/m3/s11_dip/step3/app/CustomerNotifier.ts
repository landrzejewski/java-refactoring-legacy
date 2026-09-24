/**
 * Krok 3: port wyjściowy należy do polityki i nazywa JEJ potrzebę
 * ("powiadom klienta"), a nie możliwości technologii (brak MIME, hosta, kodów SMTP).
 * Niepowodzenie: `IllegalStateError` - bez szczegółów protokołu w kontrakcie.
 */
export interface CustomerNotifier {
  notifyCustomer(email: string, message: string): void;
}
