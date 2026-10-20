namespace Training.Module5.Chain.After;

/// <summary>
/// Ogniwo łańcucha zobowiązań. Każdy handler sam decyduje: obsługuje żądanie
/// (i kończy przetwarzanie) albo przekazuje je dalej przez <see cref="PassOn"/>.
/// Żądanie obsługuje dokładnie jeden handler; gdy nikt go nie obsłuży, koniec łańcucha
/// odrzuca je.
/// </summary>
/// <remarks>
/// Różnica względem Decoratora: dekorator zawsze deleguje do opakowanego obiektu i
/// dokłada do wyniku swoje zachowanie (działają wszystkie warstwy). W łańcuchu zobowiązań
/// handler może przerwać przetwarzanie - zwykle działa tylko jedno ogniwo.
/// </remarks>
public abstract class ApprovalHandler
{
    private ApprovalHandler? _next;

    /// <summary>
    /// Jawne składanie łańcucha; zwraca kolejne ogniwo, aby można było pisać
    /// <c>first.Then(second).Then(third)</c>.
    /// </summary>
    public ApprovalHandler Then(ApprovalHandler next)
    {
        ArgumentNullException.ThrowIfNull(next);
        _next = next;
        return next;
    }

    public abstract Decision Handle(ChangeRequest request);

    protected Decision PassOn(ChangeRequest request)
    {
        if (_next is null)
        {
            return Decision.Of(Approver.Rejected, "no approver for " + request.Id);
        }
        return _next.Handle(request);
    }
}
