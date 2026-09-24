using System.Globalization;

namespace Training.Workshop.M3.S15Invariants.Start;

/// <summary>Start: import z pliku partnera "email;miejsca;kwota" - tworzy model z pominięciem walidacji.</summary>
public sealed class ReservationImport
{
    public string ImportLine(string line)
    {
        var columns = line.Split(';');
        var reservation = new Reservation();
        reservation.Email = columns[0];
        reservation.Seats = int.Parse(columns[1], CultureInfo.InvariantCulture);
        reservation.Total = decimal.Parse(columns[2], CultureInfo.InvariantCulture);
        return "zaimportowano: " + reservation.Email + ", miejsc " + reservation.Seats
            + ", kwota " + reservation.Total.ToString(CultureInfo.InvariantCulture);
    }
}
