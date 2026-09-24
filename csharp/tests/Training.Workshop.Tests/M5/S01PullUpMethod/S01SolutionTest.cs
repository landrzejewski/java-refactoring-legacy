using System.Reflection;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S01PullUpMethod;

/// <summary>Po Pull Up zmienia się typ deklarujący metody - to widzi refleksja i każde stare assembly.</summary>
public sealed class S01SolutionTest
{
    [Fact]
    public void BeforeLastStepEverySubclassDeclaresLabel()
    {
        Assert.False(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step2.Ticket), "Label"));
        Assert.True(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step2.StandardTicket), "Label"));
        Assert.True(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step2.StudentTicket), "Label"));
        Assert.True(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step2.VipTicket), "Label"));
    }

    [Fact]
    public void SolutionDeclaresLabelOnceAsFinalAndPriceAsAbstract()
    {
        var type = typeof(Training.Workshop.M5.S01PullUpMethod.Step3.Ticket);
        var label = type.GetMethod("Label", BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly)!;
        var price = type.GetMethod("Price", BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly)!;
        Assert.False(label.IsVirtual);
        Assert.True(price.IsAbstract);
        Assert.False(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step3.StandardTicket), "Label"));
        Assert.False(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step3.StudentTicket), "Label"));
        Assert.False(Declares(typeof(Training.Workshop.M5.S01PullUpMethod.Step3.VipTicket), "Label"));
    }

    [Fact]
    public void SolutionIsUsableThroughBaseType()
    {
        Training.Workshop.M5.S01PullUpMethod.Step3.Ticket ticket = new Training.Workshop.M5.S01PullUpMethod.Step3.StudentTicket("Amator", Money.Of("25.00"));
        Assert.Equal("Amator: 18.75", ticket.Label());
    }

    private static bool Declares(Type type, string name)
    {
        const BindingFlags all = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance
            | BindingFlags.Static | BindingFlags.DeclaredOnly;
        return type.GetMethods(all).Any(m => m.Name == name);
    }
}
