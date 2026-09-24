using System.Reflection;
using Training.Workshop.M3.S11Dip;

namespace Training.Workshop.Tests.M3.S11Dip;

/// <summary>
/// DIP to kierunek zależności, nie sposób jej dostarczenia. Sprawdzamy przestrzenie nazw typów
/// pól przypadku użycia: DI w kroku 1 nie zmienia kierunku, port w kroku 3 - tak.
/// (Bez Start - ten jest edytowany na żywo; krok 1 to Start z wstrzykniętą zależnością.)
/// </summary>
public sealed class S11DependencyDirectionTest
{
    private static bool DependsOnInfra(Type useCase)
    {
        return useCase.GetFields(BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public
                                 | BindingFlags.NonPublic | BindingFlags.DeclaredOnly)
            .Select(field => field.FieldType)
            .Any(type => type.Namespace?.EndsWith(".Infra", StringComparison.Ordinal) == true);
    }

    [Fact]
    public void Step1AndStep2PolicyDependsOnInfrastructure()
    {
        Assert.True(DependsOnInfra(typeof(Training.Workshop.M3.S11Dip.Step1.App.ConfirmReservation)),
            "wstrzykniecie konkretnej klasy to DI, ale nie DIP");
        Assert.True(DependsOnInfra(typeof(Training.Workshop.M3.S11Dip.Step2.App.ConfirmReservation)));
    }

    [Fact]
    public void Step3PolicyDependsOnlyOnItsOwnPort()
    {
        Assert.False(DependsOnInfra(typeof(Training.Workshop.M3.S11Dip.Step3.App.ConfirmReservation)));
        Assert.True(typeof(Training.Workshop.M3.S11Dip.Step3.App.ICustomerNotifier)
                .IsAssignableFrom(typeof(Training.Workshop.M3.S11Dip.Step3.Infra.SmtpCustomerNotifier)),
            "adapter z Infra implementuje port z App: zaleznosc zrodlowa Infra -> App");
    }

    [Fact]
    public void Step3PolicyIsTestableWithAHandWrittenFake()
    {
        var sent = new List<string>();
        var useCase = new Training.Workshop.M3.S11Dip.Step3.App.ConfirmReservation(
            new FakeNotifier((email, message) => sent.Add(email + ": " + message)));

        useCase.Confirm(new Reservation("anna@kino.pl", "Diuna", new DateTime(2026, 10, 2, 20, 0, 0), 2));

        Assert.Equal(["anna@kino.pl: Rezerwacja: Diuna, 2026-10-02T20:00, miejsc: 2. Zaplac w ciagu 15 minut."],
            sent);
    }

    private sealed class FakeNotifier(Action<string, string> notify)
        : Training.Workshop.M3.S11Dip.Step3.App.ICustomerNotifier
    {
        public void NotifyCustomer(string email, string message) => notify(email, message);
    }
}
