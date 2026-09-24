using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Step2;

/// <summary>
/// Krok 2: Form Template Method - Extract Superclass + Pull Up Render(). Szkielet nie jest wirtualny
/// (odpowiednik final): kolejność kroków i liczenie sumy należą do bazy, podklasy dostarczają tylko formatowanie.
/// </summary>
public abstract class SalesReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        var sorted = sales.OrderBy(sale => sale.Time).ToList();
        var text = new StringBuilder(Header());
        var tickets = 0;
        var total = Money.Zero;
        foreach (var sale in sorted)
        {
            text.Append(Row(sale));
            tickets += sale.Tickets;
            total = total.Plus(sale.Amount);
        }
        return text.Append(Footer(tickets, total)).ToString();
    }

    protected abstract string Header();

    protected abstract string Row(Sale sale);

    protected abstract string Footer(int tickets, Money total);
}
