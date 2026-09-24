namespace Training.Workshop.M4.S10EncapsulateField.Step1;

/// <summary>
/// Krok 1: Encapsulate Field - pole prywatne, trywialna właściwość get/set na TYM SAMYM polu.
/// Zachowanie bez zmian (setter przyjmuje wszystko), ale każdy zapis przechodzi teraz przez nas.
/// </summary>
public sealed class Reservation
{
    private string _status = "NEW";

    public string Status
    {
        get => _status;
        set => _status = value;
    }
}
