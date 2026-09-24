namespace Training.Workshop.M6.S19Visitor.Step1;

/// <summary>Krok 1: element przyjmuje odwiedzającego (double dispatch).</summary>
public interface IOrderItem
{
    TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor);
}
