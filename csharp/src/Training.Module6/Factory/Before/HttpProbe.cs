namespace Training.Module6.Factory.Before;

public sealed record HttpProbe : IDeploymentProbe
{
    public HttpProbe(string? endpoint)
    {
        if (string.IsNullOrWhiteSpace(endpoint))
        {
            throw new ArgumentException("endpoint must not be blank");
        }
        Endpoint = endpoint;
    }

    public string Endpoint { get; }

    public string Check() => "http-ok:" + Endpoint;
}
