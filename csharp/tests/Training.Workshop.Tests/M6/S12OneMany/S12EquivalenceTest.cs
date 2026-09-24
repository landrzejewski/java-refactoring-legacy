using Training.Workshop.M6.S12OneMany;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S12OneMany;

/// <summary>
/// Klient (CancellationDesk) zwraca jeden bilet albo listę. W Start rozróżnia Refund/RefundAll,
/// w kroku 3 zawsze buduje TicketGroup (także z jednego biletu) - wynik jest ten sam.
/// </summary>
public sealed class S12EquivalenceTest
{
    private static readonly DateTime Now = new(2026, 10, 2, 12, 0, 0);

    private static readonly Scene<IReadOnlyList<TicketData>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<TicketData>, string>()
            .Variant("start", tickets => new Training.Workshop.M6.S12OneMany.Start.CancellationDesk()
                .Refund(tickets, Now).ToString())
            .Variant("step1", tickets => new Training.Workshop.M6.S12OneMany.Step1.CancellationDesk()
                .Refund(tickets, Now).ToString())
            .Variant("step2", tickets => new Training.Workshop.M6.S12OneMany.Step2.CancellationDesk()
                .Refund(tickets, Now).ToString())
            .Variant("step3", tickets => new Training.Workshop.M6.S12OneMany.Step3.CancellationDesk()
                .Refund(tickets, Now).ToString())
            .Expect("jeden, ponad 24h", [Ticket("40.00", 2, 18, 0)], "37.00")
            .Expect("jeden, dokładnie 24h", [Ticket("25.00", 1, 12, 0)], "22.00")
            .Expect("jeden, 23h59m - 50%", [Ticket("25.00", 1, 11, 59)], "9.50")
            .Expect("jeden, po starcie - potrącenie nie schodzi poniżej 0",
                [Ticket("25.00", 0, 11, 0)], "0.00")
            .Expect("wiele - potrącenie raz", [
                Ticket("40.00", 2, 18, 0), Ticket("32.00", 0, 20, 0), Ticket("25.00", 0, 11, 0)], "53.00")
            .Expect("pusta lista", [], "0.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepRefundsTheSame(string test) => Scene.Run(test);

    private static TicketData Ticket(string price, int daysAfter, int hour, int minute)
    {
        return new TicketData(Money.Of(price), Now.Date.AddDays(daysAfter).Add(new TimeSpan(hour, minute, 0)));
    }
}
