using Training.Workshop.M7.S12ReturnAsap;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S12ReturnAsap;

/// <summary>
/// Test równoważności: wynik FirstFree, licznik Inspected (efekt uboczny) oraz klasy miejsc.
/// Gdyby return trafił przed _inspected++, licznik różniłby się o jeden - test to wykryje.
/// </summary>
public sealed class S12EquivalenceTest
{
    private static readonly IReadOnlyList<Seat> Hall =
    [
        new Seat("A1", 1, false),
        new Seat("A10", 10, true),
        new Seat("B10", 10, false),
        new Seat("C11", 11, false),
    ];

    public sealed record Query(IReadOnlyList<Seat>? Seats, int MinRow);

    private static readonly Scene<Query, string> Scene = Support.Scene.Variants<Query, string>()
        .Variant("start", q =>
        {
            var finder = new Training.Workshop.M7.S12ReturnAsap.Start.SeatFinder();
            return Show(finder.FirstFree(q.Seats, q.MinRow)) + " sprawdzono=" + finder.Inspected
                + " klasy=" + Classes(seat => finder.SeatClass(seat, 10));
        })
        .Variant("step1", q =>
        {
            var finder = new Training.Workshop.M7.S12ReturnAsap.Step1.SeatFinder();
            return Show(finder.FirstFree(q.Seats, q.MinRow)) + " sprawdzono=" + finder.Inspected
                + " klasy=" + Classes(seat => finder.SeatClass(seat, 10));
        })
        .Variant("step2", q =>
        {
            var finder = new Training.Workshop.M7.S12ReturnAsap.Step2.SeatFinder();
            return Show(finder.FirstFree(q.Seats, q.MinRow)) + " sprawdzono=" + finder.Inspected
                + " klasy=" + Classes(seat => finder.SeatClass(seat, 10));
        })
        .Expect("pierwsze wolne VIP po zajetym", new Query(Hall, 10),
            "B10 sprawdzono=3 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
        .Expect("pierwsze miejsce od razu", new Query(Hall, 1),
            "A1 sprawdzono=1 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
        .Expect("brak pasujacego - przejrzane wszystkie", new Query(Hall, 12),
            "null sprawdzono=4 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
        .Expect("lista null", new Query(null, 1),
            "null sprawdzono=0 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepFindsAndCountsTheSame(string test) => Scene.Run(test);

    /// <summary>Odpowiednik Optional.toString(): wynik albo "null".</summary>
    private static string Show(string? label) => label ?? "null";

    private static string Classes(Func<Seat?, string> seatClass)
    {
        var probes = new List<Seat?>(Hall) { null };
        return "[" + string.Join(", ", probes.Select(seatClass)) + "]";
    }
}
