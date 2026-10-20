namespace Training.Workshop.M5.S29BoundaryObject.Start;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji): skąd serwis bierze terminal. Start: wprost.</summary>
public static class PaymentModule
{
    public static PaymentService PaymentService(LegacyTerminalClient terminal)
    {
        return new PaymentService(terminal);
    }
}
