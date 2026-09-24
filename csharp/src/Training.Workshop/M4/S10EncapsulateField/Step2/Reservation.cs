namespace Training.Workshop.M4.S10EncapsulateField.Step2;

/// <summary>
/// Krok 2: setter zastąpiony operacjami domenowymi (Move Method z BoxOffice + Remove Setting Method).
/// Warunki przepisane 1:1 - niedozwolone przejście nadal jest cicho ignorowane.
/// Wejście gościa ma teraz uczciwą nazwę zamiast anonimowego <c>Status = "USED"</c>.
/// </summary>
public sealed class Reservation
{
    private string _status = "NEW";

    public string Status => _status;

    public void Pay()
    {
        if (_status == "NEW")
        {
            _status = "PAID";
        }
    }

    public void CheckIn()
    {
        if (_status == "PAID")
        {
            _status = "USED";
        }
    }

    public void Cancel()
    {
        if (_status == "NEW" || _status == "PAID")
        {
            _status = "CANCELLED";
        }
    }

    public void Expire()
    {
        if (_status == "NEW")
        {
            _status = "EXPIRED";
        }
    }

    public void AdmitGuestWithoutPayment()
    {
        _status = "USED";
    }
}
