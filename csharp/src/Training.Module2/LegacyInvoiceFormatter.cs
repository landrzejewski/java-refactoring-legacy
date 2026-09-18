using System.Globalization;
using System.Text;

namespace Training.Module2;

public sealed class LegacyInvoiceFormatter
{
    public string Format(string? customer, IReadOnlyList<InvoiceLine> lines)
    {
        decimal subtotal = 0m;
        StringBuilder result = new("INVOICE\n");

        string displayedCustomer = customer == null
            ? "UNKNOWN"
            : customer.Trim().ToUpperInvariant();
        result.Append("Customer: ").Append(displayedCustomer).Append('\n');

        foreach (InvoiceLine line in lines)
        {
            decimal lineTotal = line.UnitPrice * line.Quantity;
            subtotal = subtotal + lineTotal;

            result.Append(line.Sku)
                .Append(" x ")
                .Append(line.Quantity)
                .Append(" = ")
                .Append(Math.Round(lineTotal, 2, MidpointRounding.AwayFromZero)
                    .ToString("0.00", CultureInfo.InvariantCulture))
                .Append('\n');
        }

        decimal tax = Math.Round(
            subtotal * 0.23m,
            2,
            MidpointRounding.AwayFromZero);
        decimal total = Math.Round(
            subtotal + tax,
            2,
            MidpointRounding.AwayFromZero);

        result.Append("Subtotal: ")
            .Append(Math.Round(subtotal, 2, MidpointRounding.AwayFromZero)
                .ToString("0.00", CultureInfo.InvariantCulture))
            .Append('\n');
        result.Append("Tax: ").Append(tax.ToString("0.00", CultureInfo.InvariantCulture)).Append('\n');
        result.Append("Total: ").Append(total.ToString("0.00", CultureInfo.InvariantCulture)).Append('\n');

        return result.ToString();
    }
}
