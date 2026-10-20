namespace Training.Workshop.M5.S19Visitor.Step2;

/// <summary>Krok 2: element przyjmuje odwiedzającego (double dispatch).</summary>
public interface IOrderItem
{
    TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor);
}
