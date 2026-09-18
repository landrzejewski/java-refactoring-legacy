namespace Training.Module4.Tests;

/// <summary>
/// Tests that replace <see cref="Console.Out"/> or change <see cref="System.Globalization.CultureInfo.CurrentCulture"/>
/// run sequentially (JUnit: @ResourceLock / Locale.setDefault).
/// </summary>
[CollectionDefinition(Name, DisableParallelization = true)]
public sealed class GlobalStateCollection
{
    public const string Name = "Console";
}
