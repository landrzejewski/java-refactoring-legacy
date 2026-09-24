using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Step3;

/// <summary>
/// Krok 3 (bez zmian): raport rozbieżności - zgodność, rozbieżność albo awaria kandydata.
/// Każde porównanie to typowane zdarzenie, a nie wpis w logu do parsowania.
/// </summary>
public sealed class VerificationReport
{
    public interface IVerification
    {
    }

    public sealed record Agreement(TicketQuery Query, Money Price) : IVerification;

    public sealed record Divergence(TicketQuery Query, Money Legacy, Money Candidate) : IVerification;

    public sealed record CandidateFailure(TicketQuery Query, Money Legacy, string Error) : IVerification;

    private readonly List<IVerification> _entries = [];

    public void Record(IVerification verification)
    {
        _entries.Add(verification);
    }

    public IReadOnlyList<IVerification> Entries()
    {
        return _entries.ToList();
    }

    /// <summary>Czytelne wiersze raportu - tylko to, co wymaga uwagi zespołu.</summary>
    public IReadOnlyList<string> Problems()
    {
        var lines = new List<string>();
        foreach (IVerification entry in _entries)
        {
            switch (entry)
            {
                case Agreement:
                    break;
                case Divergence d:
                    lines.Add("ROZBIEZNOSC " + d.Query + ": legacy " + d.Legacy + ", kandydat " + d.Candidate);
                    break;
                case CandidateFailure f:
                    lines.Add("BLAD KANDYDATA " + f.Query + ": " + f.Error);
                    break;
                default:
                    throw new InvalidOperationException("nieznany wpis raportu: " + entry);
            }
        }
        return lines;
    }
}
