namespace Training.Workshop.M7.S09DoubleNegative.Start;

/// <summary>Start: podwójne zaprzeczenia - !customer.NotVip i !voucher.IsNotExpired(today).</summary>
public sealed class LoungeAccess
{
    public bool CanEnter(Customer customer, Voucher? voucher, DateOnly today)
    {
        if (!customer.NotVip)
        {
            return true;
        }
        if (voucher == null)
        {
            return false;
        }
        if (!voucher.IsNotExpired(today))
        {
            return false;
        }
        return true;
    }

    public string Badge(Customer customer)
    {
        return !customer.NotVip ? "VIP" : "STANDARD";
    }
}
