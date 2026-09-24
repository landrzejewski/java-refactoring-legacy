using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S09DoubleNegative;

/// <summary>
/// Test równoważności. Wejście opisujemy "po staremu" (NotVip), a adapter kroku 3
/// musi odwrócić wartość przy budowie rekordu - to jest dokładnie migracja granicy.
/// </summary>
public sealed class S09EquivalenceTest
{
    public sealed record Visit(bool NotVip, DateOnly? VoucherValidUntil);

    private static readonly DateOnly Today = new(2026, 3, 10);

    private static readonly Scene<Visit, string> Scene = Support.Scene.Variants<Visit, string>()
        .Variant("start", v =>
        {
            var customer = new Training.Workshop.M7.S09DoubleNegative.Start.Customer("anna@kino.pl", v.NotVip);
            var voucher = v.VoucherValidUntil == null ? null
                : new Training.Workshop.M7.S09DoubleNegative.Start.Voucher("LOUNGE", v.VoucherValidUntil.Value);
            var lounge = new Training.Workshop.M7.S09DoubleNegative.Start.LoungeAccess();
            return Show(lounge.CanEnter(customer, voucher, Today)) + " " + lounge.Badge(customer);
        })
        .Variant("step1", v =>
        {
            var customer = new Training.Workshop.M7.S09DoubleNegative.Step1.Customer("anna@kino.pl", v.NotVip);
            var voucher = v.VoucherValidUntil == null ? null
                : new Training.Workshop.M7.S09DoubleNegative.Step1.Voucher("LOUNGE", v.VoucherValidUntil.Value);
            var lounge = new Training.Workshop.M7.S09DoubleNegative.Step1.LoungeAccess();
            return Show(lounge.CanEnter(customer, voucher, Today)) + " " + lounge.Badge(customer);
        })
        .Variant("step2", v =>
        {
            var customer = new Training.Workshop.M7.S09DoubleNegative.Step2.Customer("anna@kino.pl", v.NotVip);
            var voucher = v.VoucherValidUntil == null ? null
                : new Training.Workshop.M7.S09DoubleNegative.Step2.Voucher("LOUNGE", v.VoucherValidUntil.Value);
            var lounge = new Training.Workshop.M7.S09DoubleNegative.Step2.LoungeAccess();
            return Show(lounge.CanEnter(customer, voucher, Today)) + " " + lounge.Badge(customer);
        })
        .Variant("step3", v =>
        {
            var customer = new Training.Workshop.M7.S09DoubleNegative.Step3.Customer("anna@kino.pl", !v.NotVip);
            var voucher = v.VoucherValidUntil == null ? null
                : new Training.Workshop.M7.S09DoubleNegative.Step3.Voucher("LOUNGE", v.VoucherValidUntil.Value);
            var lounge = new Training.Workshop.M7.S09DoubleNegative.Step3.LoungeAccess();
            return Show(lounge.CanEnter(customer, voucher, Today)) + " " + lounge.Badge(customer);
        })
        .Expect("VIP bez vouchera", new Visit(false, null), "true VIP")
        .Expect("VIP z przeterminowanym voucherem", new Visit(false, Today.AddDays(-1)), "true VIP")
        .Expect("zwykly klient bez vouchera", new Visit(true, null), "false STANDARD")
        .Expect("voucher wazny do dzis wlacznie", new Visit(true, Today), "true STANDARD")
        .Expect("voucher wygasl wczoraj", new Visit(true, Today.AddDays(-1)), "false STANDARD");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepGrantsTheSameAccess(string test) => Scene.Run(test);

    private static string Show(bool value) => value ? "true" : "false";
}
