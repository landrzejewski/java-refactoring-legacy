using System.Globalization;

namespace Training.Workshop.M3.S15Invariants.Step1;

/// <summary>Krok 1: import tworzy rekord - nadal bez walidacji.</summary>
public sealed class ReservationImport
{
    public string ImportLine(string line)
    {
        var columns = line.Split(';');
        var reservation = new Reservation(columns[0], int.Parse(columns[1], CultureInfo.InvariantCulture),
            decimal.Parse(columns[2], CultureInfo.InvariantCulture));
        return "zaimportowano: " + reservation.Email + ", miejsc " + reservation.Seats
            + ", kwota " + reservation.Total.ToString(CultureInfo.InvariantCulture);
    }
}
