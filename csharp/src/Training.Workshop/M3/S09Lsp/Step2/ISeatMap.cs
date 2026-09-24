namespace Training.Workshop.M3.S09Lsp.Step2;

/// <summary>
/// Rola "plan miejsc do odczytu".
/// Kontrakt: <c>FreeSeats</c> równa się liczbie miejsc 1..Capacity, dla których <c>IsFree</c>.
/// </summary>
public interface ISeatMap
{
    bool IsFree(int seat);

    int FreeSeats { get; }

    int Capacity { get; }
}
