namespace Training.Patterns.Tests;

/// <summary>Runs every example registered in <see cref="Program"/> with redirected console and empty stdin.</summary>
[Collection(ConsoleCollection.Name)]
public sealed class ExamplesSmokeTest
{
    public static TheoryData<string> ExampleKeys()
    {
        var data = new TheoryData<string>();
        foreach (var example in Program.Examples)
        {
            data.Add(example.Key);
        }
        return data;
    }

    [Theory]
    [MemberData(nameof(ExampleKeys))]
    public void ExampleRunsWithoutException(string key)
    {
        var (found, output) = Capture(() => Program.TryRun(key));

        Assert.True(found);
        Assert.False(string.IsNullOrWhiteSpace(output));
    }

    [Fact]
    public void UnknownKeyIsNotFound()
    {
        Assert.False(Program.TryRun("no-such-example"));
    }

    [Fact]
    public void DecoratorUsesDefaultTextWhenStdinIsEmpty()
    {
        var (_, output) = Capture(() => Program.TryRun("decorator"));

        Assert.Contains("INFO: hello_decorator_pattern", output);
    }

    [Fact]
    public void DecoratorTransformsTheLineReadFromStdin()
    {
        var (_, output) = Capture(() => Program.TryRun("decorator"), "Hello World Foo\n");

        Assert.Contains("INFO: hello_world_foo", output);
    }

    [Fact]
    public void MainWithoutArgumentsListsEveryExampleKey()
    {
        var (exitCode, output) = Capture(() => Program.Main([]));

        Assert.Equal(0, exitCode);
        Assert.All(Program.Examples, example => Assert.Contains(example.Key, output));
    }

    [Fact]
    public void MainRunsAllExamples()
    {
        var (exitCode, output) = Capture(() => Program.Main(["all"]));

        Assert.Equal(0, exitCode);
        Assert.All(Program.Examples, example => Assert.Contains($"=== {example.Key} ===", output));
    }

    private static (T Result, string Output) Capture<T>(Func<T> action, string input = "")
    {
        var originalOut = Console.Out;
        var originalError = Console.Error;
        var originalIn = Console.In;
        using var output = new StringWriter();
        try
        {
            Console.SetOut(output);
            Console.SetError(output);
            Console.SetIn(new StringReader(input));
            var result = action();
            return (result, output.ToString());
        }
        finally
        {
            Console.SetOut(originalOut);
            Console.SetError(originalError);
            Console.SetIn(originalIn);
        }
    }
}
