using Training.Workshop.M3.S12CleanArchitecture;

namespace Training.Workshop.Tests.M3.S12CleanArchitecture;

/// <summary>Z zewnątrz (odpowiedź, zapisane wiersze, komunikaty) każdy krok zachowuje się identycznie.</summary>
public sealed class S12EquivalenceTest
{
    public sealed record Request(IReadOnlyDictionary<string, string> Params, bool DbAvailable);

    private static readonly Support.Scene<Request, string> Scene = Support.Scene.Variants<Request, string>()
        .Variant("start", Run((db, outbox) => Training.Workshop.M3.S12CleanArchitecture.Start
            .CinemaApplication.ReservationController(db, outbox).Handle))
        .Variant("step1", Run((db, outbox) => Training.Workshop.M3.S12CleanArchitecture.Step1
            .CinemaApplication.ReservationController(db, outbox).Handle))
        .Variant("step2", Run((db, outbox) => Training.Workshop.M3.S12CleanArchitecture.Step2
            .CinemaApplication.ReservationController(db, outbox).Handle))
        .Variant("step3", Run((db, outbox) => Training.Workshop.M3.S12CleanArchitecture.Step3
            .CinemaApplication.ReservationController(db, outbox).Handle))
        .Variant("step4", Run((db, outbox) => Training.Workshop.M3.S12CleanArchitecture.Step4
            .CinemaApplication.ReservationController(db, outbox).Handle))
        .Expect("IMAX, jedno miejsce VIP",
            new Request(Params(("email", "anna@kino.pl"), ("format", "IMAX"), ("rows", "5,10")), true),
            "201 R-1 90.00 | db=[anna@kino.pl;IMAX;2;90.00]"
                + " | outbox=[reservation-created:R-1;anna@kino.pl;90.00]")
        .Expect("brak adresu e-mail", new Request(Params(("format", "2D"), ("rows", "3")), true),
            "400 brak email | db=[] | outbox=[]")
        .Expect("brak miejsc", new Request(Params(("email", "jan@kino.pl"), ("rows", "")), true),
            "400 brak miejsc | db=[] | outbox=[]")
        .Expect("baza niedostepna - brak powiadomienia",
            new Request(Params(("email", "jan@kino.pl"), ("format", "2D"), ("rows", "3")), false),
            "503 baza niedostepna | db=[] | outbox=[]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepHandlesRequestsTheSame(string test) => Scene.Run(test);

    private static Func<Request, string> Run(
        Func<RowStore, Outbox, Func<IReadOnlyDictionary<string, string>, string>> app)
    {
        return request =>
        {
            var db = new RowStore(request.DbAvailable);
            var outbox = new Outbox();
            var response = app(db, outbox)(request.Params);
            return response + " | db=" + Show(db.Dump()) + " | outbox=" + Show(outbox.Messages);
        };
    }

    private static IReadOnlyDictionary<string, string> Params(params (string Key, string Value)[] entries) =>
        entries.ToDictionary(e => e.Key, e => e.Value);

    private static string Show(IReadOnlyList<string> items) => "[" + string.Join(", ", items) + "]";
}
