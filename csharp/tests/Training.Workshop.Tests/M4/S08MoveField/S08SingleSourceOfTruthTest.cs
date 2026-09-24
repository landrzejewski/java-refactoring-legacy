namespace Training.Workshop.Tests.M4.S08MoveField;

/// <summary>Po co Move Field: w Start dwa seanse w TEJ SAMEJ sali mogą się nie zgadzać co do VIP.</summary>
public sealed class S08SingleSourceOfTruthTest
{
    [Fact]
    public void StartLetsScreeningsInOneHallDisagree()
    {
        var hall = new Training.Workshop.M4.S08MoveField.Start.Hall("Sala 1");
        var evening = new Training.Workshop.M4.S08MoveField.Start.Screening(hall, 3, 10);
        var morning = new Training.Workshop.M4.S08MoveField.Start.Screening(hall, 1, 8);
        // ten sam fotel raz jest VIP, raz nie
        Assert.NotEqual(evening.IsVip(9), morning.IsVip(9));
    }

    [Fact]
    public void AfterMoveFieldTheHallDecidesForEveryScreening()
    {
        var hall = new Training.Workshop.M4.S08MoveField.Step3.Hall("Sala 1", 10);
        var evening = new Training.Workshop.M4.S08MoveField.Step3.Screening(hall, 3);
        var morning = new Training.Workshop.M4.S08MoveField.Step3.Screening(hall, 1);
        Assert.Equal(evening.IsVip(9), morning.IsVip(9));
        Assert.Equal(evening.IsVip(10), morning.IsVip(10));
    }
}
