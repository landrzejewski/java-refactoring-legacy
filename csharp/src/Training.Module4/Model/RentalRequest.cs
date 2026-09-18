namespace Training.Module4.Model;

public sealed record RentalRequest
{
    public RentalRequest(
        string customerName,
        EquipmentType equipmentType,
        int days,
        bool insurance,
        bool delivery)
    {
        ArgumentNullException.ThrowIfNull(customerName);
        if (!Enum.IsDefined(equipmentType))
        {
            throw new ArgumentOutOfRangeException(nameof(equipmentType));
        }
        if (string.IsNullOrWhiteSpace(customerName))
        {
            throw new ArgumentException("Customer name must not be blank", nameof(customerName));
        }
        if (days <= 0)
        {
            throw new ArgumentException("Rental days must be positive", nameof(days));
        }

        CustomerName = customerName;
        EquipmentType = equipmentType;
        Days = days;
        Insurance = insurance;
        Delivery = delivery;
    }

    public string CustomerName { get; }

    public EquipmentType EquipmentType { get; }

    public int Days { get; }

    public bool Insurance { get; }

    public bool Delivery { get; }
}
