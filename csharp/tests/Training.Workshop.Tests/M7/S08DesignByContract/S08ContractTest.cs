using System.Globalization;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S08DesignByContract;

/// <summary>
/// Dla poprawnych wejść start i kroki są równoważne. Dla niepoprawnych - świadomie NIE:
/// dodanie kontroli kontraktu to zmiana zachowania i test pokazuje ją jawnie.
/// </summary>
public sealed class S08ContractTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene = Support.Scene
        .Variants<IReadOnlyList<string>, string>()
        .Variant("start", Start)
        .Variant("step1", Step1)
        .Variant("step2", Step2)
        .Expect("rezerwacja i zwolnienie", ["reserve 3", "reserve 90", "release 2"],
            "reserve 3 -> true, zostalo 97; reserve 90 -> true, zostalo 7; release 2 -> zostalo 9")
        .Expect("za malo miejsc to false, nie wyjatek", ["reserve 98", "reserve 5"],
            "reserve 98 -> true, zostalo 2; reserve 5 -> false, zostalo 2")
        .Expect("cala sala i zwrot wszystkiego", ["reserve 100", "release 100"],
            "reserve 100 -> true, zostalo 0; release 100 -> zostalo 100");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void ValidUsageBehavesTheSame(string test) => Scene.Run(test);

    [Fact]
    public void StartSilentlyCorruptsStateForInvalidInput()
    {
        Assert.Equal("reserve -2 -> true, zostalo 102", Start(["reserve -2"]));
        Assert.Equal("reserve 10 -> true, zostalo 90; release 15 -> zostalo 105",
            Start(["reserve 10", "release 15"]));
    }

    [Fact]
    public void PreconditionsRejectInvalidInputAndLeaveStateUntouched()
    {
        var negative = "reserve -2 -> ArgumentException: seats must be positive, zostalo 100";
        Assert.Equal(negative, Step1(["reserve -2"]));
        Assert.Equal(negative, Step2(["reserve -2"]));
        var tooMany = "reserve 10 -> true, zostalo 90; "
            + "release 15 -> ArgumentException: cannot release more seats than reserved, zostalo 90";
        Assert.Equal(tooMany, Step1(["reserve 10", "release 15"]));
        Assert.Equal(tooMany, Step2(["reserve 10", "release 15"]));
    }

    private static string Start(IReadOnlyList<string> ops)
    {
        var pool = new Training.Workshop.M7.S08DesignByContract.Start.SeatPool(100);
        return Run(ops, pool.Reserve, pool.Release, () => pool.Remaining);
    }

    private static string Step1(IReadOnlyList<string> ops)
    {
        var pool = new Training.Workshop.M7.S08DesignByContract.Step1.SeatPool(100);
        return Run(ops, pool.Reserve, pool.Release, () => pool.Remaining);
    }

    private static string Step2(IReadOnlyList<string> ops)
    {
        var pool = new Training.Workshop.M7.S08DesignByContract.Step2.SeatPool(100);
        return Run(ops, pool.Reserve, pool.Release, () => pool.Remaining);
    }

    /// <summary>Wykonuje operacje i zapisuje wektor: wynik albo wyjątek oraz stan po każdej operacji.</summary>
    private static string Run(IReadOnlyList<string> ops, Func<int, bool> reserve, Action<int> release,
        Func<int> remaining)
    {
        return string.Join("; ", ops.Select(op =>
        {
            var parts = op.Split(' ');
            var seats = int.Parse(parts[1], CultureInfo.InvariantCulture);
            string WithState(string outcome) => op + " -> " + outcome + "zostalo " + remaining();
            try
            {
                if (parts[0] == "reserve")
                {
                    return WithState((reserve(seats) ? "true" : "false") + ", ");
                }
                release(seats);
                return WithState("");
            }
            catch (Exception e) when (e is ArgumentException or InvalidOperationException)
            {
                return WithState(e.GetType().Name + ": " + e.Message + ", ");
            }
        }));
    }
}
