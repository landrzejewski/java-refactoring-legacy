using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step1;

/// <summary>
/// Krok 1: Extract Interface - generyczna rola reguły cenowej. C# nie ma typów wieloznacznych
/// (Javowe PriceRule&lt;?&gt;), więc rola generyczna dostaje nieogólny interfejs bazowy, przez który
/// rejestr może trzymać różne reguły. Każda implementacja musi dostarczyć też Apply(ITicket) z rzutowaniem -
/// to ręczny odpowiednik Javowej metody bridge.
/// </summary>
public interface IPriceRule
{
    Money Apply(ITicket ticket);
}

/// <summary>Krok 1: generyczna rola reguły cenowej dla konkretnego typu biletu.</summary>
public interface IPriceRule<in T> : IPriceRule
    where T : ITicket
{
    Money Apply(T ticket);
}
