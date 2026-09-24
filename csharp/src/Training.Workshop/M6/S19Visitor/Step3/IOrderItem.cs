namespace Training.Workshop.M6.S19Visitor.Step3;

/// <summary>
/// Krok 3: alternatywa C# - switch expression po typach zamiast Accept/Visitor. Uwaga: C# nie ma
/// odpowiednika sealed interface z Javy, więc kompilator nie sprawdza wyczerpania przypadków
/// (ostrzeżenie CS8509 wymusza ramię "_"). Pełną kontrolę kompilatora w C# daje tylko Visitor (krok 2).
/// </summary>
public interface IOrderItem
{
}
