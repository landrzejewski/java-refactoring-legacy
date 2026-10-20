namespace Training.Module5.AntiCorruptionLayer.After.Acl;

/// <summary>
/// Anti-Corruption Layer: adapter implementujący port domenowy + tłumacz modelu.
/// Typy CMDB nie wychodzą poza tę przestrzeń nazw.
/// </summary>
/// <remarks>
/// Facade a ACL: fasada upraszcza interfejs podsystemu, ale zostaje w tym samym modelu
/// (te same pojęcia, mniej wywołań). ACL tłumaczy między dwoma różnymi modelami, aby
/// obcy model nie "zepsuł" języka domeny; często używa fasady lub adaptera w środku.
/// </remarks>
public sealed class CmdbEnvironmentDirectory : IEnvironmentDirectory
{
    private readonly LegacyCmdbClient _cmdb;

    public CmdbEnvironmentDirectory(LegacyCmdbClient cmdb)
    {
        ArgumentNullException.ThrowIfNull(cmdb);
        _cmdb = cmdb;
    }

    public IReadOnlyList<Environment> All() =>
        _cmdb().Select(CmdbTranslator.ToEnvironment).ToList();
}
