namespace Training.Workshop.M3.S05Kiss;

/// <summary>
/// Stabilny kontrakt sceny - plan sali. Rzędy numerowane od 1.
/// Znaki w rzędzie: '.' wolne, 'X' zajęte, 'B' zablokowane (awaria), ' ' przejście.
/// </summary>
/// <param name="Rows">rzędy sali</param>
/// <param name="VipFromRow">od tego rzędu (włącznie) miejsca są VIP</param>
public sealed record Hall(IReadOnlyList<string> Rows, int VipFromRow)
{
    public IReadOnlyList<string> Rows { get; } = Rows.ToList().AsReadOnly();
}
