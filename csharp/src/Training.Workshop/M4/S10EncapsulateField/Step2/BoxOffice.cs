namespace Training.Workshop.M4.S10EncapsulateField.Step2;

/// <summary>Krok 2: kasa tylko deleguje - reguły przejść mają jednego właściciela.</summary>
public sealed class BoxOffice
{
    public void Pay(Reservation r)
    {
        r.Pay();
    }

    public void CheckIn(Reservation r)
    {
        r.CheckIn();
    }

    public void Cancel(Reservation r)
    {
        r.Cancel();
    }

    public void Expire(Reservation r)
    {
        r.Expire();
    }

    public void GuestEntry(Reservation r)
    {
        r.AdmitGuestWithoutPayment();
    }
}
