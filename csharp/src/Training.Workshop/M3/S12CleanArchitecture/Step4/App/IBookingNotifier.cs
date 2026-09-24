namespace Training.Workshop.M3.S12CleanArchitecture.Step4.App;

/// <summary>Krok 2: port wyjściowy - "daj znać, że rezerwacja powstała".</summary>
public interface IBookingNotifier
{
    void ReservationCreated(string id, NewReservation reservation);
}
