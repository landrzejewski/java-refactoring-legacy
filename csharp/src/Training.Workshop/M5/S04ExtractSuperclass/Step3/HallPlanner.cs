namespace Training.Workshop.M5.S04ExtractSuperclass.Step3;

/// <summary>Krok 3: jedna pętla po wspólnym typie; publiczna sygnatura Conflicts(...) bez zmian.</summary>
public sealed class HallPlanner
{
    public IReadOnlyList<string> Conflicts(IReadOnlyList<Screening> screenings, IReadOnlyList<PrivateEvent> events)
    {
        var bookings = new List<HallBooking>(screenings);
        bookings.AddRange(events);
        var result = new List<string>();
        for (var i = 0; i < bookings.Count; i++)
        {
            for (var j = i + 1; j < bookings.Count; j++)
            {
                var a = bookings[i];
                var b = bookings[j];
                if (a.Overlaps(b))
                {
                    result.Add(a.Name + " x " + b.Name);
                }
            }
        }
        result.Sort(string.CompareOrdinal);
        return result;
    }
}
