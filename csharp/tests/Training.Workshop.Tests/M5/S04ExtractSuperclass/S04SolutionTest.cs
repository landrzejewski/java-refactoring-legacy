using System.Globalization;
using Training.Workshop.M5.S04ExtractSuperclass.Step3;

namespace Training.Workshop.Tests.M5.S04ExtractSuperclass;

/// <summary>Extract Superclass zmienia model typów: BaseType, typ deklarujący akcesorów, wspólny kontrakt.</summary>
public sealed class S04SolutionTest
{
    private static readonly DateTime Evening = DateTime.Parse("2026-10-02T20:00", CultureInfo.InvariantCulture);

    [Fact]
    public void ClassesJoinTheSuperclassOneAtATime()
    {
        Assert.Equal(typeof(Training.Workshop.M5.S04ExtractSuperclass.Step1.HallBooking), typeof(Training.Workshop.M5.S04ExtractSuperclass.Step1.Screening).BaseType);
        Assert.Equal(typeof(object), typeof(Training.Workshop.M5.S04ExtractSuperclass.Step1.PrivateEvent).BaseType);
    }

    [Fact]
    public void SolutionSharesAbstractBookingWithDomainName()
    {
        Assert.Equal(typeof(HallBooking), typeof(Screening).BaseType);
        Assert.Equal(typeof(HallBooking), typeof(PrivateEvent).BaseType);
        Assert.True(typeof(HallBooking).IsAbstract);
        Assert.Equal(typeof(HallBooking), typeof(PrivateEvent).GetProperty("Hall")!.DeclaringType);
    }

    [Fact]
    public void OverlapsIsSymmetricAcrossBookingTypes()
    {
        HallBooking screening = new Screening("Diuna", "Sala 1", Evening.AddHours(-2), 166);
        HallBooking rental = PrivateEvent.Rental("Firma X", "Sala 1", Evening);
        Assert.True(screening.Overlaps(rental));
        Assert.True(rental.Overlaps(screening));
        Assert.False(rental.Overlaps(PrivateEvent.Rental("Firma Y", "Sala 1", Evening.AddHours(2))));
    }
}
