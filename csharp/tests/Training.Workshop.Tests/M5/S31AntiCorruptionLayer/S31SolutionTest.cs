using System.Reflection;
using Training.Workshop.M5.S31AntiCorruptionLayer;
using Training.Workshop.M5.S31AntiCorruptionLayer.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S31AntiCorruptionLayer;

/// <summary>Typy dystrybutora nie przekraczają ACL; licencja w języku kina pilnuje swoich niezmienników.</summary>
public sealed class S31SolutionTest
{
    private const string LegacyNamespace = "Training.Workshop.M5.S31AntiCorruptionLayer";

    private const BindingFlags Declared =
        BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic;

    [Fact]
    public void DomainServiceUsesNoLegacyType()
    {
        foreach (var type in SignatureTypes(typeof(SettlementService)))
        {
            Assert.False(type.Namespace == LegacyNamespace, "typ dystrybutora w domenie: " + type);
        }
    }

    [Fact]
    public void TranslatorIsAnImplementationDetailOfTheAcl()
    {
        var translator = typeof(DistributorAcl).Assembly.GetType(LegacyNamespace + ".Step3.DistributorTranslator")!;
        Assert.False(translator.IsPublic);
    }

    [Fact]
    public void AclTranslatesLegacyConventions()
    {
        var acl = new DistributorAcl(new DistributorFeed([
            new DistributorRecord("KRL", "KRAINA LODU", "S", "20261015", "20261115", "30000", " 40 %")]));
        Assert.Equal(new FilmLicense("KRL", "Kraina lodu", false, new DateOnly(2026, 10, 15),
            new DateOnly(2026, 11, 15), 40, Money.Of("300.00")), acl.Find("KRL"));
        Assert.Null(acl.Find("XYZ")); // brak licencji to jawne FilmLicense? portu, nie nieoznaczony null klienta dystrybutora
    }

    [Fact]
    public void AclRejectsMalformedLegacyDataAtTheBoundary()
    {
        var acl = new DistributorAcl(new DistributorFeed([
            new DistributorRecord("DIU", "DIUNA", "A", "2026-10-01", "20261031", "50000", "45%")]));
        var error = Assert.Throws<ArgumentException>(() => acl.Find("DIU"));
        Assert.Equal("invalid legacy date: 2026-10-01", error.Message);
    }

    [Fact]
    public void LicenseProtectsItsInvariants()
    {
        var day = new DateOnly(2026, 10, 20);
        Assert.Throws<ArgumentException>(() => new FilmLicense("DIU", "Diuna", true, day, day.AddDays(-1), 45, Money.Zero));
        Assert.Throws<ArgumentException>(() => new FilmLicense("DIU", "Diuna", true, day, day, 120, Money.Zero));
    }

    [Fact]
    public void ServiceWorksWithAnyLicenseSource()
    {
        var day = new DateOnly(2026, 10, 20);
        var festival = new FilmLicense("FST", "Festiwal", true, day, day, 10, Money.Of("50.00"));
        var service = new SettlementService(new FixedLicenses(festival));
        Assert.True(service.CanScreen("FST", day));
        Assert.Equal("ROZLICZENIE Festiwal 2026-10-20: przychod 1000.00, udzial 10%, minimum 50.00, "
            + "dla dystrybutora 100.00", service.Settle("FST", day, Money.Of("1000.00")));
    }

    private static List<Type> SignatureTypes(Type type)
    {
        List<Type> types = [];
        types.AddRange(type.GetFields(Declared).Select(field => field.FieldType));
        foreach (var constructor in type.GetConstructors(Declared))
        {
            types.AddRange(constructor.GetParameters().Select(parameter => parameter.ParameterType));
        }
        foreach (var method in type.GetMethods(Declared))
        {
            types.AddRange(method.GetParameters().Select(parameter => parameter.ParameterType));
            types.Add(method.ReturnType);
        }
        return types;
    }

    private sealed class FixedLicenses(FilmLicense license) : IFilmLicenses
    {
        public FilmLicense? Find(string filmCode) => license;
    }
}
