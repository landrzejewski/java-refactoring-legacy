using Training.Module4.Model;

namespace Training.Module4.Tests.Model;

public sealed class RentalRequestTest
{
    [Fact]
    public void RejectsInvalidInput()
    {
        Assert.Multiple(
            () => Assert.Throws<ArgumentException>(
                () => new RentalRequest(" ", EquipmentType.Drill, 1, false, false)),
            () => Assert.Throws<ArgumentException>(
                () => new RentalRequest("Acme", EquipmentType.Drill, 0, false, false)));
    }
}
