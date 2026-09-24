using Training.Workshop.M7.S07Arrowhead;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S07Arrowhead;

/// <summary>
/// Macierz gałęzi: każda ścieżka plus kombinacje sprawdzające priorytet warunków.
/// Obserwujemy wynik ORAZ audyt - guard clause wstawiona w Book() zgubiłaby wpis.
/// </summary>
public sealed class S07EquivalenceTest
{
    private static readonly Scene<BookingAttempt, string> Scene = Support.Scene.Variants<BookingAttempt, string>()
        .Variant("start", Observe(() => new Training.Workshop.M7.S07Arrowhead.Start.BookingGate(),
            (g, a) => g.Book(a), g => g.Audit))
        .Variant("step1", Observe(() => new Training.Workshop.M7.S07Arrowhead.Step1.BookingGate(),
            (g, a) => g.Book(a), g => g.Audit))
        .Variant("step2", Observe(() => new Training.Workshop.M7.S07Arrowhead.Step2.BookingGate(),
            (g, a) => g.Book(a), g => g.Audit))
        .Variant("step3", Observe(() => new Training.Workshop.M7.S07Arrowhead.Step3.BookingGate(),
            (g, a) => g.Book(a), g => g.Audit))
        .Expect("sciezka glowna", Attempt(true, true, false, 2, 5), "BOOKED [anna@kino.pl -> BOOKED]")
        .Expect("dokladnie tyle wolnych", Attempt(true, true, false, 5, 5), "BOOKED [anna@kino.pl -> BOOKED]")
        .Expect("brak seansu wygrywa ze wszystkim", Attempt(false, false, true, 0, 0),
            "NO_SCREENING [anna@kino.pl -> NO_SCREENING]")
        .Expect("sprzedaz zamknieta przed blokada klienta", Attempt(true, false, true, 2, 5),
            "SALES_CLOSED [anna@kino.pl -> SALES_CLOSED]")
        .Expect("blokada klienta przed liczba miejsc", Attempt(true, true, true, 0, 5),
            "CUSTOMER_BLOCKED [anna@kino.pl -> CUSTOMER_BLOCKED]")
        .Expect("zero miejsc przed brakiem wolnych", Attempt(true, true, false, 0, 0),
            "NO_SEATS_REQUESTED [anna@kino.pl -> NO_SEATS_REQUESTED]")
        .Expect("wyprzedane", Attempt(true, true, false, 6, 5), "SOLD_OUT [anna@kino.pl -> SOLD_OUT]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepKeepsBranchesPriorityAndAudit(string test) => Scene.Run(test);

    private static BookingAttempt Attempt(bool found, bool open, bool blocked, int requested, int free)
    {
        return new BookingAttempt("anna@kino.pl", found, open, blocked, requested, free);
    }

    private static Func<BookingAttempt, string> Observe<TGate>(Func<TGate> gates,
        Func<TGate, BookingAttempt, string> book, Func<TGate, IReadOnlyList<string>> audit)
    {
        return attempt =>
        {
            var gate = gates();
            var result = book(gate, attempt);
            return result + " [" + string.Join(", ", audit(gate)) + "]";
        };
    }
}
