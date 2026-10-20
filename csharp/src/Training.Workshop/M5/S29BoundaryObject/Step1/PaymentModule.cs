namespace Training.Workshop.M5.S29BoundaryObject.Step1;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji): skąd serwis bierze terminal. Krok 1: bez zmian.</summary>
public static class PaymentModule
{
    public static PaymentService PaymentService(LegacyTerminalClient terminal)
    {
        return new PaymentService(terminal);
    }
}
