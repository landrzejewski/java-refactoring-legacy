namespace Training.Module5.Tests;

/// <summary>
/// Tests that replace <see cref="Console.Out"/> run sequentially (JUnit: @ResourceLock(SYSTEM_OUT)).
/// </summary>
[CollectionDefinition(Name, DisableParallelization = true)]
public sealed class GlobalStateCollection
{
    public const string Name = "Console";
}
