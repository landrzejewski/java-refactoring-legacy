using System.Globalization;
using System.Text.RegularExpressions;

namespace Training.Workshop.M6.S14Adapter;

/// <summary>
/// "Biblioteka" starej bramki (nie zmieniamy jej): XML jako tekst, kwota w groszach.
/// Deterministyczna symulacja: powyżej 500.00 odmowa z kodem 51.
/// </summary>
public class XmlPayGateway
{
    private static readonly Regex ChargePattern = new("^<charge ref='([^']+)' amount='(\\d+)'/>$");

    public virtual string Submit(string xml)
    {
        var match = ChargePattern.Match(xml);
        if (!match.Success)
        {
            return "<result status='ERROR' code='XML'/>";
        }
        if (long.Parse(match.Groups[2].Value, CultureInfo.InvariantCulture) > 50_000)
        {
            return "<result status='DECLINED' code='51'/>";
        }
        return "<result status='OK' id='X-" + match.Groups[1].Value + "'/>";
    }
}
