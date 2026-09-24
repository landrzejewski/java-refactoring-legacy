using System.Globalization;
using Microsoft.Extensions.Time.Testing;
using Training.Workshop.M6.S05ExtractFactory;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S05ExtractFactory;

/// <summary>
/// Scenariusz kilku rezerwacji na jednym serwisie. Linia skryptu: "KANAŁ email miejsca",
/// kanał GROUP oznacza ReserveGroup.
/// </summary>
public sealed class S05EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene = Support.Scene.Variants<IReadOnlyList<string>, string>()
        .Variant("start", script =>
        {
            var service = new Training.Workshop.M6.S05ExtractFactory.Start.ReservationService(Clock());
            return Play(script, service.Reserve, service.ReserveGroup);
        })
        .Variant("step1", script =>
        {
            var service = new Training.Workshop.M6.S05ExtractFactory.Step1.ReservationService(Clock());
            return Play(script, service.Reserve, service.ReserveGroup);
        })
        .Variant("step2", script =>
        {
            var service = new Training.Workshop.M6.S05ExtractFactory.Step2.ReservationService(Clock());
            return Play(script, service.Reserve, service.ReserveGroup);
        })
        .Variant("step3", script =>
        {
            var service = new Training.Workshop.M6.S05ExtractFactory.Step3.ReservationService(Clock());
            return Play(script, service.Reserve, service.ReserveGroup);
        })
        .Expect("kasa i online",
            [
                "BOX_OFFICE jan@kino.pl A1,A2",
                "ONLINE anna@kino.pl A3",
            ],
            """
            R1 BOX_OFFICE [A1, A2] fee=0.00 expires=null
            R2 ONLINE [A3] fee=2.00 expires=2026-10-02T16:15

            """)
        .Expect("zajęte miejsce nie zużywa numeru, nieznany kanał zużywa",
            [
                "ONLINE anna@kino.pl A1",
                "ONLINE jan@kino.pl A1",
                "PHONE jan@kino.pl B1",
                "BOX_OFFICE jan@kino.pl B1",
            ],
            """
            R1 ONLINE [A1] fee=2.00 expires=2026-10-02T16:15
            InvalidOperationException: seat taken: A1
            ArgumentException: unknown channel: PHONE
            R3 BOX_OFFICE [B1] fee=0.00 expires=null

            """)
        .Expect("grupa",
            [
                "GROUP jan@kino.pl C1,C2",
                "GROUP jan@kino.pl C1,C2,C3,C4,C5,C6,C7,C8,C9,C10",
            ],
            """
            ArgumentException: group needs 10+ seats
            R1 ONLINE [C1, C2, C3, C4, C5, C6, C7, C8, C9, C10] fee=20.00 expires=2026-10-02T16:15

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepCreatesTheSameReservations(string test) => Scene.Run(test);

    private static FakeTimeProvider Clock() =>
        new(DateTimeOffset.Parse("2026-10-02T16:00:00Z", CultureInfo.InvariantCulture));

    private static string Play(
        IReadOnlyList<string> script,
        Func<string, string, IReadOnlyList<string>, Reservation> reserve,
        Func<string, IReadOnlyList<string>, Reservation> group)
    {
        var lines = new List<string>();
        foreach (var line in script)
        {
            var parts = line.Split(' ');
            IReadOnlyList<string> seats = parts[2].Split(',');
            try
            {
                var r = parts[0] == "GROUP"
                    ? group(parts[1], seats)
                    : reserve(parts[0], parts[1], seats);
                lines.Add(r.Id + " " + r.Channel + " [" + string.Join(", ", r.Seats) + "] fee=" + r.Fee
                    + " expires=" + (r.ExpiresAt?.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture) ?? "null"));
            }
            catch (Exception exception) when (exception is InvalidOperationException or ArgumentException)
            {
                lines.Add(exception.GetType().Name + ": " + exception.Message);
            }
        }
        return string.Join("\n", lines) + "\n";
    }
}
