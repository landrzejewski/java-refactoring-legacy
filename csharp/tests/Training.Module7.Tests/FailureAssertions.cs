namespace Training.Module7.Tests;

internal static class FailureAssertions
{
    public static void AssertSameFailure(Action before, Action after)
    {
        var beforeFailure = Assert.ThrowsAny<Exception>(before);
        var afterFailure = Assert.ThrowsAny<Exception>(after);

        AssertSameFailure(beforeFailure, afterFailure);
    }

    public static void AssertSameFailure(Exception before, Exception after)
    {
        Assert.Equal(before.GetType(), after.GetType());
        Assert.Equal(before.Message, after.Message);
    }
}
