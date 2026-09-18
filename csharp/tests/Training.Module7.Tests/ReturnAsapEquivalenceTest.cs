using System.Collections;
using Training.Module7.ReturnAsap;
using Training.Module7.ReturnAsap.After;
using Training.Module7.ReturnAsap.Before;

namespace Training.Module7.Tests;

public sealed class ReturnAsapEquivalenceTest
{
    private readonly LegacyArtifactFinder _before = new();
    private readonly ArtifactFinder _after = new();

    [Fact]
    public void EarlyReturnPreservesEmptyMissingAndPresentResults()
    {
        var first = new Artifact("api.jar", "sha-1");
        var second = new Artifact("worker.jar", "sha-2");
        Artifact[] artifacts = [first, second];

        Assert.Equal(
            _before.FindByChecksum([], "missing"),
            _after.FindByChecksum([], "missing"));
        Assert.Equal(
            _before.FindByChecksum(artifacts, "missing"),
            _after.FindByChecksum(artifacts, "missing"));
        Assert.Same(first, _after.FindByChecksum(artifacts, "sha-1"));
        Assert.Same(second, _after.FindByChecksum(artifacts, "sha-2"));
        Assert.Equal(
            _before.FindByChecksum(artifacts, "sha-2"),
            _after.FindByChecksum(artifacts, "sha-2"));
    }

    [Fact]
    public void BothVersionsReturnTheFirstMatchingInstance()
    {
        var first = new Artifact("api.jar", "same");
        var second = new Artifact("worker.jar", "same");
        Artifact[] artifacts = [first, second];

        Assert.Same(first, _before.FindByChecksum(artifacts, "same"));
        Assert.Same(first, _after.FindByChecksum(artifacts, "same"));
    }

    [Fact]
    public void NullElementIsReadBeforeAMatchButNotAfterIt()
    {
        var matching = new Artifact("api.jar", "sha-1");
        Artifact[] nullBeforeMatch = [null!, matching];
        Artifact[] nullAfterMatch = [matching, null!];

        Assert.Equal(
            Assert.Throws<ArgumentNullException>(
                () => _before.FindByChecksum(nullBeforeMatch, "sha-1")).Message,
            Assert.Throws<ArgumentNullException>(
                () => _after.FindByChecksum(nullBeforeMatch, "sha-1")).Message);
        Assert.Same(matching, _before.FindByChecksum(nullAfterMatch, "sha-1"));
        Assert.Same(matching, _after.FindByChecksum(nullAfterMatch, "sha-1"));
    }

    [Fact]
    public void InputValidationHasTheSameOrderAndMessages()
    {
        AssertSameFailure(null, "sha-1");
        AssertSameFailure([], null);
    }

    [Fact]
    public void EarlyReturnPreservesIndexedListAccesses()
    {
        Artifact[] artifacts =
        [
            new("api.jar", "sha-1"),
            new("worker.jar", "sha-2")
        ];
        var legacyTrace = new List<string>();
        var refactoredTrace = new List<string>();

        _before.FindByChecksum(new TracedList(artifacts, legacyTrace), "sha-1");
        _after.FindByChecksum(new TracedList(artifacts, refactoredTrace), "sha-1");

        Assert.Equal(["size", "get:0"], legacyTrace);
        Assert.Equal(legacyTrace, refactoredTrace);
    }

    private void AssertSameFailure(IReadOnlyList<Artifact>? artifacts, string? checksum)
    {
        Assert.Equal(
            Assert.Throws<ArgumentNullException>(
                () => _before.FindByChecksum(artifacts!, checksum!)).Message,
            Assert.Throws<ArgumentNullException>(
                () => _after.FindByChecksum(artifacts!, checksum!)).Message);
    }

    private sealed class TracedList(IReadOnlyList<Artifact> artifacts, List<string> trace)
        : IReadOnlyList<Artifact>
    {
        public Artifact this[int index]
        {
            get
            {
                trace.Add($"get:{index}");
                return artifacts[index];
            }
        }

        public int Count
        {
            get
            {
                trace.Add("size");
                return artifacts.Count;
            }
        }

        public IEnumerator<Artifact> GetEnumerator() => artifacts.GetEnumerator();

        IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();
    }
}
