using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods;

/// <summary>Stabilny kontrakt sceny: bilet z ceną bazową formatu.</summary>
public interface ITicket
{
    Money BasePrice { get; }
}
