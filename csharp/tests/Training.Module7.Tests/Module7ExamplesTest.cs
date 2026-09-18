namespace Training.Module7.Tests;

[Collection("Console")]
public sealed class Module7ExamplesTest
{
    [Fact]
    public void RunsOneDeterministicExampleForEveryTopic()
    {
        Assert.Equal(
            [
                "Break Dependencies: ALLOWED",
                "Extract Method Object: 45/MEDIUM",
                "Break Responsibilities: deployments=3;failures=1;"
                    + "avgLeadTimeMinutes=18",
                "Remove Duplication: image:42",
                "Break Method: 10|api|sha-api,20|worker|sha-worker",
                "Introduce Parameter Object: 165s",
                "Remove Arrowhead: ELIGIBLE",
                "Design by Contract: remaining=7",
                "Remove Double Negative: true",
                "Remove God Class: rel-42/1/1/1",
                "Remove Boolean Parameter: deployed:rel-42",
                "Remove Middle Man: dep-42 -> RUNNING",
                "Return ASAP: api.jar"
            ],
            Module7Examples.RunExamples());
    }

    [Fact]
    public void ReturnedResultsCannotBeModified()
    {
        var results = Module7Examples.RunExamples();

        var asCollection = Assert.IsAssignableFrom<ICollection<string>>(results);
        Assert.Throws<NotSupportedException>(() => asCollection.Add("unexpected"));
    }

    [Fact]
    public void MainPrintsEveryResultOnItsOwnLine()
    {
        var originalOutput = Console.Out;
        using var capturedOutput = new StringWriter();

        try
        {
            Console.SetOut(capturedOutput);
            var failure = Record.Exception(() => Module7Examples.Main([]));
            Assert.Null(failure);
        }
        finally
        {
            Console.SetOut(originalOutput);
        }

        var expected = string.Join(Environment.NewLine, Module7Examples.RunExamples())
            + Environment.NewLine;
        Assert.Equal(expected, capturedOutput.ToString());
    }
}
