using System.Reflection;

namespace Training.Workshop.Tests.M5.S02PullUpField;

/// <summary>Pull Up Field: jedno pole private readonly w bazie, a pola o innym znaczeniu zostają na miejscu.</summary>
public sealed class S02SolutionTest
{
    private const BindingFlags Declared = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.DeclaredOnly;

    [Fact]
    public void BeforePullUpAllSeatFieldsHaveSameNameTypeAndLifecycle()
    {
        foreach (var type in new[]
                 {
                     typeof(Training.Workshop.M5.S02PullUpField.Step2.StandardTicket),
                     typeof(Training.Workshop.M5.S02PullUpField.Step2.StudentTicket),
                     typeof(Training.Workshop.M5.S02PullUpField.Step2.VipTicket),
                 })
        {
            var seat = type.GetField("_seat", Declared)!;
            Assert.Equal(typeof(string), seat.FieldType);
            Assert.True(seat.IsPrivate && seat.IsInitOnly);
        }
        // przed krokiem 2: inny cykl życia (setter)
        Assert.False(typeof(Training.Workshop.M5.S02PullUpField.Step1.StandardTicket).GetField("_seat", Declared)!.IsInitOnly);
    }

    [Fact]
    public void SolutionDeclaresSeatOnceAsPrivateFinal()
    {
        var seat = typeof(Training.Workshop.M5.S02PullUpField.Step3.Ticket).GetField("_seat", Declared)!;
        Assert.True(seat.IsPrivate);
        Assert.True(seat.IsInitOnly);
        Assert.Null(typeof(Training.Workshop.M5.S02PullUpField.Step3.VipTicket).GetField("_seat", Declared));
        Assert.Equal(typeof(string), typeof(Training.Workshop.M5.S02PullUpField.Step3.StudentTicket).GetField("_studentId", Declared)!.FieldType);
    }

    [Fact]
    public void SolutionReadsSeatThroughBaseType()
    {
        Training.Workshop.M5.S02PullUpField.Step3.Ticket ticket = new Training.Workshop.M5.S02PullUpField.Step3.VipTicket("k12");
        Assert.Equal("K12", ticket.Seat);
    }
}
