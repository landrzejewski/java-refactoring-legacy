namespace Training.Workshop.M3.S05Kiss.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Substitute Algorithm - zwykłe pętle zamiast regex i sekwencji
/// indeksów. Złożoność istotna (co to znaczy "wolne miejsce", od którego rzędu VIP)
/// zostaje, ale jest nazwana: <see cref="Free"/> i <see cref="VipRows"/>.
/// </summary>
public sealed class SeatCounter
{
    private const char Free = '.';

    public string Summary(Hall hall)
    {
        return "wolne: " + FreeIn(hall.Rows) + ", wolne VIP: " + FreeIn(VipRows(hall));
    }

    private static IReadOnlyList<string> VipRows(Hall hall)
    {
        var firstVipIndex = Math.Clamp(hall.VipFromRow - 1, 0, hall.Rows.Count);
        return hall.Rows.Skip(firstVipIndex).ToList();
    }

    private static int FreeIn(IReadOnlyList<string> rows)
    {
        var free = 0;
        foreach (var row in rows)
        {
            foreach (var seat in row)
            {
                if (seat == Free)
                {
                    free++;
                }
            }
        }
        return free;
    }
}
