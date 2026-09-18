namespace Training.Patterns.Tests;

/// <summary>Tests that redirect <see cref="Console"/> (Java: System.setOut + synchronized(System.class)) never run in parallel.</summary>
[CollectionDefinition(Name, DisableParallelization = true)]
public sealed class ConsoleCollection
{
    public const string Name = "Console";
}
