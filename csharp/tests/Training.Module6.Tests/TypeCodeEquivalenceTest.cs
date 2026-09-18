using Training.Module6.TypeCode.After;
using Training.Module6.TypeCode.Before;

namespace Training.Module6.Tests;

public sealed class TypeCodeEquivalenceTest
{
    [Fact]
    public void PreservesMeaningOfEveryKnownCode()
    {
        foreach (var code in new[] { "TEST", "PROD", "DR", "prod" })
        {
            var before = new LegacyDeploymentRequest("rel-42", code);
            var after = new DeploymentRequest("rel-42", DeploymentZone.FromCode(code));

            Assert.Equal(before.RequiresApproval(), after.RequiresApproval());
            Assert.Equal(before.ZoneCode, after.Zone.Code);
        }
    }

    [Fact]
    public void CanonicalizesKnownInstancesAndRejectsUnknownCodes()
    {
        Assert.Same(DeploymentZone.Production, DeploymentZone.FromCode("prod"));
        Assert.Equal(
            Assert.Throws<ArgumentException>(
                () => new LegacyDeploymentRequest("rel-42", null)).Message,
            Assert.Throws<ArgumentException>(() => DeploymentZone.FromCode(null)).Message);
        Assert.Equal(
            Assert.Throws<ArgumentException>(
                () => new LegacyDeploymentRequest("rel-42", "unknown")).Message,
            Assert.Throws<ArgumentException>(() => DeploymentZone.FromCode("unknown")).Message);
    }
}
