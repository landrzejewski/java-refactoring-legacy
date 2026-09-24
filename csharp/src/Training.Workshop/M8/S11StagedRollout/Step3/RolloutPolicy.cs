using System.Buffers.Binary;
using System.Security.Cryptography;
using System.Text;

namespace Training.Workshop.M8.S11StagedRollout.Step3;

/// <summary>
/// Krok 3: wyłącznik awaryjny (kill switch). Ma pierwszeństwo przed wszystkim - także przed
/// listą wyjątków - bo służy do natychmiastowego wycofania bez wydania nowej wersji.
/// </summary>
public sealed record RolloutPolicy(int Percent, IReadOnlySet<string> AllowList, bool KillSwitch)
{
    public int Percent { get; } = Percent is >= 0 and <= 100
        ? Percent
        : throw new ArgumentException("Procent spoza 0-100: " + Percent);

    public IReadOnlySet<string> AllowList { get; } = AllowList.ToHashSet();

    /// <summary>Dotychczasowe ustawienia produkcyjne: 0% ruchu + testerzy, wyłącznik nieaktywny.</summary>
    public static RolloutPolicy Current()
    {
        return new RolloutPolicy(0, new HashSet<string> { "anna@kino.pl", "jan@kino.pl" }, false);
    }

    public bool Allows(string email)
    {
        if (KillSwitch)
        {
            return false;
        }
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
