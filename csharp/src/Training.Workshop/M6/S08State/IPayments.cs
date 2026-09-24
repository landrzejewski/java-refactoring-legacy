namespace Training.Workshop.M6.S08State;

/// <summary>Port bramki płatności - efekt zewnętrzny, który może się nie udać.</summary>
public interface IPayments
{
    void Charge(string reservationId);
}
