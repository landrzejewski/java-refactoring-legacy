using System.Buffers.Binary;
using System.Security.Cryptography;
using System.Text;

namespace Training.Workshop.M8.S11StagedRollout.Step2;

/// <summary>
/// Krok 2: deterministyczny podział - bool zastąpiony procentem klientów. Klient trafia do
/// koszyka 0-99 ze stabilnego skrótu (SHA-256) znormalizowanego e-maila: ten sam klient zawsze
/// dostaje tę samą ścieżkę, a zwiększenie procentu nikogo nie wyrzuca z nowej ścieżki.
/// </summary>
public sealed record RolloutPolicy(int Percent, IReadOnlySet<string> AllowList)
{
    public int Percent { get; } = Percent is >= 0 and <= 100
        ? Percent
        : throw new ArgumentException("Procent spoza 0-100: " + Percent);

    public IReadOnlySet<string> AllowList { get; } = AllowList.ToHashSet();

    /// <summary>Dotychczasowe ustawienia produkcyjne: 0% ruchu + testerzy.</summary>
    public static RolloutPolicy Current()
    {
        return new RolloutPolicy(0, new HashSet<string> { "anna@kino.pl", "jan@kino.pl" });
    }

    public bool Allows(string email)
    {
        return AllowList.Contains(Normalize(email)) || Bucket(email) < Percent;
    }

    /// <summary>
    /// Koszyk 0-99 - stabilny między restartami i maszynami (nie string.GetHashCode, który w .NET
    /// jest losowany przy każdym starcie procesu, i nie losowanie).
    /// </summary>
    public static int Bucket(string email)
    {
        byte[] hash = SHA256.HashData(Encoding.UTF8.GetBytes(Normalize(email)));
        return (int)(BinaryPrimitives.ReadUInt32BigEndian(hash) % 100);
    }

    private static string Normalize(string email)
    {
        return email.Trim().ToLowerInvariant();
    }
}
