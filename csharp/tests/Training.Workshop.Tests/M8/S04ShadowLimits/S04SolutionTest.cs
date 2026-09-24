using Training.Workshop.M8.S04ShadowLimits;

namespace Training.Workshop.Tests.M8.S04ShadowLimits;

/// <summary>Efekty uboczne w trybie shadow: od podwójnych maili i obciążeń do porównania zamiarów.</summary>
public sealed class S04SolutionTest
{
    private static readonly BookingRequest Anna = new("anna@kino.pl", "4111-1111", "Amator", 2);

    private static readonly IReadOnlyList<string> LegacyEffects =
    [
        "CHARGE 4111-1111: 54.00",
        "SAVE Amator;anna@kino.pl;2;54.00",
        "MAIL anna@kino.pl: Bilety Amator x2, zaplacono 54.00",
    ];

    private const string MailDivergence = "efekty legacy: MAIL anna@kino.pl: Bilety Amator x2, zaplacono 54.00"
        + " | kandydat: MAIL anna@kino.pl: Bilety Amator x2, zaplacono 50.00";

    [Fact]
    public void StartShadowChargesTwiceAndSendsTwoMailsWithoutNoticingAnything()
    {
        var infra = new Infrastructure();
        var shadow = new Training.Workshop.M8.S04ShadowLimits.Start.ShadowBooking(infra);
        shadow.Book(Anna);
        Assert.Equal(2, infra.Count("MAIL")); // klient dostał dwa maile
        Assert.Equal(2, infra.Count("CHARGE")); // karta obciążona dwa razy
        Assert.Equal(2, infra.Count("SAVE")); // dwa wiersze w bazie
        Assert.Empty(shadow.Divergences()); // wynik ten sam, więc cień niczego nie widzi
    }

    [Fact]
    public void Step1PreparatoryRefactoringKeepsTheBugOnPurpose()
    {
        var infra = new Infrastructure();
        new Training.Workshop.M8.S04ShadowLimits.Step1.ShadowBooking(infra).Book(Anna);
        Assert.Equal(2, infra.Count("MAIL"));
        Assert.Equal(2, infra.Count("CHARGE"));
    }

    [Fact]
    public void Step2RecordsCandidateEffectsInsteadOfExecutingThem()
    {
        var infra = new Infrastructure();
        var shadow = new Training.Workshop.M8.S04ShadowLimits.Step2.ShadowBooking(infra);
        shadow.Book(Anna);
        Assert.Equal(LegacyEffects, infra.Log()); // tylko efekty legacy
        Assert.Equal([MailDivergence], shadow.Divergences()); // porównanie efektów znalazło błąd w mailu
    }

    [Fact]
    public void Step3ComparesAPurePlanAndNeverTouchesInfrastructure()
    {
        var infra = new Infrastructure();
        var shadow = new Training.Workshop.M8.S04ShadowLimits.Step3.ShadowBooking(infra);
        shadow.Book(Anna);
        Assert.Equal(LegacyEffects, infra.Log());
        Assert.Equal([MailDivergence], shadow.Divergences());
    }

    [Fact]
    public void Step3NewFlowStillExecutesItsPlanWhenItIsAuthoritative()
    {
        var infra = new Infrastructure();
        var flow = new Training.Workshop.M8.S04ShadowLimits.Step3.NewBookingFlow(
            new Training.Workshop.M8.S04ShadowLimits.Step3.RealEffects(infra));
        Assert.Equal("OK 54.00", flow.Book(Anna));
        Assert.Equal(3, infra.Log().Count);
    }
}
