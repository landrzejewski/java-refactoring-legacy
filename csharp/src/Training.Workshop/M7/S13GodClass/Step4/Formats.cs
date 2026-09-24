using System.Globalization;

namespace Training.Workshop.M7.S13GodClass.Step4;

/// <summary>Krok 2: format kwot ("114.00") potrzebny i CinemaManager, i NotificationService - jeden właściciel.</summary>
internal static class Formats
{
    internal static string Amount(double value)
    {
        return value.ToString("F2", CultureInfo.InvariantCulture);
    }
}
