namespace Training.Module7.Contract.Before;

public sealed class LegacyDeploymentCapacity
{
    private int _remaining;

    public LegacyDeploymentCapacity(int totalSlots)
    {
        _remaining = totalSlots;
    }

    public int Remaining => _remaining;

    public void Reserve(int slots)
    {
        _remaining -= slots;
    }

    public void Release(int slots)
    {
        _remaining += slots;
    }
}
