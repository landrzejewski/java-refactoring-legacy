namespace Training.Workshop.M4.S10EncapsulateField.Step1;

/// <summary>
/// Krok 1: klienci bez zmian w źródle - w C# Encapsulate Field zamienia pole na właściwość
/// o tej samej nazwie, więc odczyt i przypisanie <c>r.Status</c> idą teraz przez get/set.
/// </summary>
public sealed class BoxOffice
{
    public void Pay(Reservation r)
    {
        if (r.Status == "NEW")
        {
            r.Status = "PAID";
        }
    }

    public void CheckIn(Reservation r)
    {
        if (r.Status == "PAID")
        {
            r.Status = "USED";
        }
    }

    public void Cancel(Reservation r)
    {
        if (r.Status == "NEW" || r.Status == "PAID")
        {
            r.Status = "CANCELLED";
        }
    }

    public void Expire(Reservation r)
    {
        if (r.Status == "NEW")
        {
            r.Status = "EXPIRED";
        }
    }

    /// <summary>Wejście gościa kierownika - "na skróty", bez płatności i bez sprawdzania statusu.</summary>
    public void GuestEntry(Reservation r)
    {
        r.Status = "USED";
    }
}
