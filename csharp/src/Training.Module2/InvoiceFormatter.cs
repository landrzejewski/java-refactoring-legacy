using System.Globalization;
using System.Text;

namespace Training.Module2;

public sealed class InvoiceFormatter
{
    private const decimal TaxRate = 0.23m;

    public string Format(string? customer, IReadOnlyList<InvoiceLine> lines)
    {
        decimal subtotal = CalculateSubtotal(lines);
        decimal tax = Money(subtotal * TaxRate);
        decimal total = Money(subtotal + tax);

        StringBuilder result = new StringBuilder("INVOICE\n")
            .Append("Customer: ")
            .Append(DisplayedCustomer(customer))
            .Append('\n');

        AppendLines(result, lines);

        return result
            .Append("Subtotal: ").Append(Text(Money(subtotal))).Append('\n')
            .Append("Tax: ").Append(Text(tax)).Append('\n')
            .Append("Total: ").Append(Text(total)).Append('\n')
            .ToString();
    }

    private static string DisplayedCustomer(string? customer) =>
        customer == null
            ? "UNKNOWN"
            : customer.Trim().ToUpperInvariant();

    private static decimal CalculateSubtotal(IReadOnlyList<InvoiceLine> lines) =>
        lines.Select(LineTotal).Aggregate(0m, (sum, value) => sum + value);

    private static void AppendLines(
        StringBuilder result,
        IReadOnlyList<InvoiceLine> lines)
    {
        foreach (InvoiceLine line in lines)
        {
            result.Append(line.Sku)
                .Append(" x ")
                .Append(line.Quantity)
                .Append(" = ")
                .Append(Text(Money(LineTotal(line))))
                .Append('\n');
        }
    }

    private static decimal LineTotal(InvoiceLine line) =>
        line.UnitPrice * line.Quantity;

    private static decimal Money(decimal value) =>
        Math.Round(value, 2, MidpointRounding.AwayFromZero);

    private static string Text(decimal money) =>
        money.ToString("0.00", CultureInfo.InvariantCulture);
}
