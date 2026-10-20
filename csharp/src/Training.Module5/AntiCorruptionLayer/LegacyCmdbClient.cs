namespace Training.Module5.AntiCorruptionLayer;

/// <summary>
/// Zewnętrzny system, którego modelu nie kontrolujemy.
/// </summary>
public delegate IReadOnlyList<LegacyCmdbRecord> LegacyCmdbClient();
