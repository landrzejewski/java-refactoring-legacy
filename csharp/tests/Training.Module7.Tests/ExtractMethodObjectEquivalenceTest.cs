using Training.Module7.MethodObject;
using Training.Module7.MethodObject.After;
using Training.Module7.MethodObject.Before;

namespace Training.Module7.Tests;

public sealed class ExtractMethodObjectEquivalenceTest
{
    [Fact]
    public void PreservesRiskAssessmentsAcrossRepresentativeInputs()
    {
        var before = new LegacyDeploymentRiskCalculator();
        var after = new DeploymentRiskCalculator();
        DeploymentRiskInput[] inputs =
        [
            new(0, 0, 0, false),
            new(5, 0, 0, true),
            new(10, 2, 1, true),
            new(29, 0, 0, false),
            new(30, 0, 0, false),
            new(69, 0, 0, false),
            new(70, 0, 0, false),
            new(int.MaxValue, int.MaxValue, int.MaxValue, false)
        ];

        foreach (var input in inputs)
        {
            Assert.True(
                before.Calculate(input) == after.Calculate(input),
                $"Different assessment for {input}");
        }
    }

    [Fact]
    public void PreservesScoreClampingAndClassificationBoundaries()
    {
        var calculator = new DeploymentRiskCalculator();

        Assert.Equal(new RiskAssessment(0, RiskLevel.Low),
            calculator.Calculate(new DeploymentRiskInput(5, 0, 0, true)));
        Assert.Equal(new RiskAssessment(29, RiskLevel.Low),
            calculator.Calculate(new DeploymentRiskInput(29, 0, 0, false)));
        Assert.Equal(new RiskAssessment(30, RiskLevel.Medium),
            calculator.Calculate(new DeploymentRiskInput(30, 0, 0, false)));
        Assert.Equal(new RiskAssessment(69, RiskLevel.Medium),
            calculator.Calculate(new DeploymentRiskInput(69, 0, 0, false)));
        Assert.Equal(new RiskAssessment(70, RiskLevel.High),
            calculator.Calculate(new DeploymentRiskInput(70, 0, 0, false)));
        Assert.Equal(new RiskAssessment(100, RiskLevel.High),
            calculator.Calculate(new DeploymentRiskInput(
                int.MaxValue, int.MaxValue, int.MaxValue, false)));
    }

    [Fact]
    public void PreservesEachRiskWeightAndRollbackReduction()
    {
        var calculator = new DeploymentRiskCalculator();

        Assert.Equal(new RiskAssessment(20, RiskLevel.Low),
            calculator.Calculate(new DeploymentRiskInput(0, 1, 0, false)));
        Assert.Equal(new RiskAssessment(10, RiskLevel.Low),
            calculator.Calculate(new DeploymentRiskInput(0, 0, 1, false)));
        Assert.Equal(new RiskAssessment(35, RiskLevel.Medium),
            calculator.Calculate(new DeploymentRiskInput(50, 0, 0, true)));
    }

    [Fact]
    public void FacadeCreatesAnIndependentCalculationForEveryInvocation()
    {
        var calculator = new DeploymentRiskCalculator();
        var low = new DeploymentRiskInput(10, 0, 0, false);
        var high = new DeploymentRiskInput(80, 0, 0, false);

        Assert.Equal(new RiskAssessment(10, RiskLevel.Low), calculator.Calculate(low));
        Assert.Equal(new RiskAssessment(80, RiskLevel.High), calculator.Calculate(high));
        Assert.Equal(new RiskAssessment(10, RiskLevel.Low), calculator.Calculate(low));
    }

    [Fact]
    public void PreservesMissingInputFailure()
    {
        var before = new LegacyDeploymentRiskCalculator();
        var after = new DeploymentRiskCalculator();

        var beforeFailure = Assert.Throws<ArgumentNullException>(() => before.Calculate(null!));
        var afterFailure = Assert.Throws<ArgumentNullException>(() => after.Calculate(null!));

        Assert.Equal(beforeFailure.Message, afterFailure.Message);
    }

    [Fact]
    public void SharedInputModelRejectsNegativeCounts()
    {
        AssertInvalidInput(
            () => new DeploymentRiskInput(-1, 0, 0, false),
            "changedFiles must not be negative");
        AssertInvalidInput(
            () => new DeploymentRiskInput(0, -1, 0, false),
            "criticalServices must not be negative");
        AssertInvalidInput(
            () => new DeploymentRiskInput(0, 0, -1, false),
            "failedChecks must not be negative");
    }

    [Fact]
    public void AssessmentProtectsItsRangeAndRequiredLevel()
    {
        var belowRange = Assert.Throws<ArgumentException>(
            () => new RiskAssessment(-1, RiskLevel.Low));
        var aboveRange = Assert.Throws<ArgumentException>(
            () => new RiskAssessment(101, RiskLevel.High));
        // Enums are never null in C#; an undefined value plays the role of Java's null level.
        var missingLevel = Assert.Throws<ArgumentOutOfRangeException>(
            () => new RiskAssessment(10, (RiskLevel)42));

        Assert.Equal("score must be between 0 and 100", belowRange.Message);
        Assert.Equal("score must be between 0 and 100", aboveRange.Message);
        Assert.Equal("level", missingLevel.ParamName);
    }

    private static void AssertInvalidInput(Action constructor, string expectedMessage)
    {
        var failure = Assert.Throws<ArgumentException>(constructor);
        Assert.Equal(expectedMessage, failure.Message);
    }
}
