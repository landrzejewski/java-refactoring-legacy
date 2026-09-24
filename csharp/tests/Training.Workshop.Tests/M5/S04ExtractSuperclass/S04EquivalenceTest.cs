using System.Globalization;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S04ExtractSuperclass;

/// <summary>Test równoważności: lista konfliktów w salach jest identyczna w start i każdym kroku.</summary>
public sealed class S04EquivalenceTest
{
    /// <summary>Rezerwacja w teście: seans (Minutes &gt; 0) albo standardowy wynajem (Minutes == 0).</summary>
    public sealed record Booking(string Name, string Hall, string Start, int Minutes)
    {
        public bool Rental => Minutes == 0;

        public DateTime At => DateTime.Parse(Start, CultureInfo.InvariantCulture);
    }

    private static readonly Scene<IReadOnlyList<Booking>, string> Scene = Support.Scene.Variants<IReadOnlyList<Booking>, string>()
        .Variant("start", RunStart)
        .Variant("step1", RunStep1)
        .Variant("step2", RunStep2)
        .Variant("step3", RunStep3)
        .Expect("seans nachodzi na wynajem", [
                new Booking("Diuna", "Sala 1", "2026-10-02T18:00", 166),
                new Booking("Firma X", "Sala 1", "2026-10-02T20:00", 0)],
            "Diuna x Wynajem: Firma X")
        .Expect("różne sale - brak konfliktu", [
                new Booking("Diuna", "Sala 1", "2026-10-02T18:00", 166),
                new Booking("Firma X", "Sala 2", "2026-10-02T18:00", 0)],
            "")
        .Expect("dwa seanse i dwa wynajmy", [
                new Booking("Kraina Lodu", "Sala 2", "2026-10-02T18:30", 102),
                new Booking("Amator", "Sala 2", "2026-10-02T17:00", 100),
                new Booking("Firma Y", "Sala 3", "2026-10-02T11:00", 0),
                new Booking("Firma X", "Sala 3", "2026-10-02T10:00", 0)],
            "Kraina Lodu x Amator; Wynajem: Firma Y x Wynajem: Firma X")
        .Expect("koniec o 20:00 i start o 20:00 - styk to nie konflikt", [
                new Booking("Amator", "Sala 1", "2026-10-02T18:00", 120),
                new Booking("Firma X", "Sala 1", "2026-10-02T20:00", 0)],
            "");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepFindsTheSameConflicts(string test) => Scene.Run(test);

    private static string RunStart(IReadOnlyList<Booking> bookings)
    {
        var screenings = new List<Training.Workshop.M5.S04ExtractSuperclass.Start.Screening>();
        var events = new List<Training.Workshop.M5.S04ExtractSuperclass.Start.PrivateEvent>();
        foreach (var b in bookings)
        {
            if (b.Rental)
            {
                events.Add(Training.Workshop.M5.S04ExtractSuperclass.Start.PrivateEvent.Rental(b.Name, b.Hall, b.At));
            }
            else
            {
                screenings.Add(new Training.Workshop.M5.S04ExtractSuperclass.Start.Screening(b.Name, b.Hall, b.At, b.Minutes));
            }
        }
        return string.Join("; ", new Training.Workshop.M5.S04ExtractSuperclass.Start.HallPlanner().Conflicts(screenings, events));
    }

    private static string RunStep1(IReadOnlyList<Booking> bookings)
    {
        var screenings = new List<Training.Workshop.M5.S04ExtractSuperclass.Step1.Screening>();
        var events = new List<Training.Workshop.M5.S04ExtractSuperclass.Step1.PrivateEvent>();
        foreach (var b in bookings)
        {
            if (b.Rental)
            {
                events.Add(Training.Workshop.M5.S04ExtractSuperclass.Step1.PrivateEvent.Rental(b.Name, b.Hall, b.At));
            }
            else
            {
                screenings.Add(new Training.Workshop.M5.S04ExtractSuperclass.Step1.Screening(b.Name, b.Hall, b.At, b.Minutes));
            }
        }
        return string.Join("; ", new Training.Workshop.M5.S04ExtractSuperclass.Step1.HallPlanner().Conflicts(screenings, events));
    }

    private static string RunStep2(IReadOnlyList<Booking> bookings)
    {
        var screenings = new List<Training.Workshop.M5.S04ExtractSuperclass.Step2.Screening>();
        var events = new List<Training.Workshop.M5.S04ExtractSuperclass.Step2.PrivateEvent>();
        foreach (var b in bookings)
        {
            if (b.Rental)
            {
                events.Add(Training.Workshop.M5.S04ExtractSuperclass.Step2.PrivateEvent.Rental(b.Name, b.Hall, b.At));
            }
            else
            {
                screenings.Add(new Training.Workshop.M5.S04ExtractSuperclass.Step2.Screening(b.Name, b.Hall, b.At, b.Minutes));
            }
        }
        return string.Join("; ", new Training.Workshop.M5.S04ExtractSuperclass.Step2.HallPlanner().Conflicts(screenings, events));
    }

    private static string RunStep3(IReadOnlyList<Booking> bookings)
    {
        var screenings = new List<Training.Workshop.M5.S04ExtractSuperclass.Step3.Screening>();
        var events = new List<Training.Workshop.M5.S04ExtractSuperclass.Step3.PrivateEvent>();
        foreach (var b in bookings)
        {
            if (b.Rental)
            {
                events.Add(Training.Workshop.M5.S04ExtractSuperclass.Step3.PrivateEvent.Rental(b.Name, b.Hall, b.At));
            }
            else
            {
                screenings.Add(new Training.Workshop.M5.S04ExtractSuperclass.Step3.Screening(b.Name, b.Hall, b.At, b.Minutes));
            }
        }
        return string.Join("; ", new Training.Workshop.M5.S04ExtractSuperclass.Step3.HallPlanner().Conflicts(screenings, events));
    }
}
