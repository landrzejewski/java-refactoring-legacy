using System.Reflection;
using Training.Workshop.M5.S29BoundaryObject;
using Training.Workshop.M5.S29BoundaryObject.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S29BoundaryObject;

/// <summary>Obiekt graniczny jest zawsze poprawny, format terminala zna tylko granica, domena widzi port.</summary>
public sealed class S29SolutionTest
{
    private readonly TerminalResponseMapper _mapper = new();

    [Fact]
    public void BoundaryObjectCannotBeInvalid()
    {
        Assert.Throws<ArgumentException>(() => new PaymentConfirmation(Outcome.Approved, " ", Money.Of("10.00")));
        Assert.Throws<ArgumentException>(() => new PaymentConfirmation(Outcome.Approved, "AUTH", Money.Of("-0.01")));
        Assert.Throws<ArgumentException>(() => new PaymentConfirmation(Outcome.Declined, "51", Money.Of("10.00")));
    }

    [Theory]
    [InlineData("OK;AUTH-R1")]
    [InlineData("MAYBE;X;0")]
    [InlineData("OK;;4500")]
    [InlineData("OK;AUTH-R1;abc")]
    public void MalformedResponseIsRejectedAtTheBoundary(string raw)
    {
        Assert.Throws<ArgumentException>(() => _mapper.ToConfirmation(raw));
    }

    [Fact]
    public void MapperTranslatesUnitsBothWays()
    {
        Assert.Equal(4500, _mapper.ToGrosze(Money.Of("45.00")));
        Assert.Equal(new PaymentConfirmation(Outcome.Approved, "AUTH-R1", Money.Of("45.00")),
            _mapper.ToConfirmation("OK;AUTH-R1;4500"));
    }

    [Fact]
    public void ServiceWorksWithAnyTerminalBehindThePort()
    {
        var service = new PaymentService(new FakeTerminal());
        Assert.Equal("PAID FAKE-R1", service.Pay("R1", Money.Of("999.00")));
        Assert.Equal(Money.Of("999.00"), service.TotalAuthorized());
    }

    [Fact]
    public void DomainServiceSeesNeitherTheLegacyClientNorTheMapper()
    {
        const BindingFlags declared = BindingFlags.Instance | BindingFlags.Static
            | BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.DeclaredOnly;
        var used = new List<Type>();
        used.AddRange(typeof(PaymentService).GetFields(declared).Select(field => field.FieldType));
        foreach (var constructor in typeof(PaymentService).GetConstructors())
        {
            used.AddRange(constructor.GetParameters().Select(parameter => parameter.ParameterType));
        }
        foreach (var method in typeof(PaymentService).GetMethods(declared))
        {
            used.AddRange(method.GetParameters().Select(parameter => parameter.ParameterType));
            used.Add(method.ReturnType);
        }
        Assert.DoesNotContain(typeof(LegacyTerminalClient), used); // serwis nie zna klienta terminala
        Assert.DoesNotContain(typeof(TerminalResponseMapper), used); // serwis nie zna formatu odpowiedzi
    }

    private sealed class FakeTerminal : IPaymentTerminal
    {
        public PaymentConfirmation Authorize(string bookingId, Money amount) =>
            new(Outcome.Approved, "FAKE-" + bookingId, amount);
    }
}
