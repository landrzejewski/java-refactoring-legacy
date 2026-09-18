using System.Globalization;
using Training.Module4.Model;
using Training.Module4.Pricing;

namespace Training.Module4.Stage2;

public sealed class RentalQuoteService
{
    private const string QuoteTemplate =
        "RENTAL QUOTE\n"
        + "Customer: {0}\n"
        + "Equipment: {1}\n"
        + "Days: {2}\n"
        + "Base: {3:0.00}\n"
        + "Discount: {4:0.00}\n"
        + "Insurance: {5:0.00}\n"
        + "Delivery: {6:0.00}\n"
        + "Net: {7:0.00}\n"
        + "VAT: {8:0.00}\n"
        + "Total: {9:0.00}\n";

    private readonly RentalPricing _pricing;

    public RentalQuoteService()
        : this(RentalPricing.Standard())
    {
    }

    public RentalQuoteService(RentalPricing pricing)
    {
        ArgumentNullException.ThrowIfNull(pricing);
        _pricing = pricing;
    }

    public string CreateQuote(RentalRequest request)
    {
        ArgumentNullException.ThrowIfNull(request);

        var price = CalculatePrice(request);
        return BuildDocument(request, price);
    }

    private PriceBreakdown CalculatePrice(RentalRequest request)
    {
        return _pricing.Calculate(request);
    }

    private static string BuildDocument(RentalRequest request, PriceBreakdown price)
    {
        return string.Format(
            CultureInfo.InvariantCulture,
            QuoteTemplate,
            request.CustomerName.Trim().ToUpperInvariant(),
            request.EquipmentType.ToString().ToUpperInvariant(),
            request.Days,
            price.BaseRentalCost,
            price.Discount,
            price.InsuranceCost,
            price.DeliveryCost,
            price.NetAmount,
            price.Vat,
            price.Total);
    }
}
