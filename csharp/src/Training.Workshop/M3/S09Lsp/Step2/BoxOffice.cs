namespace Training.Workshop.M3.S09Lsp.Step2;

/// <summary>Klient zapisu: kasa ufa kontraktowi Hall.Reserve.</summary>
public sealed class BoxOffice
{
    public string Sell(Hall hall, int seat)
    {
        hall.Reserve(seat);
        return "sprzedano miejsce " + seat + ", wolnych: " + hall.FreeSeats;
    }
}
