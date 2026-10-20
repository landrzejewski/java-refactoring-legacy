namespace Training.Workshop.M2.S09CleanArchitecture.Step3.App;

/// <summary>Krok 2: port wyjściowy - "daj znać, że rezerwacja powstała".</summary>
public interface IBookingNotifier
{
    void ReservationCreated(string id, NewReservation reservation);
}
