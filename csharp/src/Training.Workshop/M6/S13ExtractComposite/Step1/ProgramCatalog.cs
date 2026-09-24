namespace Training.Workshop.M6.S13ExtractComposite.Step1;

/// <summary>Krok 1: bez zmian - klient budujący programy wydarzeń.</summary>
public sealed class ProgramCatalog
{
    public IProgramItem Find(string code)
    {
        switch (code)
        {
            case "marathon":
            {
                var marathon = new Marathon("Diuna");
                marathon.Add(new Film("Diuna", 155));
                marathon.Add(new Film("Diuna: Czesc druga", 166));
                return marathon;
            }
            case "shorts":
                return Shorts();
            case "night":
            {
                var night = new Marathon("Noc kina");
                night.Add(Shorts());
                night.Add(new Film("Amator", 120));
                return night;
            }
            case "empty":
                return new Marathon("Pusty");
            default:
                throw new ArgumentException("unknown program: " + code);
        }
    }

    private static ShortsBlock Shorts()
    {
        var block = new ShortsBlock("Krotkie metraze");
        block.Add(new Film("Kot", 12));
        block.Add(new Film("Pies", 9));
        block.Add(new Film("Ryba", 15));
        return block;
    }
}
