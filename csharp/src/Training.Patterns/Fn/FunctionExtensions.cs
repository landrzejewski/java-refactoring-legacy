namespace Training.Patterns.Fn;

/// <summary>Java's <c>Function.andThen</c> for <see cref="Func{T, TResult}"/>.</summary>
public static class FunctionExtensions
{
    public static Func<T, TNext> AndThen<T, TResult, TNext>(this Func<T, TResult> function, Func<TResult, TNext> after)
    {
        ArgumentNullException.ThrowIfNull(after);
        return value => after(function(value));
    }
}
