namespace Training.Module7.GodClass.After;

public interface IReleaseRepository
{
    bool ExistsById(string releaseId);

    void Save(PublishedRelease release);

    IReadOnlyList<PublishedRelease> FindAll();
}
