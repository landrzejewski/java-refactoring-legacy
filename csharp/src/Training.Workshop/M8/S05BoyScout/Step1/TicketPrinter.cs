using System.Globalization;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S05BoyScout.Step1;

/// <summary>
/// Krok 1 (NADUŻYCIE - antyprzykład): "skoro już tu jestem, posprzątam wszystko". Obok
/// dobrych ruchów przemycono trzy zmiany zachowania: sortowanie miejsc, e-mail małymi literami
/// i pominięcie linii "Tel" bez telefonu. Test równoważności to wykrywa - ten krok cofamy.
/// </summary>
public sealed class TicketPrinter
{
    public string Print(Ticket ticket)
    {
        var text = new StringBuilder();
        text.Append("Film: ").Append(ticket.Title).Append('\n');
        text.Append("Seans: ").Append(ticket.Start.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append(' ')
            .Append(ticket.Start.ToString("HH:mm", CultureInfo.InvariantCulture)).Append('\n');
        IReadOnlyList<string> sortedSeats = ticket.Seats.Order(StringComparer.Ordinal).ToList();
        text.Append("Miejsca: ").Append(string.Join(", ", sortedSeats)).Append('\n');
        text.Append("Klient: ").Append(ticket.Email.Trim().ToLowerInvariant()).Append('\n');
        if (ticket.Phone != null)
        {
            text.Append("Tel: ").Append(ticket.Phone).Append('\n');
        }
        text.Append("Do zaplaty: ").Append(new Money((decimal)ticket.Total)).Append('\n');
        return text.ToString();
    }
}
