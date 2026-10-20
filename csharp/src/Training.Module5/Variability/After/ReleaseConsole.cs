namespace Training.Module5.Variability.After;

/// <summary>
/// Konsola wydania złożona z trzech mechanizmów, po jednym na każdą oś zmienności:
/// Strategy (taryfa wybrana raz przez klienta), Command (żądania operatora jako obiekty)
/// i State (cykl życia, który sam zmienia zachowanie).
/// </summary>
/// <remarks>
/// Command, Strategy czy State? Te same <c>switch</c>-e z <c>LegacyReleaseConsole</c>
/// rozdzielone według tego, kto wybiera wariant i kiedy (Java: <c>package-info</c>):
/// <list type="bullet">
/// <item><b>Strategy</b> (<see cref="SupportCostPolicy"/>) - wariant wybiera klient raz
/// (np. przy tworzeniu obiektu) i nie zmienia go w trakcie życia obiektu; rodzina
/// algorytmów jest stabilna i wymienna.</item>
/// <item><b>Command</b> (<see cref="OperatorCommand"/>) - żądanie staje się obiektem, bo
/// trzeba je kolejkować, logować i cofać; wariant wybiera nadawca żądania przy każdym
/// wywołaniu.</item>
/// <item><b>State</b> (<see cref="ReleaseLifecycle"/>) - nikt z zewnątrz nie wybiera
/// wariantu; obiekt sam zmienia zachowanie, przechodząc między stanami.</item>
/// </list>
/// </remarks>
public sealed class ReleaseConsole
{
    private readonly SupportCostPolicy _costPolicy;
    private readonly CommandConsole _commands = new();
    private readonly ReleaseLifecycle _lifecycle = new();

    public ReleaseConsole(SupportCostPolicy costPolicy)
    {
        ArgumentNullException.ThrowIfNull(costPolicy);
        _costPolicy = costPolicy;
    }

    public static ReleaseConsole ForTier(CustomerTier tier) =>
        new(SupportCostPolicies.ForTier(tier));

    public long SupportCost(int hours)
    {
        if (hours < 0)
        {
            throw new ArgumentException("hours must not be negative");
        }
        return _costPolicy(hours);
    }

    public void Submit(string? action) => _commands.Submit(OperatorCommand.ByName(action));

    public void RunQueued() => _commands.RunQueued();

    public void UndoLast() => _commands.UndoLast();

    public IReadOnlyList<string> Log => _commands.Log;

    public ReleaseStatus Status => _lifecycle.Status;

    public void Advance() => _lifecycle.Advance();

    public void Rollback() => _lifecycle.Rollback();

    public string Banner => _lifecycle.Banner;
}
