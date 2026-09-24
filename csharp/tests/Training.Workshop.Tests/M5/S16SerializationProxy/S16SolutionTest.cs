using System.Reflection;
using System.Text.Json;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S16SerializationProxy;

/// <summary>Serializacja: dane zapisane przez start czytane przez nowsze kroki. Proxy: kiedy da się opakować serwis.</summary>
public sealed class S16SolutionTest
{
    /// <summary>
    /// "Plik z v1": StudentTicket("Amator", "F3", "S-123") zapisany przez klasę ze start (bez nadklasy).
    /// Zamrożony jako stała, żeby zmiany start na żywo nie zmieniały danych historycznych.
    /// Stała nazwa i przestrzeń kontraktu pozwalają czytać go klasami z innych przestrzeni nazw C#.
    /// </summary>
    private const string SavedByV1 = """<StudentTicket xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:cinelegacy:tickets"><seat>F3</seat><studentId>S-123</studentId><title>Amator</title></StudentTicket>""";

    [Fact]
    public void StartWritesTheFrozenV1Data()
    {
        Assert.Equal(SavedByV1, TicketStore.Save(new Training.Workshop.M5.S16SerializationProxy.Start.StudentTicket("Amator", "F3", "S-123")));
    }

    [Fact]
    public void PulledUpFieldsAreSilentlyLostWhenReadingOldData()
    {
        var ticket = TicketStore.Load<Training.Workshop.M5.S16SerializationProxy.Step1.StudentTicket>(SavedByV1);
        // pułapka: ten sam kontrakt, brak wyjątku, utracony title (w XML stoi po studentId, a baza czytana jest pierwsza)
        Assert.Null(ticket.Title);
        Assert.Equal(" F3 (legitymacja S-123)", ticket.Describe());
    }

    [Fact]
    public void SerializationProxyRejectsOldDataLoudly()
    {
        Assert.ThrowsAny<JsonException>(() => TicketStore.Load<Training.Workshop.M5.S16SerializationProxy.Step2.StudentTicket>(SavedByV1));
    }

    [Fact]
    public void SerializationProxyWritesFlatFormIndependentOfHierarchy()
    {
        var stream = TicketStore.Save(new Training.Workshop.M5.S16SerializationProxy.Step2.StudentTicket("Amator", "F3", "S-1"));
        Assert.Equal("""{"Title":"Amator","Seat":"F3","StudentId":"S-1"}""", stream);
        // poziom Ticket nie jest częścią formatu
        Assert.DoesNotContain("Ticket", stream);
    }

    [Fact]
    public void DispatchProxyCannotWrapSealedClassWithoutInterface()
    {
        var pricing = typeof(Training.Workshop.M5.S16SerializationProxy.Step2.TicketPricing);
        Assert.True(pricing.IsSealed);
        Assert.Empty(pricing.GetInterfaces());
        Assert.Throws<ArgumentException>(() => DispatchProxy.Create(pricing, typeof(AuditProxy)));
    }

    [Fact]
    public void ExtractedInterfaceAllowsDynamicProxy()
    {
        var pricing = DispatchProxy.Create<Training.Workshop.M5.S16SerializationProxy.Step3.IPricing, AuditProxy>();
        var audit = (AuditProxy)(object)pricing;
        audit.Target = new Training.Workshop.M5.S16SerializationProxy.Step3.TicketPricing();
        Assert.Equal(Money.Of("24.00"), pricing.StudentPrice(Money.Of("32.00")));
        Assert.Equal(1, audit.Calls);
    }

    /// <summary>Proxy audytowe: liczy wywołania i deleguje do celu (odpowiednik InvocationHandler z Javy).</summary>
    public class AuditProxy : DispatchProxy
    {
        public object? Target { get; set; }

        public int Calls { get; private set; }

        protected override object? Invoke(MethodInfo? targetMethod, object?[]? args)
        {
            Calls++;
            return targetMethod!.Invoke(Target, args);
        }
    }
}
