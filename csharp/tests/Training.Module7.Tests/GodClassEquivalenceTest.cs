using Training.Module7.GodClass.After;
using Training.Module7.GodClass.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

public sealed class GodClassEquivalenceTest
{
    [Fact]
    public void CollaboratorsPreserveResultEffectsAndTheirOrder()
    {
        var before = new LegacyReleaseManager();
        var events = new List<string>();
        var repository = new InMemoryReleaseRepository(events.Add);
        var auditTrail = new AuditTrail(events.Add);
        var notifier = new ReleaseNotifier(events.Add);
        var after = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);

        var legacyResult = before.Publish("rel-42", "payments", "2.1.0");
        var refactoredResult = after.Publish("rel-42", "payments", "2.1.0");

        Assert.Equal(legacyResult, refactoredResult);
        Assert.Equal(before.Releases(), repository.FindAll());
        Assert.Equal(before.AuditEntries(), auditTrail.Entries());
        Assert.Equal(before.Notifications(), notifier.Notifications());
        Assert.Equal(["save:rel-42", "audit:rel-42", "notify:rel-42"], events);
        Assert.Equal(before.Events(), events);
    }

    [Fact]
    public void ValidationAndDuplicateFailuresDoNotCreateFurtherEffects()
    {
        var before = new LegacyReleaseManager();
        var events = new List<string>();
        var repository = new InMemoryReleaseRepository(events.Add);
        var auditTrail = new AuditTrail(events.Add);
        var notifier = new ReleaseNotifier(events.Add);
        var after = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);

        var legacyValidationFailure = Assert.ThrowsAny<Exception>(
            () => before.Publish(" ", "payments", "2.1.0"));
        var refactoredValidationFailure = Assert.ThrowsAny<Exception>(
            () => after.Publish(" ", "payments", "2.1.0"));
        AssertSameFailure(legacyValidationFailure, refactoredValidationFailure);
        AssertNoAfterEffects(repository, auditTrail, notifier, events);
        Assert.Empty(before.Events());

        before.Publish("rel-42", "payments", "2.1.0");
        after.Publish("rel-42", "payments", "2.1.0");
        var eventsBeforeDuplicate = events.ToList();
        var legacyDuplicateFailure = Assert.ThrowsAny<Exception>(
            () => before.Publish("rel-42", "other", "9.0.0"));
        var refactoredDuplicateFailure = Assert.ThrowsAny<Exception>(
            () => after.Publish("rel-42", "other", "9.0.0"));

        AssertSameFailure(legacyDuplicateFailure, refactoredDuplicateFailure);
        Assert.Equal(eventsBeforeDuplicate, events);
        Assert.Single(repository.FindAll());
        Assert.Single(auditTrail.Entries());
        Assert.Single(notifier.Notifications());
        Assert.Equal(before.Releases(), repository.FindAll());
        Assert.Equal(before.AuditEntries(), auditTrail.Entries());
        Assert.Equal(before.Notifications(), notifier.Notifications());
    }

    [Fact]
    public void ExposedCollectionsAreDefensiveSnapshots()
    {
        var before = new LegacyReleaseManager();
        before.Publish("rel-42", "payments", "2.1.0");
        var repository = new InMemoryReleaseRepository();
        var auditTrail = new AuditTrail();
        var notifier = new ReleaseNotifier();
        var after = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);
        after.Publish("rel-42", "payments", "2.1.0");

        AssertUnmodifiable(before.Releases());
        AssertUnmodifiable(before.AuditEntries());
        AssertUnmodifiable(before.Notifications());
        AssertUnmodifiable(repository.FindAll());
        AssertUnmodifiable(auditTrail.Entries());
        AssertUnmodifiable(notifier.Notifications());
    }

    [Fact]
    public void RepositoryFailureStopsLaterEffectsAndKeepsTheCompletedSave()
    {
        var events = new List<string>();
        var repository = new InMemoryReleaseRepository(@event =>
        {
            events.Add(@event);
            throw new InvalidOperationException("repository event failure");
        });
        var auditTrail = new AuditTrail(events.Add);
        var notifier = new ReleaseNotifier(events.Add);
        var service = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);

        var failure = Assert.Throws<InvalidOperationException>(
            () => service.Publish("rel-42", "payments", "2.1.0"));

        Assert.Equal("repository event failure", failure.Message);
        Assert.Equal(["save:rel-42"], events);
        Assert.Single(repository.FindAll());
        Assert.Empty(auditTrail.Entries());
        Assert.Empty(notifier.Notifications());
    }

    [Fact]
    public void AuditFailureStopsNotificationAndKeepsEarlierEffects()
    {
        var events = new List<string>();
        var repository = new InMemoryReleaseRepository(events.Add);
        var auditTrail = new AuditTrail(@event =>
        {
            events.Add(@event);
            throw new InvalidOperationException("audit event failure");
        });
        var notifier = new ReleaseNotifier(events.Add);
        var service = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);

        var failure = Assert.Throws<InvalidOperationException>(
            () => service.Publish("rel-42", "payments", "2.1.0"));

        Assert.Equal("audit event failure", failure.Message);
        Assert.Equal(["save:rel-42", "audit:rel-42"], events);
        Assert.Single(repository.FindAll());
        Assert.Equal(["published:rel-42"], auditTrail.Entries());
        Assert.Empty(notifier.Notifications());
    }

    [Fact]
    public void NotificationFailureKeepsAllEarlierEffects()
    {
        var events = new List<string>();
        var repository = new InMemoryReleaseRepository(events.Add);
        var auditTrail = new AuditTrail(events.Add);
        var notifier = new ReleaseNotifier(@event =>
        {
            events.Add(@event);
            throw new InvalidOperationException("notification event failure");
        });
        var service = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);

        var failure = Assert.Throws<InvalidOperationException>(
            () => service.Publish("rel-42", "payments", "2.1.0"));

        Assert.Equal("notification event failure", failure.Message);
        Assert.Equal(["save:rel-42", "audit:rel-42", "notify:rel-42"], events);
        Assert.Single(repository.FindAll());
        Assert.Equal(["published:rel-42"], auditTrail.Entries());
        Assert.Equal(["release-published:rel-42"], notifier.Notifications());
    }

    private static void AssertNoAfterEffects(
        InMemoryReleaseRepository repository,
        AuditTrail auditTrail,
        ReleaseNotifier notifier,
        List<string> events)
    {
        Assert.Empty(repository.FindAll());
        Assert.Empty(auditTrail.Entries());
        Assert.Empty(notifier.Notifications());
        Assert.Empty(events);
    }

    private static void AssertUnmodifiable<T>(IReadOnlyList<T> snapshot)
    {
        var asCollection = Assert.IsAssignableFrom<ICollection<T>>(snapshot);
        Assert.True(asCollection.IsReadOnly);
        Assert.Throws<NotSupportedException>(() => asCollection.Clear());
    }
}
