using System.Reflection;
using System.Runtime.CompilerServices;

namespace Training.Workshop.Tests.M4.S09ExtractClass;

/// <summary>Struktura po każdym kroku: gdzie są pola i gdzie zachowanie (w tym "worek" z kroku 1).</summary>
public sealed class S09StructureTest
{
    [Fact]
    public void StartBookingHoldsCustomerAndPaymentFields()
    {
        Assert.Equal(["amount", "cardNumber", "customerEmail", "customerName", "customerPhone",
            "id", "paymentStatus"], Fields(typeof(Training.Workshop.M4.S09ExtractClass.Start.Booking)));
    }

    [Fact]
    public void Step1CustomerIsADataBagWithoutBehaviour()
    {
        // tylko właściwości rekordu - logika kontaktu została w Booking
        Assert.Equal([], Behaviour(typeof(Training.Workshop.M4.S09ExtractClass.Step1.Customer)));
    }

    [Fact]
    public void Step3BookingComposesCustomerAndPayment()
    {
        Assert.Equal(["amount", "customer", "id", "payment"],
            Fields(typeof(Training.Workshop.M4.S09ExtractClass.Step3.Booking)));
        Assert.Equal(["ContactLine"],
            Behaviour(typeof(Training.Workshop.M4.S09ExtractClass.Step3.Customer)));
    }

    /// <summary>Nazwy pól (bez prefiksu "_") posortowane - kolejność z refleksji nie jest gwarantowana.</summary>
    private static List<string> Fields(Type type)
    {
        return type.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.DeclaredOnly)
            .Where(field => !field.IsDefined(typeof(CompilerGeneratedAttribute)))
            .Select(field => field.Name.TrimStart('_'))
            .Order(StringComparer.Ordinal)
            .ToList();
    }

    /// <summary>Publiczne metody poza akcesorami właściwości i metodami wygenerowanymi dla rekordu.</summary>
    private static List<string> Behaviour(Type type)
    {
        return type.GetMethods(BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public | BindingFlags.DeclaredOnly)
            .Where(method => !method.IsSpecialName)
            .Where(method => !method.IsDefined(typeof(CompilerGeneratedAttribute)))
            .Select(method => method.Name)
            .Where(name => !name.StartsWith('<'))
            .Where(name => !new[] { "Equals", "GetHashCode", "ToString", "Deconstruct" }.Contains(name))
            .Order(StringComparer.Ordinal)
            .ToList();
    }
}
