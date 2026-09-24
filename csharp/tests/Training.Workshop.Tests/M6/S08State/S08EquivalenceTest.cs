using Training.Workshop.M6.S08State;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S08State;

/// <summary>
/// Tabela przejść zapisana PRZED refaktoryzacją: stan x akcja -&gt; wynik. Każdy wariant
/// musi dać ten sam status, te same efekty (w tej samej kolejności) i ten sam wyjątek.
/// </summary>
public sealed class S08EquivalenceTest
{
    /// <summary>Przypadek: jak dojść do stanu (akcje), jaka akcja jest testowana, czy bramka działa.</summary>
    public sealed record Case(IReadOnlyList<string> Setup, string Action, bool GatewayDown);

    private const string Table = """
        New       | pay    | Paid [charged]
        New       | use    | ERROR cannot use in New -> New []
        New       | expire | Expired [seats released]
        New       | cancel | Cancelled [seats released]
        Paid      | pay    | ERROR cannot pay in Paid -> Paid [charged]
        Paid      | use    | Used [charged, gate opened]
        Paid      | expire | ERROR cannot expire in Paid -> Paid [charged]
        Paid      | cancel | Cancelled [charged, refund, seats released]
        Used      | pay    | ERROR cannot pay in Used -> Used [charged, gate opened]
        Used      | use    | ERROR cannot use in Used -> Used [charged, gate opened]
        Used      | expire | ERROR cannot expire in Used -> Used [charged, gate opened]
        Used      | cancel | ERROR cannot cancel in Used -> Used [charged, gate opened]
        Expired   | pay    | ERROR cannot pay in Expired -> Expired [seats released]
        Expired   | use    | ERROR cannot use in Expired -> Expired [seats released]
        Expired   | expire | ERROR cannot expire in Expired -> Expired [seats released]
        Expired   | cancel | ERROR cannot cancel in Expired -> Expired [seats released]
        Cancelled | pay    | ERROR cannot pay in Cancelled -> Cancelled [seats released]
        Cancelled | use    | ERROR cannot use in Cancelled -> Cancelled [seats released]
        Cancelled | expire | ERROR cannot expire in Cancelled -> Cancelled [seats released]
        Cancelled | cancel | ERROR cannot cancel in Cancelled -> Cancelled [seats released]
        """;

    private static readonly Scene<Case, string> Scene = BuildScene();

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepFollowsTheTransitionTable(string test) => Scene.Run(test);

    private static Scene<Case, string> BuildScene()
    {
        var scene = Support.Scene.Variants<Case, string>()
            .Variant("start", c => Play(c, (id, payments) => new Training.Workshop.M6.S08State.Start.Reservation(id, payments)))
            .Variant("step1", c => Play(c, (id, payments) => new Training.Workshop.M6.S08State.Step1.Reservation(id, payments)))
            .Variant("step2", c => Play(c, (id, payments) => new Training.Workshop.M6.S08State.Step2.Reservation(id, payments)))
            .Variant("step3", c => Play(c, (id, payments) => new Training.Workshop.M6.S08State.Step3.Reservation(id, payments)));
        foreach (var line in Table.Trim().Split('\n'))
        {
            var cells = line.Split('|');
            var from = cells[0].Trim();
            var action = cells[1].Trim();
            scene.Expect(from + " + " + action, new Case(SetupFor(from), action, false), cells[2].Trim());
        }
        return scene.Expect("bramka niedostępna: stan i efekty bez zmian",
            new Case([], "pay", true), "ERROR bramka niedostepna -> New []");
    }

    private static IReadOnlyList<string> SetupFor(string status) => status switch
    {
        "New" => [],
        "Paid" => ["pay"],
        "Used" => ["pay", "use"],
        "Expired" => ["expire"],
        "Cancelled" => ["cancel"],
        _ => throw new ArgumentException(status),
    };

    private static string Play(Case c, Func<string, IPayments, IReservationActions> factory)
    {
        var reservation = factory("R1", new FakePayments(c.GatewayDown));
        foreach (var action in c.Setup)
        {
            Apply(reservation, action);
        }
        try
        {
            Apply(reservation, c.Action);
            return reservation.Status + " " + Show(reservation.Effects);
        }
        catch (InvalidOperationException exception)
        {
            return "ERROR " + exception.Message + " -> " + reservation.Status + " " + Show(reservation.Effects);
        }
    }

    private static string Show(IReadOnlyList<string> effects) => "[" + string.Join(", ", effects) + "]";

    private static void Apply(IReservationActions reservation, string action)
    {
        switch (action)
        {
            case "pay": reservation.Pay(); break;
            case "use": reservation.Use(); break;
            case "expire": reservation.Expire(); break;
            case "cancel": reservation.Cancel(); break;
            default: throw new ArgumentException(action);
        }
    }

    private sealed class FakePayments(bool gatewayDown) : IPayments
    {
        public void Charge(string reservationId)
        {
            if (gatewayDown)
            {
                throw new InvalidOperationException("bramka niedostepna");
            }
        }
    }
}
