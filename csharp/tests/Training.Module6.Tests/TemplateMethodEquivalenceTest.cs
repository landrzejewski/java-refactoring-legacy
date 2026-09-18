using Training.Module6.TemplateMethod.After;
using Training.Module6.TemplateMethod.Before;
using ReleaseDraft = Training.Module6.TemplateMethod.After.ReleaseDraft;

namespace Training.Module6.Tests;

public sealed class TemplateMethodEquivalenceTest
{
    [Fact]
    public void CommonSkeletonPreservesBothImportFormats()
    {
        var legacyPipe = new LegacyPipeReleaseImporter().ImportRelease(" rel-42 | payments ");
        var pipe = new PipeReleaseImporter().ImportRelease(" rel-42 | payments ");
        var legacyKeyValue = new LegacyKeyValueReleaseImporter()
            .ImportRelease("id=rel-42;service=payments");
        var keyValue = new KeyValueReleaseImporter().ImportRelease("id=rel-42;service=payments");

        AssertEquivalent(legacyPipe, pipe);
        AssertEquivalent(legacyKeyValue, keyValue);
    }

    [Fact]
    public void TemplateMethodProtectsTheRequiredOrder()
    {
        var method = typeof(ReleaseImporter).GetMethod(
            nameof(ReleaseImporter.ImportRelease), [typeof(string)])!;

        // Java: Modifier.isFinal; in C# a non-virtual (or sealed) method cannot be overridden.
        Assert.True(!method.IsVirtual || method.IsFinal);
    }

    [Fact]
    public void CommonValidationPreservesTheLegacyFailure()
    {
        var before = Assert.ThrowsAny<Exception>(
            () => new LegacyPipeReleaseImporter().ImportRelease(" |payments"));
        var after = Assert.ThrowsAny<Exception>(
            () => new PipeReleaseImporter().ImportRelease(" |payments"));

        Assert.Equal(before.GetType(), after.GetType());
        Assert.Equal(before.Message, after.Message);
    }

    [Fact]
    public void SupportsSubclassOutsideTheImplementationPackage()
    {
        var imported = new CommaReleaseImporter().ImportRelease("rel-42,payments");

        Assert.Equal("rel-42", imported.ReleaseId);
        Assert.Equal("payments", imported.Service);
    }

    private static void AssertEquivalent(
        Training.Module6.TemplateMethod.Before.ReleaseDraft before,
        ReleaseDraft after)
    {
        Assert.Equal(before.ReleaseId, after.ReleaseId);
        Assert.Equal(before.Service, after.Service);
    }

    private sealed class CommaReleaseImporter : ReleaseImporter
    {
        protected override Fields Parse(string raw)
        {
            var parts = raw.Split(',');
            if (parts.Length != 2)
            {
                throw new ArgumentException("expected releaseId and service");
            }
            return CreateFields(parts[0].Trim(), parts[1].Trim());
        }
    }
}
