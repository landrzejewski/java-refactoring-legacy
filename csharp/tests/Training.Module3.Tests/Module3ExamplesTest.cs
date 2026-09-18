namespace Training.Module3.Tests;

public sealed class Module3ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        Assert.Null(Record.Exception(() => Module3Examples.Main([])));
    }
}
