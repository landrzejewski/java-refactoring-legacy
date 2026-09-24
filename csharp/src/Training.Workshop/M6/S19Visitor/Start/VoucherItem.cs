using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Start;

/// <summary>Start: voucher - pomniejsza kwotę do zapłaty, bez VAT.</summary>
public sealed record VoucherItem(string Code, Money Value) : IOrderItem;
