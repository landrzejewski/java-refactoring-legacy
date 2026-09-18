using Training.Module6.Observer.After;
using Training.Module6.Observer.Before;

namespace Training.Module6.Tests;

public sealed class ObserverContractTest
{
    [Fact]
    public void PreservesSynchronousRegistrationOrderForExistingRecipients()
    {
        var before = new List<string>();
        var after = new List<string>();
        var legacy = new LegacyReleasePublisher(
            @event => before.Add("audit:" + @event.ReleaseId),
            @event => before.Add("metric:" + @event.ReleaseId));
        var publisher = new ReleasePublisher();
        publisher.Subscribe(@event => after.Add("audit:" + @event.ReleaseId));
        publisher.Subscribe(@event => after.Add("metric:" + @event.ReleaseId));

        legacy.Publish(new LegacyReleasePublisher.PublishedRelease("rel-42"));
        publisher.Publish(new ReleasePublished("rel-42"));

        Assert.Equal(before, after);
    }

    [Fact]
    public void UnsubscriptionDuringPublicationAffectsTheNextSnapshot()
    {
        var calls = new List<string>();
        var publisher = new ReleasePublisher();
        ISubscription? later = null;
        publisher.Subscribe(@event =>
        {
            calls.Add("first:" + @event.ReleaseId);
            later!.Dispose();
        });
        later = publisher.Subscribe(@event => calls.Add("later:" + @event.ReleaseId));

        publisher.Publish(new ReleasePublished("one"));
        publisher.Publish(new ReleasePublished("two"));

        Assert.Equal(["first:one", "later:one", "first:two"], calls);
    }

    [Fact]
    public void UsesFailFastExceptionPolicy()
    {
        var calls = new List<string>();
        Exception failure = new InvalidOperationException("listener failed");
        var publisher = new ReleasePublisher();
        publisher.Subscribe(_ => throw failure);
        publisher.Subscribe(_ => calls.Add("not-reached"));

        var propagated = Assert.ThrowsAny<Exception>(
            () => publisher.Publish(new ReleasePublished("rel-42")));

        Assert.Same(failure, propagated);
        Assert.Empty(calls);
    }

    [Fact]
    public void DuplicateRegistrationsRemainIndependentSubscriptions()
    {
        var publisher = new ReleasePublisher();
        var calls = new List<string>();
        ReleaseListener listener = _ => calls.Add("duplicate");
        var first = publisher.Subscribe(listener);
        var middle = publisher.Subscribe(_ => calls.Add("middle"));
        var second = publisher.Subscribe(listener);

        second.Dispose();
        publisher.Publish(new ReleasePublished("one"));
        first.Dispose();
        middle.Dispose();
        publisher.Publish(new ReleasePublished("two"));

        Assert.Equal(["duplicate", "middle"], calls);
    }
}
