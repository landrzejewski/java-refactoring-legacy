namespace Training.Patterns.Behavioral.Observer;

internal class ServerEvent
{
    public string? Payload { get; }

    internal ServerEvent(string? payload)
    {
        Payload = payload;
    }

    public class ServerEventBuilder
    {
        private string? payload;

        internal ServerEventBuilder()
        {
        }

        /// <returns><c>this</c>.</returns>
        public ServerEventBuilder Payload(string? payload)
        {
            this.payload = payload;
            return this;
        }

        public ServerEvent Build() => new(payload);

        public override string ToString() => "ServerEvent.ServerEventBuilder(payload=" + (payload ?? "null") + ")";
    }

    public static ServerEventBuilder Builder() => new();
}
