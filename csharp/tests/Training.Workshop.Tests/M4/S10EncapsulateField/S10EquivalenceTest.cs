using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S10EncapsulateField;

/// <summary>
/// Start, Step1, Step2 - refaktoryzacja: identyczny ślad statusów, także dla niedozwolonych przejść.
/// Step3 - świadoma zmiana zachowania: dozwolone ścieżki bez zmian, niedozwolone kończą się BLAD.
/// </summary>
public sealed class S10EquivalenceTest
{
    private static readonly IReadOnlyList<string> PayAndEnter = ["pay", "checkIn"];
    private static readonly IReadOnlyList<string> CancelOnly = ["cancel"];
    private static readonly IReadOnlyList<string> Guest = ["guest"];
    private static readonly IReadOnlyList<string> ExpiredThenPay = ["expire", "pay"];
    private static readonly IReadOnlyList<string> PayTwice = ["pay", "pay"];
    private static readonly IReadOnlyList<string> EnterUnpaid = ["checkIn"];
    private static readonly IReadOnlyList<string> GuestOnCancelled = ["cancel", "guest"];

    private static readonly Scene<IReadOnlyList<string>, string> EncapsulationScene = WithAllowedPaths(
        Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", commands =>
            {
                var office = new Training.Workshop.M4.S10EncapsulateField.Start.BoxOffice();
                var r = new Training.Workshop.M4.S10EncapsulateField.Start.Reservation();
                return Trace(commands, command => command switch
                {
                    "pay" => office.Pay,
                    "checkIn" => office.CheckIn,
                    "cancel" => office.Cancel,
                    "expire" => office.Expire,
                    _ => office.GuestEntry,
                }, r, () => r.Status);
            })
            .Variant("step1", commands =>
            {
                var office = new Training.Workshop.M4.S10EncapsulateField.Step1.BoxOffice();
                var r = new Training.Workshop.M4.S10EncapsulateField.Step1.Reservation();
                return Trace(commands, command => command switch
                {
                    "pay" => office.Pay,
                    "checkIn" => office.CheckIn,
                    "cancel" => office.Cancel,
                    "expire" => office.Expire,
                    _ => office.GuestEntry,
                }, r, () => r.Status);
            })
            .Variant("step2", commands =>
            {
                var office = new Training.Workshop.M4.S10EncapsulateField.Step2.BoxOffice();
                var r = new Training.Workshop.M4.S10EncapsulateField.Step2.Reservation();
                return Trace(commands, command => command switch
                {
                    "pay" => office.Pay,
                    "checkIn" => office.CheckIn,
                    "cancel" => office.Cancel,
                    "expire" => office.Expire,
                    _ => office.GuestEntry,
                }, r, () => r.Status);
            }))
        .Expect("ZASTANE: płatność po wygaśnięciu cicho ignorowana", ExpiredThenPay, "EXPIRED,EXPIRED")
        .Expect("ZASTANE: druga płatność cicho ignorowana", PayTwice, "PAID,PAID")
        .Expect("ZASTANE: wejście bez płatności cicho ignorowane", EnterUnpaid, "NEW")
        .Expect("ZASTANE: gość wchodzi na anulowaną rezerwację", GuestOnCancelled, "CANCELLED,USED");

    private static readonly Scene<IReadOnlyList<string>, string> Step3Scene = WithAllowedPaths(
        Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("step3", commands =>
            {
                var office = new Training.Workshop.M4.S10EncapsulateField.Step3.BoxOffice();
                var r = new Training.Workshop.M4.S10EncapsulateField.Step3.Reservation();
                return Trace(commands, command => command switch
                {
                    "pay" => office.Pay,
                    "checkIn" => office.CheckIn,
                    "cancel" => office.Cancel,
                    "expire" => office.Expire,
                    _ => office.GuestEntry,
                }, r, () => r.Status);
            }))
        .Expect("ZMIANA: płatność po wygaśnięciu odrzucona", ExpiredThenPay, "EXPIRED,BLAD")
        .Expect("ZMIANA: druga płatność odrzucona", PayTwice, "PAID,BLAD")
        .Expect("ZMIANA: wejście bez płatności odrzucone", EnterUnpaid, "BLAD")
        .Expect("ZMIANA: gość nie wejdzie na anulowaną rezerwację", GuestOnCancelled, "CANCELLED,BLAD");

    public static TheoryData<string> EncapsulationCases => EncapsulationScene.Tests();

    public static TheoryData<string> Step3Cases => Step3Scene.Tests();

    [Theory]
    [MemberData(nameof(EncapsulationCases))]
    public void EncapsulationKeepsBehaviour(string test) => EncapsulationScene.Run(test);

    [Theory]
    [MemberData(nameof(Step3Cases))]
    public void Step3RejectsIllegalTransitions(string test) => Step3Scene.Run(test);

    [Fact]
    public void StartFieldIsPublicFromStep1ItIsNot()
    {
        Assert.NotNull(typeof(Training.Workshop.M4.S10EncapsulateField.Start.Reservation).GetField("Status"));
        Assert.Null(typeof(Training.Workshop.M4.S10EncapsulateField.Step1.Reservation).GetField("Status"));
    }

    /// <summary>Ścieżki dozwolone - wspólne oczekiwania dla wszystkich wariantów, także Step3.</summary>
    private static Scene<IReadOnlyList<string>, string> WithAllowedPaths(Scene<IReadOnlyList<string>, string> scene)
    {
        return scene
            .Expect("zapłata i wejście", PayAndEnter, "PAID,USED")
            .Expect("anulowanie nowej", CancelOnly, "CANCELLED")
            .Expect("gość na nową rezerwację", Guest, "USED");
    }

    /// <summary>Wykonuje polecenia i zapisuje status po każdym (albo BLAD, gdy operacja rzuciła wyjątek).</summary>
    private static string Trace<TReservation>(IReadOnlyList<string> commands,
        Func<string, Action<TReservation>> action, TReservation reservation, Func<string> status)
    {
        var trace = new List<string>();
        foreach (var command in commands)
        {
            try
            {
                action(command)(reservation);
                trace.Add(status());
            }
            catch (InvalidOperationException)
            {
                trace.Add("BLAD");
            }
        }
        return string.Join(",", trace);
    }
}
