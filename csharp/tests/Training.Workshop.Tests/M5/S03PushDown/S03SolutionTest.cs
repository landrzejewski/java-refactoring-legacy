using System.Reflection;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S03PushDown;

/// <summary>Push Down zawęża kontrakt bazy - i łamie stare assembly, które wołały metodę przez nadklasę.</summary>
public sealed class S03SolutionTest
{
    [Fact]
    public void BeforePushDownStudentTicketBreaksBaseContract()
    {
        Training.Workshop.M5.S03PushDown.Step2.Ticket ticket = new Training.Workshop.M5.S03PushDown.Step2.StudentTicket(Money.Of("25.00"));
        Assert.Throws<NotSupportedException>(ticket.UpgradeToVip);
    }

    [Fact]
    public void BeforePushDownSubclassFindsMethodInSuperclass()
    {
        Assert.Equal(typeof(Training.Workshop.M5.S03PushDown.Step2.Ticket),
            typeof(Training.Workshop.M5.S03PushDown.Step2.StandardTicket).GetMethod("UpgradeToVip")!.DeclaringType);
    }

    [Fact]
    public void AfterPushDownBaseTypeNoLongerHasTheMethod()
    {
        Assert.Null(typeof(Training.Workshop.M5.S03PushDown.Step3.Ticket).GetMethod("UpgradeToVip"));
        Assert.Equal(typeof(Training.Workshop.M5.S03PushDown.Step3.StandardTicket),
            typeof(Training.Workshop.M5.S03PushDown.Step3.StandardTicket).GetMethod("UpgradeToVip")!.DeclaringType);
        Assert.Null(typeof(Training.Workshop.M5.S03PushDown.Step3.Ticket)
            .GetField("_vipUpgraded", BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.DeclaredOnly));
    }
}
