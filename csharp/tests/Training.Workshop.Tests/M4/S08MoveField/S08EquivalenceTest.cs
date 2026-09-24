using Training.Workshop.M4.S08MoveField;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S08MoveField;

/// <summary>
/// Test równoważności: ta sama wycena miejsca. Adaptery Start/Step1 podają próg do Screening,
/// a Step2/Step3 do Hall - to widoczna w teście zmiana konstruktorów po Move Field.
/// </summary>
public sealed class S08EquivalenceTest
{
    private static readonly Scene<SeatQuery, string> Scene = Support.Scene.Variants<SeatQuery, string>()
        .Variant("start", q => new Training.Workshop.M4.S08MoveField.Start.SeatPricer().Quote(
            new Training.Workshop.M4.S08MoveField.Start.Screening(
                new Training.Workshop.M4.S08MoveField.Start.Hall(q.Hall),
                q.Format, q.VipFromRow), q.Row))
        .Variant("step1", q => new Training.Workshop.M4.S08MoveField.Step1.SeatPricer().Quote(
            new Training.Workshop.M4.S08MoveField.Step1.Screening(
                new Training.Workshop.M4.S08MoveField.Step1.Hall(q.Hall),
                q.Format, q.VipFromRow), q.Row))
        .Variant("step2", q => new Training.Workshop.M4.S08MoveField.Step2.SeatPricer().Quote(
            new Training.Workshop.M4.S08MoveField.Step2.Screening(
                new Training.Workshop.M4.S08MoveField.Step2.Hall(q.Hall, q.VipFromRow),
                q.Format), q.Row))
        .Variant("step3", q => new Training.Workshop.M4.S08MoveField.Step3.SeatPricer().Quote(
            new Training.Workshop.M4.S08MoveField.Step3.Screening(
                new Training.Workshop.M4.S08MoveField.Step3.Hall(q.Hall, q.VipFromRow),
                q.Format), q.Row))
        .Expect("IMAX, rząd 10 przy progu 10 - VIP", new SeatQuery("Sala 1", 10, 3, 10),
            "Sala 1, rzad 10 (VIP): 50.00")
        .Expect("3D, rząd 9 przy progu 10", new SeatQuery("Sala 1", 10, 2, 9), "Sala 1, rzad 9: 32.00")
        .Expect("2D, mała sala z progiem 8 - rząd 9 to VIP", new SeatQuery("Sala 2", 8, 1, 9),
            "Sala 2, rzad 9 (VIP): 35.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepQuotesSeatsTheSameWay(string test) => Scene.Run(test);
}
