using Training.Workshop.M8.S04ShadowLimits;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S04ShadowLimits;

/// <summary>Test równoważności: odpowiedź dla klienta (wynik legacy) jest taka sama w każdym kroku.</summary>
public sealed class S04EquivalenceTest
{
    private static readonly Scene<BookingRequest, string> Scene = Support.Scene.Variants<BookingRequest, string>()
        .Variant("start", r => new Training.Workshop.M8.S04ShadowLimits.Start.ShadowBooking(
            new Infrastructure()).Book(r))
        .Variant("step1", r => new Training.Workshop.M8.S04ShadowLimits.Step1.ShadowBooking(
            new Infrastructure()).Book(r))
        .Variant("step2", r => new Training.Workshop.M8.S04ShadowLimits.Step2.ShadowBooking(
            new Infrastructure()).Book(r))
        .Variant("step3", r => new Training.Workshop.M8.S04ShadowLimits.Step3.ShadowBooking(
            new Infrastructure()).Book(r))
        .Expect("2 bilety online", new BookingRequest("anna@kino.pl", "4111-1111", "Amator", 2), "OK 54.00")
        .Expect("1 bilet online", new BookingRequest("jan@kino.pl", "5500-2222", "Diuna", 1), "OK 27.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void CustomerGetsTheSameAnswer(string test) => Scene.Run(test);
}
