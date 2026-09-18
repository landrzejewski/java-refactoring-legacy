using Training.Module5.Composition.After;

namespace Training.Module5.Tests.Composition;

public sealed class RecipientListEquivalenceTest
{
    [Fact]
    public void CompositionPreservesTheIntendedRecipientListContract()
    {
        var before = new Training.Module5.Composition.Before.RecipientList();
        var after = new RecipientList();

        // Java's List.add returns a boolean; ICollection<T>.Add in .NET returns void,
        // so both variants expose the same void Add.
        before.Add("alice@example.com");
        after.Add("alice@example.com");
        before.Add("bob@example.com");
        after.Add("bob@example.com");
        before.Add("alice@example.com");
        after.Add("alice@example.com");

        Assert.Equal(3, before.Count);
        Assert.Equal(before.Count, after.Count);
        Assert.Contains("bob@example.com", before);
        Assert.Equal(
            before.Contains("bob@example.com"),
            after.Contains("bob@example.com"));
        Assert.Equal(before, after);

        Assert.Equal(
            before.Remove("alice@example.com"),
            after.Remove("alice@example.com"));
        Assert.Equal(
            ["bob@example.com", "alice@example.com"],
            before.Snapshot());
        Assert.Equal(before.Snapshot(), after.Snapshot());

        var beforeSnapshot = before.Snapshot();
        var afterSnapshot = after.Snapshot();
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<string>)beforeSnapshot).Add("forbidden@example.com"));
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<string>)afterSnapshot).Add("forbidden@example.com"));
        before.Add("carol@example.com");
        after.Add("carol@example.com");

        Assert.Equal(
            ["bob@example.com", "alice@example.com"],
            beforeSnapshot);
        Assert.Equal(beforeSnapshot, afterSnapshot);
        Assert.Equal(before, after);

        // Java: after.iterator().remove() throws. The .NET counterpart of that leak is
        // a client casting the composed list back to a mutable collection interface.
        using (var afterEnumerator = after.GetEnumerator())
        {
            Assert.True(afterEnumerator.MoveNext());
        }
        Assert.IsAssignableFrom<ICollection<string>>(before);
        Assert.False(((object)after) is ICollection<string>);

        bool removedBefore = before.Remove("missing@example.com");
        bool removedAfter = after.Remove("missing@example.com");
        Assert.False(removedBefore);
        Assert.Equal(removedBefore, removedAfter);

        var anotherBefore = new Training.Module5.Composition.Before.RecipientList();
        var anotherAfter = new Training.Module5.Composition.After.RecipientList();
        Assert.Empty(anotherBefore);
        Assert.Equal(0, anotherAfter.Count);
    }
}
