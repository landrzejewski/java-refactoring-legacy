namespace Training.Workshop.M7.S13GodClass.Step4;

/// <summary>
/// Krok 3: rezerwacja jako typ zamiast object?[] {screeningId, email, phone, seats, types, web,
/// total, status, createdAt, card, ticketsSum}. Status zostaje kodem int jak w legacy -
/// zamiana na enum to kolejny, osobny krok.
/// </summary>
internal sealed class Booking
{
    // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED

    internal Booking(string id, string screeningId, string email, string? phone, string[] seats, string[] types,
        bool web, double total, DateTime createdAt, double ticketsSum)
    {
        Id = id;
        ScreeningId = screeningId;
        Email = email;
        Phone = phone;
        Seats = seats;
        Types = types;
        Web = web;
        Total = total;
        CreatedAt = createdAt;
        TicketsSum = ticketsSum;
    }

    internal string Id { get; }

    internal string ScreeningId { get; }

    internal string Email { get; }

    internal string? Phone { get; }

    internal string[] Seats { get; }

    internal string[] Types { get; }

    internal bool Web { get; }

    internal double Total { get; }

    internal DateTime CreatedAt { get; }

    internal double TicketsSum { get; }

    internal int Status { get; set; }

    internal string? Card { get; private set; }

    internal void MarkPaid(string? card)
    {
        Status = 1;
        Card = card;
    }
}
