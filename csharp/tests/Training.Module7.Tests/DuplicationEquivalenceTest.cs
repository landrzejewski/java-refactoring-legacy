using System.Globalization;
using Training.Module7.Duplication.After;
using Training.Module7.Duplication.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

[Collection("Console")]
public sealed class DuplicationEquivalenceTest
{
    [Fact]
    public void PreservesBothPublicationVariantsIndependentlyOfDefaultLocale()
    {
        var previousCulture = CultureInfo.CurrentCulture;
        try
        {
            // Turkish-I problem: culture-sensitive "IMAGE".ToLower() yields "ımage".
            CultureInfo.CurrentCulture = new CultureInfo("tr-TR");

            var legacy = new LegacyArtifactPublisher();
            var refactored = new ArtifactPublisher();

            Assert.Equal("image:42-SNAPSHOT", legacy.PublishSnapshot(" IMAGE ", 42));
            Assert.Equal(
                legacy.PublishSnapshot(" IMAGE ", 42),
                refactored.PublishSnapshot(" IMAGE ", 42));
            Assert.Equal("image:42", legacy.PublishRelease(" IMAGE ", 42));
            Assert.Equal(
                legacy.PublishRelease(" IMAGE ", 42),
                refactored.PublishRelease(" IMAGE ", 42));
        }
        finally
        {
            CultureInfo.CurrentCulture = previousCulture;
        }
    }

    [Fact]
    public void PreservesValidationTypeMessageAndOrderForBothVariants()
    {
        var legacy = new LegacyArtifactPublisher();
        var refactored = new ArtifactPublisher();

        AssertSameFailure(
            () => legacy.PublishSnapshot(null!, 0),
            () => refactored.PublishSnapshot(null!, 0));
        AssertSameFailure(
            () => legacy.PublishSnapshot(" \t", 0),
            () => refactored.PublishSnapshot(" \t", 0));
        AssertSameFailure(
            () => legacy.PublishSnapshot("api", 0),
            () => refactored.PublishSnapshot("api", 0));

        AssertSameFailure(
            () => legacy.PublishRelease(null!, 0),
            () => refactored.PublishRelease(null!, 0));
        AssertSameFailure(
            () => legacy.PublishRelease(" \t", 0),
            () => refactored.PublishRelease(" \t", 0));
        AssertSameFailure(
            () => legacy.PublishRelease("api", 0),
            () => refactored.PublishRelease("api", 0));
    }
}
