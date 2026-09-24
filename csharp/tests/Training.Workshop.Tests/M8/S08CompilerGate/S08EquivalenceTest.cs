using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S08CompilerGate;

/// <summary>Test równoważności: usuwanie ostrzeżeń kompilatora nie zmienia raportu obłożenia.</summary>
public sealed class S08EquivalenceTest
{
    public sealed record Input(int Format, IReadOnlyList<string> Seats);

    private static readonly Scene<Input, string> Scene = Support.Scene.Variants<Input, string>()
        .Variant("start", input =>
        {
            var map = new Training.Workshop.M8.S08CompilerGate.Start.SeatMap();
            foreach (var seat in input.Seats)
            {
                map.Take(seat);
            }
            return new Training.Workshop.M8.S08CompilerGate.Start.OccupancyReport().Describe(map, input.Format);
        })
        .Variant("step1", input =>
        {
            var map = new Training.Workshop.M8.S08CompilerGate.Step1.SeatMap();
            foreach (var seat in input.Seats)
            {
                map.Take(seat);
            }
            return new Training.Workshop.M8.S08CompilerGate.Step1.OccupancyReport().Describe(map, input.Format);
        })
        .Variant("step2", input =>
        {
            var map = new Training.Workshop.M8.S08CompilerGate.Step2.SeatMap();
            foreach (var seat in input.Seats)
            {
                map.Take(seat);
            }
            return new Training.Workshop.M8.S08CompilerGate.Step2.OccupancyReport().Describe(map, input.Format);
        })
        .Variant("step3", input =>
        {
            var map = new Training.Workshop.M8.S08CompilerGate.Step3.SeatMap();
            foreach (var seat in input.Seats)
            {
                map.Take(seat);
            }
            return new Training.Workshop.M8.S08CompilerGate.Step3.OccupancyReport().Describe(map, input.Format);
        })
        .Expect("IMAX - przelot do Dolby", new Input(3, ["A1", "B1", "C10"]),
            "IMAX [duzy ekran, dzwiek Dolby], cena 40 zl, zajete: {1=2, 10=1}")
        .Expect("3D", new Input(2, ["D5"]), "3D [dzwiek Dolby], cena 32 zl, zajete: {5=1}")
        .Expect("2D, pusta sala", new Input(1, []), "2D [standard], cena 25 zl, zajete: {}");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDescribesOccupancyTheSameWay(string test) => Scene.Run(test);
}
