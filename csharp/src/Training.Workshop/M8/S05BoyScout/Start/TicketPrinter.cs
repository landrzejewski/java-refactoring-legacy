using System.Globalization;

namespace Training.Workshop.M8.S05BoyScout.Start;

/// <summary>
/// Start: wydruk biletu, do którego i tak musimy zajrzeć (w tym sprincie dochodzi linia "Sala").
/// Nazwy s, d, x, ręczne sklejanie listy miejsc, konkatenacja w pętli. Kusi, żeby "posprzątać wszystko".
/// </summary>
public sealed class TicketPrinter
{
    public string Print(Ticket t)
    {
        string s = "";
        s = s + "Film: " + t.Title + "\n";
        string d = t.Start.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture) + " "
            + t.Start.ToString("HH:mm", CultureInfo.InvariantCulture);
        s = s + "Seans: " + d + "\n";
        string x = "";
        for (int i = 0; i < t.Seats.Count; i++)
        {
            if (i > 0)
            {
                x = x + ", ";
            }
            x = x + t.Seats[i];
        }
        s = s + "Miejsca: " + x + "\n";
        s = s + "Klient: " + t.Email.Trim() + "\n";
        if (t.Phone != null)
        {
            s = s + "Tel: " + t.Phone + "\n";
        }
        else
        {
            s = s + "Tel: -\n";
        }
        s = s + "Do zaplaty: " + t.Total.ToString("F2", CultureInfo.InvariantCulture) + "\n";
        return s;
    }
}
