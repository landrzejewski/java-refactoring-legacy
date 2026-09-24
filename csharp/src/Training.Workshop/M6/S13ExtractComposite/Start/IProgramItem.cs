namespace Training.Workshop.M6.S13ExtractComposite.Start;

/// <summary>Start: element programu kina (film albo kontener filmów).</summary>
public interface IProgramItem
{
    int Minutes { get; }

    string Describe();
}
