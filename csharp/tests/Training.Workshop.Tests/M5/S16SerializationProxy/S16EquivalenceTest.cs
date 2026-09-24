using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S16SerializationProxy;

/// <summary>Test równoważności: opis biletu po zapisie i odczycie w tej samej wersji oraz cena - bez zmian.</summary>
public sealed class S16EquivalenceTest
{
    private static readonly Scene<string, string> RoundTrip = Scene.Variants<string, string>()
        .Variant("start", id => TicketStore.RoundTrip(
            new Training.Workshop.M5.S16SerializationProxy.Start.StudentTicket("Amator", "F3", id)).Describe())
        .Variant("step1", id => TicketStore.RoundTrip(
            new Training.Workshop.M5.S16SerializationProxy.Step1.StudentTicket("Amator", "F3", id)).Describe())
        .Variant("step2", id => TicketStore.RoundTrip(
            new Training.Workshop.M5.S16SerializationProxy.Step2.StudentTicket("Amator", "F3", id)).Describe())
        .Variant("step3", id => TicketStore.RoundTrip(
            new Training.Workshop.M5.S16SerializationProxy.Step3.StudentTicket("Amator", "F3", id)).Describe())
        .Expect("bilet studencki", "S-123", "Amator F3 (legitymacja S-123)");

    private static readonly Scene<string, string> Pricing = Scene.Variants<string, string>()
        .Variant("start", p => new Training.Workshop.M5.S16SerializationProxy.Start.TicketPricing().StudentPrice(Money.Of(p)).ToString())
        .Variant("step1", p => new Training.Workshop.M5.S16SerializationProxy.Step1.TicketPricing().StudentPrice(Money.Of(p)).ToString())
        .Variant("step2", p => new Training.Workshop.M5.S16SerializationProxy.Step2.TicketPricing().StudentPrice(Money.Of(p)).ToString())
        .Variant("step3", p => new Training.Workshop.M5.S16SerializationProxy.Step3.TicketPricing().StudentPrice(Money.Of(p)).ToString())
        .Expect("studencki 3D", "32.00", "24.00");

    public static TheoryData<string> RoundTripCases => RoundTrip.Tests();

    public static TheoryData<string> PricingCases => Pricing.Tests();

    [Theory]
    [MemberData(nameof(RoundTripCases))]
    public void EveryStepRoundTripsTicketTheSameWay(string test) => RoundTrip.Run(test);

    [Theory]
    [MemberData(nameof(PricingCases))]
    public void EveryStepPricesStudentTicketTheSameWay(string test) => Pricing.Run(test);
}
