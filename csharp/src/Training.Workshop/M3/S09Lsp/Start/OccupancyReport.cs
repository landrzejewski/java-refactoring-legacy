namespace Training.Workshop.M3.S09Lsp.Start;

/// <summary>Klient odczytu: raport obłożenia - potrzebuje tylko planu miejsc.</summary>
public sealed class OccupancyReport
{
    public string Describe(Hall hall)
    {
        return "zajete " + (hall.Capacity - hall.FreeSeats) + " z " + hall.Capacity;
    }
}
