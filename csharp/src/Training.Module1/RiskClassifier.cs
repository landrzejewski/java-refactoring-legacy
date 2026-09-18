namespace Training.Module1;

public static class RiskClassifier
{
    public static int RiskLevel(OrderSummary order)
    {
        int score = 0;

        if (order.Total > 1000.00m)
        {
            score++;
        }

        if (order.International)
        {
            score++;
        }

        foreach (Item item in order.Items)
        {
            if (item.Fragile)
            {
                score++;
            }
        }

        return score;
    }
}

public sealed record OrderSummary(
    decimal Total,
    bool International,
    IReadOnlyList<Item> Items);

public sealed record Item(bool Fragile);
