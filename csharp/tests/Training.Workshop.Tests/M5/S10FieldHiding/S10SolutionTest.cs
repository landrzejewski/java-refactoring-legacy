using System.Reflection;

namespace Training.Workshop.Tests.M5.S10FieldHiding;

/// <summary>Pola i metody static są wiązane statycznie - wynik zależy od typu referencji, nie od obiektu.</summary>
public sealed class S10SolutionTest
{
    private const BindingFlags Declared = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance
        | BindingFlags.Static | BindingFlags.DeclaredOnly;

    /*
     * Testy start używają refleksji zamiast student.Type / StudentTicket.Category(), żeby cały projekt
     * kompilował się także po naprawie start na żywo (wtedy te dwa testy po prostu zrobią się czerwone).
     */
    [Fact]
    public void StartHidesFieldSoOneObjectHasTwoSlots()
    {
        object student = new Training.Workshop.M5.S10FieldHiding.Start.StudentTicket();
        var inStudentTicket = typeof(Training.Workshop.M5.S10FieldHiding.Start.StudentTicket).GetField("Type", Declared)!;
        var inTicket = typeof(Training.Workshop.M5.S10FieldHiding.Start.Ticket).GetField("Type", Declared)!;
        // ((StudentTicket) t).Type
        Assert.Equal("STUDENT", inStudentTicket.GetValue(student));
        // pułapka: ((Ticket) t).Type - drugi slot w tym samym obiekcie
        Assert.Equal("NORMAL", inTicket.GetValue(student));
    }

    [Fact]
    public void StartStaticMethodIsHiddenNotOverridden()
    {
        var hiding = typeof(Training.Workshop.M5.S10FieldHiding.Start.StudentTicket).GetMethod("Category", Declared)!;
        Assert.True(hiding.IsStatic);
        Assert.Equal("BILET ULGOWY", hiding.Invoke(null, null));
        // pułapka: Label() widzi pole i static bazy
        Assert.Equal("BILET: NORMAL", new Training.Workshop.M5.S10FieldHiding.Start.StudentTicket().Label());
    }

    [Fact]
    public void Step1FixesFieldButStaticIsStillHidden()
    {
        Assert.Equal("BILET: STUDENT", new Training.Workshop.M5.S10FieldHiding.Step1.StudentTicket().Label());
        Assert.Null(typeof(Training.Workshop.M5.S10FieldHiding.Step1.StudentTicket).GetField("Type", Declared));
    }

    [Fact]
    public void SolutionDispatchesOnObjectRegardlessOfReferenceType()
    {
        Training.Workshop.M5.S10FieldHiding.Step2.Ticket ticket = new Training.Workshop.M5.S10FieldHiding.Step2.StudentTicket();
        Assert.Equal("BILET ULGOWY: STUDENT", ticket.Label());
        Assert.Equal("BILET ULGOWY", ticket.Category());
    }
}
