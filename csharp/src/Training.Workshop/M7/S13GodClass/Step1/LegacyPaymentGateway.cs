using System.Globalization;

namespace Training.Workshop.M7.S13GodClass.Step1;

/// <summary>Bramka płatności starego systemu. Karty kończące się na 0000 są odrzucane.</summary>
public static class LegacyPaymentGateway
{
    public static readonly List<string> Charges = [];

    public static bool Charge(string? cardNumber, double amount)
    {
        if (cardNumber == null || cardNumber.EndsWith("0000", StringComparison.Ordinal))
        {
            Charges.Add("DECLINED " + cardNumber + " " + amount.ToString("F2", CultureInfo.InvariantCulture));
            return false;
        }
        Charges.Add("CHARGED " + cardNumber + " " + amount.ToString("F2", CultureInfo.InvariantCulture));
        return true;
    }

    public static void Refund(string? cardNumber, double amount)
    {
        Charges.Add("REFUND " + cardNumber + " " + amount.ToString("F2", CultureInfo.InvariantCulture));
    }
}
