namespace Training.Workshop.Tests;

/// <summary>
/// Testy korzystające ze statycznego stanu starego systemu (LegacyDb, zegar CinemaManager)
/// albo z konsoli działają sekwencyjnie - odpowiednik @ResourceLock.
/// </summary>
[CollectionDefinition("Legacy", DisableParallelization = true)]
public sealed class LegacyCollection
{
}
