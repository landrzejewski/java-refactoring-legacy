using Training.Workshop.Shared;

namespace Training.Workshop.M7.S02MethodObject.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Method wewnątrz obiektu metody. Pola niosą stan
/// między krokami, więc każdy blok stał się metodą bez parametrów, a Calculate()
/// czyta się jak spis treści. Kolejność obliczeń bez zmian.
/// </summary>
internal sealed class GroupQuoteCalculation
{
    private readonly GroupOrder order;
    private Money basePrice = Money.Zero;
    private bool morning;
    private bool glasses;
    private Money tickets = Money.Zero;
    private int count;

    internal GroupQuoteCalculation(GroupOrder order)
    {
        this.order = order;
    }

    internal Quote Calculate()
    {
        ReadConditions();
        AddTickets();
        AddVipSeats();
        ApplyGroupDiscount();
        var fees = BookingFees();
        return new Quote(tickets, fees, tickets.Plus(fees), LoyaltyPoints());
    }

    private void ReadConditions()
    {
        basePrice = order.Format switch
        {
            "IMAX" => Money.Of("40.00"),
            "3D" => Money.Of("32.00"),
            _ => Money.Of("25.00"),
        };
        morning = order.Start < new TimeOnly(12, 0);
        glasses = order.Format == "3D" && !order.OwnGlasses;
    }

    private void AddTickets()
    {
        foreach (var type in order.TicketTypes)
        {
            tickets = tickets.Plus(TicketPrice(type));
            count++;
        }
    }

    private Money TicketPrice(string type)
    {
        var price = basePrice.Minus(basePrice.Percent(DiscountPercent(type)));
        if (morning)
        {
            price = price.Minus(Money.Of("5.00"));
        }
        if (glasses)
        {
            price = price.Plus(Money.Of("3.00"));
        }
        return price;
    }

    private static int DiscountPercent(string type)
    {
        return type switch
        {
            "STUDENT" => 25,
            "SENIOR" => 30,
            "CHILD" => 40,
            _ => 0,
        };
    }

    private void AddVipSeats()
    {
        tickets = tickets.Plus(Money.Of("10.00").Times(order.VipSeats));
    }

    private void ApplyGroupDiscount()
    {
        if (count >= 10)
        {
            tickets = tickets.Minus(tickets.Percent(10));
        }
    }

    private Money BookingFees()
    {
        return order.Online ? Money.Of("2.00").Times(count) : Money.Zero;
    }

    private int LoyaltyPoints()
    {
        return (int)tickets.Amount / 10;
    }
}
