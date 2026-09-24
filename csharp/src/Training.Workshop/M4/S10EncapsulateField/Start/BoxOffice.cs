namespace Training.Workshop.M4.S10EncapsulateField.Start;

/// <summary>Kasa - klient pola Status. GuestEntry łamie niezmiennik z zewnątrz.</summary>
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
