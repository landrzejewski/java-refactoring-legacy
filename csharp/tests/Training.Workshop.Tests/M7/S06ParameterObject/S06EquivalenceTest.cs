using System.Globalization;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S06ParameterObject;

/// <summary>Test równoważności dla poprawnych terminów: nowe API (od kroku 1) i stare sygnatury.</summary>
public sealed class S06EquivalenceTest
{
    /// <summary>Surowe dane wejściowe - te same dla wszystkich wariantów (Parameter Object powstaje dopiero w krokach).</summary>
    public sealed record Clump(string ScreeningId, DateOnly Date, int Hall, string Format);

    internal static readonly DateOnly Day = new(2026, 3, 10);

    private static readonly Scene<Clump, string> NewApi = Cases(Support.Scene.Variants<Clump, string>()
        .Variant("start", Start)
        .Variant("step1", Step1)
        .Variant("step2", Step2)
        .Variant("step3", Step3));

    private static readonly Scene<Clump, string> Deprecated = DeprecatedScene();

    public static TheoryData<string> NewApiCases => NewApi.Tests();

    public static TheoryData<string> DeprecatedCases => Deprecated.Tests();

    [Theory]
    [MemberData(nameof(NewApiCases))]
    public void NewApiBehavesLikeTheOldOne(string test) => NewApi.Run(test);

    [Theory]
    [MemberData(nameof(DeprecatedCases))]
    public void DeprecatedSignaturesStillWork(string test) => Deprecated.Run(test);

#pragma warning disable CS0618 // odpowiednik @SuppressWarnings("deprecation"): celowo wołamy przestarzałe sygnatury
    private static Scene<Clump, string> DeprecatedScene()
    {
        var step1 = new Training.Workshop.M7.S06ParameterObject.Step1.ScreeningPlanner();
        var step2 = new Training.Workshop.M7.S06ParameterObject.Step2.ScreeningPlanner();
        var step3 = new Training.Workshop.M7.S06ParameterObject.Step3.ScreeningPlanner();
        return Cases(Support.Scene.Variants<Clump, string>()
            .Variant("step1", c => step1.Describe(c.ScreeningId, c.Date, c.Hall, c.Format)
                + " | " + Plain(step1.TicketPrice(c.ScreeningId, c.Date, c.Hall, c.Format)))
            .Variant("step2", c => step2.Describe(c.ScreeningId, c.Date, c.Hall, c.Format)
                + " | " + Plain(step2.TicketPrice(c.ScreeningId, c.Date, c.Hall, c.Format)))
            .Variant("step3", c => step3.Describe(c.ScreeningId, c.Date, c.Hall, c.Format)
                + " | " + Plain(step3.TicketPrice(c.ScreeningId, c.Date, c.Hall, c.Format))));
    }
#pragma warning restore CS0618

    private static Scene<Clump, string> Cases(Scene<Clump, string> scene)
    {
        return scene
            .Expect("IMAX w sali 1", new Clump("S1", Day, 1, "IMAX"), "S1 2026-03-10 sala 1 (IMAX) | 40.00")
            .Expect("3D w sali 2", new Clump("S2", Day, 2, "3D"), "S2 2026-03-10 sala 2 (3D) | 32.00")
            .Expect("2D w sali 8 (granica)", new Clump("S3", Day, 8, "2D"), "S3 2026-03-10 sala 8 (2D) | 25.00");
    }

    internal static string Start(Clump c)
    {
        var planner = new Training.Workshop.M7.S06ParameterObject.Start.ScreeningPlanner();
// po jump/next w Start są już przestarzałe (delegujące) sygnatury z kroków 1-2 - wołamy je celowo
#pragma warning disable CS0618
        return Attempt(_ => planner.Describe(c.ScreeningId, c.Date, c.Hall, c.Format), c)
            + " | " + Attempt(_ => planner.TicketPrice(c.ScreeningId, c.Date, c.Hall, c.Format), c);
#pragma warning restore CS0618
    }

    internal static string Step1(Clump c)
    {
        var planner = new Training.Workshop.M7.S06ParameterObject.Step1.ScreeningPlanner();
        var slot = new Training.Workshop.M7.S06ParameterObject.Step1.ScreeningSlot(
            c.ScreeningId, c.Date, c.Hall, c.Format);
        return Attempt(planner.Describe, slot) + " | " + Attempt(planner.TicketPrice, slot);
    }

    internal static string Step2(Clump c)
    {
        var planner = new Training.Workshop.M7.S06ParameterObject.Step2.ScreeningPlanner();
        var slot = new Training.Workshop.M7.S06ParameterObject.Step2.ScreeningSlot(
            c.ScreeningId, c.Date, c.Hall, c.Format);
        return Attempt(planner.Describe, slot) + " | " + Attempt(planner.TicketPrice, slot);
    }

    internal static string Step3(Clump c)
    {
        var planner = new Training.Workshop.M7.S06ParameterObject.Step3.ScreeningPlanner();
        Training.Workshop.M7.S06ParameterObject.Step3.ScreeningSlot slot;
        try
        {
            slot = new Training.Workshop.M7.S06ParameterObject.Step3.ScreeningSlot(
                c.ScreeningId, c.Date, c.Hall, c.Format);
        }
        catch (ArgumentException e)
        {
            return "new ScreeningSlot -> EXC " + e.Message;
        }
        return Attempt(planner.Describe, slot) + " | " + Attempt(planner.TicketPrice, slot);
    }

    /// <summary>Wynik albo komunikat wyjątku - wyjątek też jest częścią obserwowalnego zachowania.</summary>
    private static string Attempt<T, TResult>(Func<T, TResult> call, T input)
    {
        try
        {
            return Convert.ToString(call(input), CultureInfo.InvariantCulture) ?? "null";
        }
        catch (ArgumentException e)
        {
            return "EXC " + e.Message;
        }
    }

    private static string Plain(decimal amount) => amount.ToString(CultureInfo.InvariantCulture);
}
