namespace Training.Module5.AntiCorruptionLayer.After;

/// <summary>
/// Port domenowy. Domena nie wie, że po drugiej stronie jest CMDB.
/// </summary>
public interface IEnvironmentDirectory
{
    IReadOnlyList<Environment> All();
}
