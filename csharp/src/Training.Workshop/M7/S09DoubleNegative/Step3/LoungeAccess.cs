namespace Training.Workshop.M7.S09DoubleNegative.Step3;

/// <summary>Krok 3 (rozwiązanie): LoungeAccess jak w kroku 2 - czyta się bez odwracania w głowie.</summary>
public sealed class LoungeAccess
{
    public bool CanEnter(Customer customer, Voucher? voucher, DateOnly today)
    {
        if (customer.Vip)
        {
            return true;
        }
        if (voucher == null)
        {
            return false;
        }
        if (voucher.IsExpired(today))
        {
            return false;
        }
        return true;
    }

    public string Badge(Customer customer)
    {
        return customer.Vip ? "VIP" : "STANDARD";
    }
}
