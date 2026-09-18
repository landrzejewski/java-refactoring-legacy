namespace Training.Module2;

public static class DeliveryFee
{
    public static int Fee(bool premium)
    {
        int fee = 100;

        if (premium)
        {
            fee = 0;
        }

        return fee;
    }
}
