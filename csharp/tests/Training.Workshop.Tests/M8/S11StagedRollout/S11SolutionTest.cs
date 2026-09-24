using Training.Workshop.M8.S11StagedRollout.Step3;

namespace Training.Workshop.Tests.M8.S11StagedRollout;

/// <summary>Test polityki wdrożenia etapowego z kroku 3 (procent, wyjątki, wyłącznik).</summary>
public sealed class S11SolutionTest
{
    private static readonly IReadOnlyList<string> Customers =
        Enumerable.Range(0, 1000).Select(i => "klient" + i + "@kino.pl").ToList();

    [Fact]
    public void SameCustomerAlwaysGetsTheSamePath()
    {
        var policy = new RolloutPolicy(30, new HashSet<string>(), false);
        foreach (string email in Customers)
        {
            Assert.Equal(policy.Allows(email), new RolloutPolicy(30, new HashSet<string>(), false).Allows(email));
        }
        Assert.Equal(RolloutPolicy.Bucket("ola@kino.pl"), RolloutPolicy.Bucket("  Ola@Kino.PL "));
    }

    [Fact]
    public void PercentOfCustomersIsRoughlyRespected()
    {
        int included = Customers.Count(new RolloutPolicy(20, new HashSet<string>(), false).Allows);
        Assert.True(included > 150 && included < 250, "20% z 1000 klientów, było: " + included);
        Assert.Equal(0, Customers.Count(new RolloutPolicy(0, new HashSet<string>(), false).Allows));
        Assert.Equal(1000, Customers.Count(new RolloutPolicy(100, new HashSet<string>(), false).Allows));
    }

    [Fact]
    public void IncreasingPercentNeverRemovesAnyone()
    {
        var ten = new RolloutPolicy(10, new HashSet<string>(), false);
        var twenty = new RolloutPolicy(20, new HashSet<string>(), false);
        Assert.True(Customers.Where(ten.Allows).All(twenty.Allows));
    }

    [Fact]
    public void AllowListWorksAtZeroPercent()
    {
        var policy = new RolloutPolicy(0, new HashSet<string> { "anna@kino.pl" }, false);
        Assert.True(policy.Allows("Anna@Kino.pl"));
        Assert.False(policy.Allows("ola@kino.pl"));
    }

    [Fact]
    public void KillSwitchOverridesPercentAndAllowList()
    {
        var killed = new RolloutPolicy(100, new HashSet<string> { "anna@kino.pl" }, true);
        Assert.False(killed.Allows("anna@kino.pl"));
        Assert.DoesNotContain(Customers, killed.Allows);
    }

    [Fact]
    public void InvalidPercentIsRejected()
    {
        Assert.Throws<ArgumentException>(() => new RolloutPolicy(101, new HashSet<string>(), false));
        Assert.Throws<ArgumentException>(() => new RolloutPolicy(-1, new HashSet<string>(), false));
    }

    [Fact]
    public void StartComparesEmailLiterally()
    {
        var router = new Training.Workshop.M8.S11StagedRollout.Start.CheckoutRouter();
        // stary if porównuje e-mail dosłownie
        Assert.False(router.UseNewCheckout("Anna@kino.pl"));
    }
}
