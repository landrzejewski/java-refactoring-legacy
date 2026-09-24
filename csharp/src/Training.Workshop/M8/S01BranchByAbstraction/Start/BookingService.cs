using System.Globalization;

namespace Training.Workshop.M8.S01BranchByAbstraction.Start;

/// <summary>
/// Start: cennik skopiowany z CinemaManager.Book() jest wpleciony w potwierdzenie rezerwacji.
/// Chcemy go wymienić na nową implementację (Money, czytelne reguły), ale nie ma szwu:
/// double, magiczne kody formatu i typu biletu, wszystko w jednej metodzie.
/// </summary>
public sealed class BookingService
{
    public string Confirm(BookingRequest request)
    {
        Screening s = request.Screening;
        double sum = 0;
        for (int i = 0; i < request.Seats.Count; i++)
        {
            double p = 0;
            int f = s.Format;
            if (f == 1)
            {
                p = 25.00;
            }
            else if (f == 2)
            {
                p = 32.00;
            }
            else if (f == 3)
            {
                p = 40.00;
            }
            string type = request.Types[i];
            if (type.Equals("S"))
            {
                p = p - p * 0.25;
            }
            else if (type.Equals("E"))
            {
                p = p - p * 0.30;
            }
            else if (type.Equals("C"))
            {
                p = p - p * 0.40;
            }
            if (s.Start.Hour < 12)
            {
                p = p - 5;
            }
            if (int.Parse(request.Seats[i].Substring(1), CultureInfo.InvariantCulture) >= s.VipFromRow)
            {
                p = p + 10;
            }
            if (f == 2 && !request.OwnGlasses)
            {
                p = p + 3;
            }
            sum = sum + p;
        }
        if (request.Seats.Count >= 10)
        {
            sum = sum - sum * 0.10;
        }
        sum = Math.Floor(sum * 100 + 0.5) / 100.0;
        double total = sum;
        if (request.Web)
        {
            total = total + 2.00 * request.Seats.Count;
        }
        return s.Title + ": " + string.Join(",", request.Seats)
            + " - do zaplaty " + total.ToString("F2", CultureInfo.InvariantCulture);
    }
}
