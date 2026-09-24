using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S07Adr;

/// <summary>Test równoważności: dochodzenie do zgodności z ADR nie zmienia odpowiedzi ani wysłanych maili.</summary>
public sealed class S07EquivalenceTest
{
    public sealed record Order(string Organizer, int Tickets, string Format);

    private static readonly Scene<Order, string> Scene = Support.Scene.Variants<Order, string>()
        .Variant("start", order =>
        {
            var service = new Training.Workshop.M8.S07Adr.Start.BookingService();
            return service.Book(order.Organizer, order.Tickets, order.Format) + " " + Show(service.SentMails());
        })
        .Variant("step1", order =>
        {
            var service = new Training.Workshop.M8.S07Adr.Step1.BookingService();
            return service.Book(order.Organizer, order.Tickets, order.Format) + " " + Show(service.SentMails());
        })
        .Variant("step2", order =>
        {
            var service = new Training.Workshop.M8.S07Adr.Step2.BookingService();
            return service.Book(order.Organizer, order.Tickets, order.Format) + " " + Show(service.SentMails());
        })
        .Expect("grupa 12 biletów 2D", new Order("anna@kino.pl", 12, "2D"),
            "DO ZAPLATY 270.00 [anna@kino.pl: rabat grupowy dla 12 biletow]")
        .Expect("2 bilety IMAX", new Order("jan@kino.pl", 2, "IMAX"), "DO ZAPLATY 80.00 []")
        .Expect("grupa 10 biletów 3D", new Order("ola@kino.pl", 10, "3D"),
            "DO ZAPLATY 288.00 [ola@kino.pl: rabat grupowy dla 10 biletow]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepBooksTheSameWay(string test) => Scene.Run(test);

    private static string Show(IReadOnlyList<string> mails) => "[" + string.Join(", ", mails) + "]";
}
