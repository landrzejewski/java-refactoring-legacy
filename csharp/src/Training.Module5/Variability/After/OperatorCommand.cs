namespace Training.Module5.Variability.After;

/// <summary>
/// Command: żądanie operatora jako obiekt, który można zakolejkować, zalogować i cofnąć.
/// Wykonanie i cofnięcie są obok siebie, a nie w dwóch osobnych <c>switch</c>-ach.
/// Zamknięty zbiór wariantów (Java: <c>sealed interface</c> z rekordami).
/// </summary>
public abstract record OperatorCommand
{
    private protected OperatorCommand()
    {
    }

    public abstract string Name { get; }

    public abstract void Execute(Cluster cluster);

    public abstract void Undo(Cluster cluster);

    public static OperatorCommand ByName(string? action) => action switch
    {
        "scale-up" => new ScaleUp(),
        "scale-down" => new ScaleDown(),
        "maintenance" => new ToggleMaintenance(),
        _ => throw new ArgumentException("unknown action: " + action)
    };

    public sealed record ScaleUp : OperatorCommand
    {
        public override string Name => "scale-up";

        public override void Execute(Cluster cluster) => cluster.AddInstance();

        public override void Undo(Cluster cluster) => cluster.RemoveInstance();
    }

    public sealed record ScaleDown : OperatorCommand
    {
        public override string Name => "scale-down";

        public override void Execute(Cluster cluster) => cluster.RemoveInstance();

        public override void Undo(Cluster cluster) => cluster.AddInstance();
    }

    public sealed record ToggleMaintenance : OperatorCommand
    {
        public override string Name => "maintenance";

        public override void Execute(Cluster cluster) => cluster.ToggleMaintenance();

        public override void Undo(Cluster cluster) => cluster.ToggleMaintenance();
    }
}
