using Training.Workshop.Shared;

namespace Training.Workshop.M7.S02MethodObject.Step1;

/// <summary>
/// Krok 1: obiekt metody - jedno wykonanie wyceny. Nie jest usługą współdzieloną:
/// powstaje dla każdego wywołania. Ciało skopiowane 1:1 z GroupPricing.Quote().
/// </summary>
internal sealed class GroupQuoteCalculation
{
    private readonly GroupOrder order;

    internal GroupQuoteCalculation(GroupOrder order)
    {
        this.order = order;
    }

    internal Quote Calculate()
    {
        Money basePrice;
        switch (order.Format)
        {
            case "IMAX": basePrice = Money.Of("40.00"); break;
            case "3D": basePrice = Money.Of("32.00"); break;
            default: basePrice = Money.Of("25.00"); break;
        }
        var morning = order.Start < new TimeOnly(12, 0);
        var glasses = order.Format == "3D" && !order.OwnGlasses;
        var tickets = Money.Zero;
        var count = 0;
        foreach (var type in order.TicketTypes)
        {
            var discount = type switch
            {
                "STUDENT" => 25,
                "SENIOR" => 30,
                "CHILD" => 40,
                _ => 0,
            };
            var price = basePrice.Minus(basePrice.Percent(discount));
            if (morning)
            {
                price = price.Minus(Money.Of("5.00"));
            }
            if (glasses)
            {
                price = price.Plus(Money.Of("3.00"));
            }
            tickets = tickets.Plus(price);
            count++;
        }
        tickets = tickets.Plus(Money.Of("10.00").Times(order.VipSeats));
        if (count >= 10)
        {
            tickets = tickets.Minus(tickets.Percent(10));
        }
        var fees = order.Online ? Money.Of("2.00").Times(count) : Money.Zero;
        var total = tickets.Plus(fees);
        var points = (int)tickets.Amount / 10;
        return new Quote(tickets, fees, total, points);
    }
}
