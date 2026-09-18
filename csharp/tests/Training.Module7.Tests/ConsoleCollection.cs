namespace Training.Module7.Tests;

// Tests that redirect Console output or change the current culture must not run in parallel.
[CollectionDefinition("Console", DisableParallelization = true)]
public sealed class ConsoleCollection;
