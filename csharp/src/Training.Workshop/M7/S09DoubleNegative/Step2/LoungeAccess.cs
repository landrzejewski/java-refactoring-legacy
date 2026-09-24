namespace Training.Workshop.M7.S09DoubleNegative.Step2;

/// <summary>Krok 2: migracja użyć po jednym - każde !Negatyw zamienione na pozytywny predykat.</summary>
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
