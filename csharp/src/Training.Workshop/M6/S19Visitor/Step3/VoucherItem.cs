using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step3;

/// <summary>Krok 3: voucher - czyste dane, bez Accept.</summary>
public sealed record VoucherItem(string Code, Money Value) : IOrderItem;
