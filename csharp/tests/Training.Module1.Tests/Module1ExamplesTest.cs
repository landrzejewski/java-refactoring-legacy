namespace Training.Module1.Tests;

public sealed class Module1ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        Assert.Null(Record.Exception(() => Module1Examples.Main([])));
    }
}
