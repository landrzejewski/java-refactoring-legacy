using System.Globalization;
using System.Text;

namespace Training.Workshop.M8.S05BoyScout.Step2;

/// <summary>
/// Krok 2: poprawna, mała poprawa Boy Scout - tylko w dotykanej metodzie i bez zmiany kontraktu:
/// Rename, StringBuilder zamiast konkatenacji, string.Join zamiast ręcznej pętli,
/// Extract Method dla linii telefonu. Sortowanie, wielkość liter i format zostają - to decyzje biznesowe.
/// </summary>
public sealed class TicketPrinter
{
    public string Print(Ticket ticket)
    {
        return new StringBuilder()
            .Append("Film: ").Append(ticket.Title).Append('\n')
            .Append("Seans: ").Append(ticket.Start.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append(' ')
            .Append(ticket.Start.ToString("HH:mm", CultureInfo.InvariantCulture)).Append('\n')
            .Append("Miejsca: ").Append(string.Join(", ", ticket.Seats)).Append('\n')
            .Append("Klient: ").Append(ticket.Email.Trim()).Append('\n')
            .Append(PhoneLine(ticket)).Append('\n')
            .Append("Do zaplaty: ").Append(ticket.Total.ToString("F2", CultureInfo.InvariantCulture))
            .Append('\n')
            .ToString();
    }

    private static string PhoneLine(Ticket ticket)
    {
        return "Tel: " + (ticket.Phone ?? "-");
    }
}
