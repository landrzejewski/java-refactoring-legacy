namespace Training.Workshop.M7.S08DesignByContract.Step1;

/// <summary>
/// Jawne kontrole kontraktu - działają zawsze, w przeciwieństwie do Debug.Assert
/// (działa tylko w buildzie Debug, odpowiednik assert wymagającego -ea w Javie).
/// Require: obowiązek klienta (ArgumentException),
/// Ensure: gwarancja operacji (InvalidOperationException).
/// </summary>
internal static class Contracts
{
    internal static void Require(bool condition, string message)
    {
        if (!condition)
        {
            throw new ArgumentException(message);
        }
    }

    internal static void Ensure(bool condition, string message)
    {
        if (!condition)
        {
            throw new InvalidOperationException(message);
        }
    }
}
