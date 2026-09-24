using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): reguła jawnie deklaruje obsługiwany typ - <c>TicketType</c> zastępuje
/// zgadywanie refleksją (Replace Reflection with Explicit Contract). Nieogólny kontrakt dla rejestru.
/// </summary>
public interface IPriceRule
{
    Type TicketType { get; }

    Money Apply(ITicket ticket);
}

/// <summary>
/// Krok 2: generyczna rola. "Most" (typ i rzutowanie) jest napisany raz, jako jawna domyślna implementacja
/// nieogólnego kontraktu - reguły go nie deklarują, a refleksja po klasach reguł go nie widzi.
/// </summary>
public interface IPriceRule<in T> : IPriceRule
    where T : ITicket
{
    Type IPriceRule.TicketType => typeof(T);

    Money Apply(T ticket);

    Money IPriceRule.Apply(ITicket ticket) => Apply((T)ticket);
}
