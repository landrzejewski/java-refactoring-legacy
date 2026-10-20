namespace Training.Module5.BoundaryObject.After;

/// <summary>
/// Port domenowy: domena prosi o <see cref="BuildReport"/>, nie o surowy ładunek.
/// </summary>
public interface IBuildReports
{
    BuildReport Find(string buildId);
}
