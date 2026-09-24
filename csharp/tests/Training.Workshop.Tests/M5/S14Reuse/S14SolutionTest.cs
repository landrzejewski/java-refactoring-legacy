using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S14Reuse;

/// <summary>Dziedziczenie dla reużycia łamie substytucję; kompozycja + rola przywraca uczciwy kontrakt.</summary>
public sealed class S14SolutionTest
{
    [Fact]
    public void RegularAccountRedeemsFreeTicket()
    {
        var account = new Training.Workshop.M5.S14Reuse.Step1.LoyaltyAccount("anna@kino.pl");
        account.Earn(Money.Of("1000.00"));
        Assert.True(account.RedeemFreeTicket());
        Assert.Equal(0, account.Points);
    }

    [Fact]
    public void BeforeSplitCorporateAccountBreaksBaseContract()
    {
        Training.Workshop.M5.S14Reuse.Step1.LoyaltyAccount account = new Training.Workshop.M5.S14Reuse.Step1.CorporateAccount("Kino-Tech");
        account.Earn(Money.Of("1000.00"));
        // pułapka: podtyp odrzuca operację, którą obiecuje nadtyp
        Assert.Throws<NotSupportedException>(() => account.RedeemFreeTicket());
    }

    [Fact]
    public void SolutionCorporateAccountIsNotALoyaltyAccount()
    {
        var corporate = typeof(Training.Workshop.M5.S14Reuse.Step2.CorporateAccount);
        Assert.False(typeof(Training.Workshop.M5.S14Reuse.Step2.LoyaltyAccount).IsAssignableFrom(corporate));
        Assert.True(typeof(Training.Workshop.M5.S14Reuse.Step2.IPointsHolder).IsAssignableFrom(corporate));
        Assert.DoesNotContain(corporate.GetMethods(), m => m.Name == "RedeemFreeTicket");
    }
}
