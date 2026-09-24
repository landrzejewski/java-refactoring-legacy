namespace Training.Workshop.Tests.M5.S11ConstructorCall;

/// <summary>Konstruktor wołający override: pułapka w start, naprawa lokalna (step1) i strukturalna (step2).</summary>
public sealed class S11SolutionTest
{
    [Fact]
    public void StartSubclassSeesUninitializedField()
    {
        // w C# null sklejony z tekstem to pusty napis (w Javie "null")
        Assert.Equal("Miejsce K12 (VIP: )", new Training.Workshop.M5.S11ConstructorCall.Start.VipTicket("K12", "Salonik A").Label());
    }

    [Fact]
    public void FieldInitializerRunsBeforeBaseConstructor()
    {
        Assert.Equal("Miejsce K12 (VIP: Salonik A)", new Training.Workshop.M5.S11ConstructorCall.Step1.VipTicket("K12", "Salonik A").Label());
    }

    [Fact]
    public void SolutionComputesLabelWhenObjectIsComplete()
    {
        Assert.Equal("Miejsce K12 (VIP: Salonik A)", new Training.Workshop.M5.S11ConstructorCall.Step2.VipTicket("K12", "Salonik A").Label());
    }
}
