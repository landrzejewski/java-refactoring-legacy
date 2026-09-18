using Training.Module6.Polymorphism.After;
using Training.Module6.Polymorphism.Before;

namespace Training.Module6.Tests;

public sealed class PolymorphismEquivalenceTest
{
    [Fact]
    public void DispatchesEachStableVariantThroughItsOwnType()
    {
        string[] before =
        [
            LegacyDeploymentStep.Script("deploy.sh").Execute(),
            LegacyDeploymentStep.Approval("anna").Execute()
        ];
        DeploymentStep[] steps = [new ScriptStep("deploy.sh"), new ApprovalStep("anna")];

        Assert.Equal(before, steps.Select(step => step.Execute()));
    }

    [Fact]
    public void PreservesInvalidValueContract()
    {
        Assert.Equal(
            Assert.Throws<ArgumentException>(() => LegacyDeploymentStep.Script(" ")).Message,
            Assert.Throws<ArgumentException>(() => new ScriptStep(" ")).Message);
    }
}
