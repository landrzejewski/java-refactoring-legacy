namespace Training.Workshop.M6.S13ExtractComposite.Step2;

/// <summary>Krok 2: bez zmian - element programu kina (film albo kontener filmów).</summary>
public interface IProgramItem
{
    int Minutes { get; }

    string Describe();
}
