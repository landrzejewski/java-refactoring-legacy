namespace Training.Workshop.M5.S04ExtractSuperclass.Step1;

/// <summary>Krok 1: bez zmian - klient jeszcze nie korzysta z nowego typu.</summary>
public sealed class HallPlanner
{
    public IReadOnlyList<string> Conflicts(IReadOnlyList<Screening> screenings, IReadOnlyList<PrivateEvent> events)
    {
        var result = new List<string>();
        for (var i = 0; i < screenings.Count; i++)
        {
            for (var j = i + 1; j < screenings.Count; j++)
            {
                var a = screenings[i];
                var b = screenings[j];
                if (a.Hall == b.Hall
                    && a.Start < b.End && b.Start < a.End)
                {
                    result.Add(a.Title + " x " + b.Title);
                }
            }
        }
        foreach (var a in screenings)
        {
            foreach (var b in events)
            {
                if (a.Hall == b.Hall
                    && a.Start < b.End && b.Start < a.End)
                {
                    result.Add(a.Title + " x Wynajem: " + b.Client);
                }
            }
        }
        for (var i = 0; i < events.Count; i++)
        {
            for (var j = i + 1; j < events.Count; j++)
            {
                var a = events[i];
                var b = events[j];
                if (a.Hall == b.Hall
                    && a.Start < b.End && b.Start < a.End)
                {
                    result.Add("Wynajem: " + a.Client + " x Wynajem: " + b.Client);
                }
            }
        }
        result.Sort(string.CompareOrdinal);
        return result;
    }
}
