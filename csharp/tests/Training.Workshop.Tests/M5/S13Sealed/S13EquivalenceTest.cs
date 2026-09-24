using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S13Sealed;

/// <summary>Test równoważności: dla znanych typów biletów cena jest identyczna w start i każdym kroku.</summary>
public sealed class S13EquivalenceTest
{
    public sealed record Sale(string Kind, string BasePrice);

    private static readonly Scene<Sale, string> Scene = Support.Scene.Variants<Sale, string>()
        .Variant("start", s =>
        {
            var @base = Money.Of(s.BasePrice);
            Training.Workshop.M5.S13Sealed.Start.Ticket t = s.Kind switch
            {
                "STUDENT" => new Training.Workshop.M5.S13Sealed.Start.StudentTicket(@base),
                "SENIOR" => new Training.Workshop.M5.S13Sealed.Start.SeniorTicket(@base),
                _ => new Training.Workshop.M5.S13Sealed.Start.StandardTicket(@base),
            };
            return new Training.Workshop.M5.S13Sealed.Start.PriceCalculator().Price(t).ToString();
        })
        .Variant("step1", s =>
        {
            var @base = Money.Of(s.BasePrice);
            Training.Workshop.M5.S13Sealed.Step1.Ticket t = s.Kind switch
            {
                "STUDENT" => new Training.Workshop.M5.S13Sealed.Step1.StudentTicket(@base),
                "SENIOR" => new Training.Workshop.M5.S13Sealed.Step1.SeniorTicket(@base),
                _ => new Training.Workshop.M5.S13Sealed.Step1.StandardTicket(@base),
            };
            return new Training.Workshop.M5.S13Sealed.Step1.PriceCalculator().Price(t).ToString();
        })
        .Variant("step2", s =>
        {
            var @base = Money.Of(s.BasePrice);
            Training.Workshop.M5.S13Sealed.Step2.Ticket t = s.Kind switch
            {
                "STUDENT" => new Training.Workshop.M5.S13Sealed.Step2.StudentTicket(@base),
                "SENIOR" => new Training.Workshop.M5.S13Sealed.Step2.SeniorTicket(@base),
                _ => new Training.Workshop.M5.S13Sealed.Step2.StandardTicket(@base),
            };
            return new Training.Workshop.M5.S13Sealed.Step2.PriceCalculator().Price(t).ToString();
        })
        .Variant("step3", s =>
        {
            var @base = Money.Of(s.BasePrice);
            Training.Workshop.M5.S13Sealed.Step3.Ticket t = s.Kind switch
            {
                "STUDENT" => new Training.Workshop.M5.S13Sealed.Step3.StudentTicket(@base),
                "SENIOR" => new Training.Workshop.M5.S13Sealed.Step3.SeniorTicket(@base),
                _ => new Training.Workshop.M5.S13Sealed.Step3.StandardTicket(@base),
            };
            return new Training.Workshop.M5.S13Sealed.Step3.PriceCalculator().Price(t).ToString();
        })
        .Expect("normalny 2D", new Sale("NORMAL", "25.00"), "25.00")
        .Expect("studencki 3D", new Sale("STUDENT", "32.00"), "24.00")
        .Expect("senior IMAX", new Sale("SENIOR", "40.00"), "28.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesKnownTicketsTheSameWay(string test) => Scene.Run(test);
}
