using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Step1;

/// <summary>
/// Krok 1: Safe Composite - Push Members Down: Add() i Children tylko w Combo.
/// Wspólny typ ma wyłącznie operacje sensowne dla liścia i węzła.
/// </summary>
public abstract class MenuComponent
{
    public abstract string Name { get; }

    public abstract Money Price { get; }

    public virtual string Describe()
    {
        return Name + " " + Price;
    }
}
