namespace Training.Module1;

public static class SalesCalculations
{
    public static decimal InvoiceLineTotal(
        decimal unitPrice,
        int quantity,
        bool vip)
    {
        decimal total = unitPrice * quantity;

        if (vip)
        {
            total = total * 0.90m;
        }

        return total;
    }

    public static decimal QuoteLineTotal(
        decimal unitPrice,
        int quantity,
        bool vip)
    {
        decimal total = unitPrice * quantity;

        if (vip)
        {
            total = total * 0.90m;
        }

        return total;
    }
}
