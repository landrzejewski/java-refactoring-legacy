using System.Collections.Frozen;
using System.Globalization;
using Training.Module4.Model;

namespace Training.Module4.Stage1;

public sealed class RentalQuoteService
{
    private const int LongRentalDays = 7;
    private const decimal InsuranceDailyRate = 8.00m;
    private const decimal DeliveryFee = 25.00m;
    private const decimal VatRate = 0.23m;
    private const decimal ZeroMoney = 0.00m;

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

    private readonly FrozenDictionary<EquipmentType, decimal> _dailyRates =
        new Dictionary<EquipmentType, decimal>
        {
            [EquipmentType.Drill] = 39.99m,
            [EquipmentType.Generator] = 120.00m
        }.ToFrozenDictionary();
    private readonly decimal _longRentalDiscountRate = 0.10m;

    public string CreateQuote(RentalRequest request)
    {
        var dailyRate = _dailyRates[request.EquipmentType];
        var baseRentalCost = Money(dailyRate * request.Days);
        var discount = CalculateDiscount(request, baseRentalCost);
        var insuranceCost = CalculateInsuranceCost(request);
        var deliveryCost = CalculateDeliveryCost(request);
        var netAmount = Money(baseRentalCost - discount + insuranceCost + deliveryCost);
        var vat = Money(netAmount * VatRate);
        var total = Money(netAmount + vat);

        return BuildDocument(
            request,
            baseRentalCost,
            discount,
            insuranceCost,
            deliveryCost,
            netAmount,
            vat,
            total);
    }

    private decimal CalculateDiscount(RentalRequest request, decimal baseRentalCost)
    {
        if (!QualifiesForLongRentalDiscount(request))
        {
            return ZeroMoney;
        }
        return Money(baseRentalCost * _longRentalDiscountRate);
    }

    private static bool QualifiesForLongRentalDiscount(RentalRequest request)
    {
        return request.Days >= LongRentalDays;
    }

    private static decimal CalculateInsuranceCost(RentalRequest request)
    {
        if (!request.Insurance)
        {
            return ZeroMoney;
        }
        return Money(InsuranceDailyRate * request.Days);
    }

    private static decimal CalculateDeliveryCost(RentalRequest request)
    {
        return request.Delivery ? DeliveryFee : ZeroMoney;
    }

    private static string BuildDocument(
        RentalRequest request,
        decimal baseRentalCost,
        decimal discount,
        decimal insuranceCost,
        decimal deliveryCost,
        decimal netAmount,
        decimal vat,
        decimal total)
    {
        return string.Format(
            CultureInfo.InvariantCulture,
            QuoteTemplate,
            request.CustomerName.Trim().ToUpperInvariant(),
            request.EquipmentType.ToString().ToUpperInvariant(),
            request.Days,
            baseRentalCost,
            discount,
            insuranceCost,
            deliveryCost,
            netAmount,
            vat,
            total);
    }

    private static decimal Money(decimal amount)
    {
        return Math.Round(amount, 2, MidpointRounding.AwayFromZero);
    }
}
