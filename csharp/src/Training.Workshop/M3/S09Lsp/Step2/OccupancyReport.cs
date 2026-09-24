namespace Training.Workshop.M3.S09Lsp.Step2;

/// <summary>Raport zależy od roli ISeatMap ("Use Interface Where Possible"), nie od Hall.</summary>
public sealed class OccupancyReport
{
    public string Describe(ISeatMap seats)
    {
        return "zajete " + (seats.Capacity - seats.FreeSeats) + " z " + seats.Capacity;
    }
}
