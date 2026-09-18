namespace Training.Module1;

public static class DiscountPolicy
{
    public static int DiscountPercent(int orderValue, bool vip)
    {
        int discount = 0;

        if (orderValue >= 100)
        {
            discount += 10;
        }

        if (vip)
        {
            discount += 5;
        }

        return discount;
    }
}
