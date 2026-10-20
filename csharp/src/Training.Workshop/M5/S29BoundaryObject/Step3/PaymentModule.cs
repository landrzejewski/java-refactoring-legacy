namespace Training.Workshop.M5.S29BoundaryObject.Step3;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: tu, i tylko tu, terminal dostaje adapter.</summary>
public static class PaymentModule
{
    public static PaymentService PaymentService(LegacyTerminalClient terminal)
    {
        return new PaymentService(new LegacyTerminalAdapter(terminal));
    }
}
