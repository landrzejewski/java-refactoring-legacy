namespace Training.Workshop.M3.S11Dip.Step3.App;

/// <summary>
/// Krok 3: port wyjściowy należy do polityki i nazywa JEJ potrzebę
/// ("powiadom klienta"), a nie możliwości technologii (brak MIME, hosta, kodów SMTP).
/// Niepowodzenie: <see cref="InvalidOperationException"/> - bez szczegółów protokołu w kontrakcie.
/// </summary>
public interface ICustomerNotifier
{
    void NotifyCustomer(string email, string message);
}
