using System.Globalization;
using Microsoft.Extensions.Time.Testing;
using Training.Workshop.M6.S05ExtractFactory.Step3;

namespace Training.Workshop.Tests.M6.S05ExtractFactory;

/// <summary>Fabryka jest testowalna sama, a serwis dostaje ją jako zwykłą zależność.</summary>
public sealed class S05SolutionTest
{
    private readonly FakeTimeProvider _morning =
        new(DateTimeOffset.Parse("2026-10-03T09:50:00Z", CultureInfo.InvariantCulture));

    [Fact]
    public void FactoryAloneKnowsFeesAndExpiry()
    {
        var reservation = new ReservationFactory(_morning).Create("ONLINE", "anna@kino.pl", ["A1", "A2"]);
        Assert.Equal("R1", reservation.Id);
        Assert.Equal("4.00", reservation.Fee.ToString());
        Assert.Equal(new DateTime(2026, 10, 3, 10, 5, 0), reservation.ExpiresAt);
    }

    [Fact]
    public void TwoServicesSharingOneFactoryShareNumbering()
    {
        var factory = new ReservationFactory(_morning);
        new ReservationService(factory).Reserve("BOX_OFFICE", "jan@kino.pl", ["A1"]);
        var second = new ReservationService(factory).Reserve("BOX_OFFICE", "jan@kino.pl", ["A1"]);
        Assert.Equal("R2", second.Id);
    }
}
