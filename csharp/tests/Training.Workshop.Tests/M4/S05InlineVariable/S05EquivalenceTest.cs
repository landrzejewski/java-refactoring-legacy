using System.Globalization;
using Training.Workshop.M4.S05InlineVariable;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S05InlineVariable;

/// <summary>Test równoważności: te same bilety (numery, etykiety, czasy) po każdym kroku.</summary>
public sealed class S05EquivalenceTest
{
    internal static readonly DateTimeOffset T0 = DateTimeOffset.Parse("2026-09-25T18:00:00Z", CultureInfo.InvariantCulture);

    public sealed record Request(string ScreeningCode, int Format);

    private static readonly Scene<IReadOnlyList<Request>, IReadOnlyList<Ticket>> Scene =
        Support.Scene.Variants<IReadOnlyList<Request>, IReadOnlyList<Ticket>>()
            .Variant("start", Issuing(clock => new Training.Workshop.M4.S05InlineVariable.Start
                .TicketIssuer(clock).Issue))
            .Variant("step1", Issuing(clock => new Training.Workshop.M4.S05InlineVariable.Step1
                .TicketIssuer(clock).Issue))
            .Variant("step2", Issuing(clock => new Training.Workshop.M4.S05InlineVariable.Step2
                .TicketIssuer(clock).Issue))
            .Variant("step3", Issuing(clock => new Training.Workshop.M4.S05InlineVariable.Step3
                .TicketIssuer(clock).Issue))
            .Expect("dwa bilety: kolejne numery, jeden odczyt zegara na bilet",
                [new Request("D1", 3), new Request("K2", 2)],
                [
                    new Ticket("D1-1", "Bilet D1-1, cena 40.00, oplata 2.00",
                        T0, T0.AddSeconds(15 * 60)),
                    new Ticket("K2-2", "Bilet K2-2, cena 32.00, oplata 2.00",
                        T0.AddSeconds(1), T0.AddSeconds(1 + 15 * 60)),
                ])
            .Expect("2D", [new Request("A3", 1)],
                [
                    new Ticket("A3-1", "Bilet A3-1, cena 25.00, oplata 2.00",
                        T0, T0.AddSeconds(15 * 60)),
                ]);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepIssuesTheSameTickets(string test) => Scene.Run(test);

    internal static Func<IReadOnlyList<Request>, IReadOnlyList<Ticket>> Issuing(
        Func<TimeProvider, Func<string, int, Ticket>> issuerFactory)
    {
        return requests =>
        {
            var issuer = issuerFactory(new TickingClock(T0));
            var tickets = new List<Ticket>();
            foreach (var request in requests)
            {
                tickets.Add(issuer(request.ScreeningCode, request.Format));
            }
            return tickets;
        };
    }
}
