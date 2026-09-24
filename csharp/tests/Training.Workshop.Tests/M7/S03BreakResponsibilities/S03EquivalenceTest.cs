using Training.Workshop.M7.S03BreakResponsibilities;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S03BreakResponsibilities;

/// <summary>Test równoważności: ten sam wynik i ta sama skrzynka nadawcza w start i każdym kroku.</summary>
public sealed class S03EquivalenceTest
{
    private static readonly Scene<BookingRequest, string> Scene = Support.Scene.Variants<BookingRequest, string>()
        .Variant("start", Observe((outbox, request) =>
            new Training.Workshop.M7.S03BreakResponsibilities.Start.BookingDesk(outbox).Book(request)))
        .Variant("step1", Observe((outbox, request) =>
            new Training.Workshop.M7.S03BreakResponsibilities.Step1.BookingDesk(outbox).Book(request)))
        .Variant("step2", Observe((outbox, request) =>
            new Training.Workshop.M7.S03BreakResponsibilities.Step2.BookingDesk(outbox).Book(request)))
        .Variant("step3", Observe((outbox, request) =>
            new Training.Workshop.M7.S03BreakResponsibilities.Step3.BookingDesk(outbox).Book(request)))
        .Expect("IMAX, jedno miejsce VIP",
            new BookingRequest("anna@kino.pl", "IMAX", ["A5", "C10"]),
            "OK 90.00 | [anna@kino.pl: Rezerwacja 2 miejsc (VIP: 1), do zaplaty 90.00]")
        .Expect("2D bez VIP",
            new BookingRequest("jan@kino.pl", "2D", ["B3"]),
            "OK 25.00 | [jan@kino.pl: Rezerwacja 1 miejsc, do zaplaty 25.00]")
        .Expect("niepoprawny e-mail ma pierwszenstwo",
            new BookingRequest("jan-kino.pl", "2D", []),
            "ERROR: niepoprawny e-mail | []")
        .Expect("brak miejsc",
            new BookingRequest("jan@kino.pl", "3D", []),
            "ERROR: brak miejsc | []")
        .Expect("niepoprawne miejsce",
            new BookingRequest("jan@kino.pl", "3D", ["A1", "Z99"]),
            "ERROR: niepoprawne miejsce Z99 | []");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepBooksAndNotifiesTheSame(string test) => Scene.Run(test);

    private static Func<BookingRequest, string> Observe(Func<Outbox, BookingRequest, string> desk)
    {
        return request =>
        {
            var outbox = new Outbox();
            var result = desk(outbox, request);
            return result + " | [" + string.Join(", ", outbox.Sent()) + "]";
        };
    }
}
