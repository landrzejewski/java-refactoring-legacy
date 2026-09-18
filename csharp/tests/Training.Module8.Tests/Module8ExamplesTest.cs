namespace Training.Module8.Tests;

[Collection("Console")]
public sealed class Module8ExamplesTest
{
    [Fact]
    public void RunsOneDeterministicExampleForEveryTopic()
    {
        Assert.Equal(
            [
                "Stopniowa migracja: 53.97/Agreement",
                "Boy Scout: Successful: 1/2",
                "Code review: ready=true",
                "Dokumentowanie: # ADR-0042: Use Branch by Abstraction",
                "Narzędzia: compiled=true",
                "Zarządzanie ryzykiem: ADVANCE"
            ],
            Module8Examples.RunExamples());
    }

    [Fact]
    public void ReturnedResultsCannotBeModified()
    {
        IReadOnlyList<string> results = Module8Examples.RunExamples();

        Assert.Throws<NotSupportedException>(
            () => ((ICollection<string>)results).Add("unexpected"));
    }

    [Fact]
    public void MainPrintsEveryResultOnItsOwnLine()
    {
        TextWriter originalOutput = Console.Out;
        using var capturedOutput = new StringWriter();

        try
        {
            Console.SetOut(capturedOutput);
            Module8Examples.Main([]);
        }
        finally
        {
            Console.SetOut(originalOutput);
        }

        string expected = string.Join(
            Environment.NewLine, Module8Examples.RunExamples())
            + Environment.NewLine;
        Assert.Equal(expected, capturedOutput.ToString());
    }
}
