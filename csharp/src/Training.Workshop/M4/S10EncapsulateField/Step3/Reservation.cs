namespace Training.Workshop.M4.S10EncapsulateField.Step3;

/// <summary>
/// Krok 3 (ŚWIADOMA ZMIANA ZACHOWANIA, osobny commit): operacje pilnują niezmiennika.
/// Niedozwolone przejście rzuca InvalidOperationException zamiast być cicho ignorowane,
/// a gość może wejść tylko na rezerwację NEW albo PAID. To już nie refaktoryzacja.
/// </summary>
public sealed class Reservation
{
    private string _status = "NEW";

    public string Status => _status;

    public void Pay()
    {
        MoveTo("PAID", "NEW");
    }

    public void CheckIn()
    {
        MoveTo("USED", "PAID");
    }

    public void Cancel()
    {
        MoveTo("CANCELLED", "NEW", "PAID");
    }

    public void Expire()
    {
        MoveTo("EXPIRED", "NEW");
    }

    public void AdmitGuestWithoutPayment()
    {
        MoveTo("USED", "NEW", "PAID");
    }

    private void MoveTo(string target, params string[] allowedFrom)
    {
        foreach (var allowed in allowedFrom)
        {
            if (_status == allowed)
            {
                _status = target;
                return;
            }
        }
        throw new InvalidOperationException("Niedozwolone przejscie " + _status + " -> " + target);
    }
}
