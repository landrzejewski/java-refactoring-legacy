using System.Reflection;
using Training.Workshop.M5.S15Compatibility;
using Training.Workshop.Tests.M5.Tooling;

namespace Training.Workshop.Tests.M5.S15Compatibility;

/// <summary>
/// Zgodność binarna i refleksyjna. Wtyczka partnera kompilowana jest w pamięci (Roslyn) przeciw jednej
/// wersji API (pliki wariantu sceny z przestrzenią nazw podmienioną na "Api", assembly "Api"),
/// a uruchamiana z inną - jak podmiana Api.dll na serwerze.
/// Wersja bazowa "1.x" to step1 (API identyczne jak w start, ale start zmieniamy na żywo).
/// </summary>
public sealed class S15SolutionTest
{
    private const string Plugin = """
        using Api;
        using Training.Workshop.Shared;

        namespace Client;

        public static class Plugin
        {
            public static string Run()
            {
                var ticket = new StudentTicket("Amator", Money.Of("25.00"));
                return ticket.Price() + "/" + new BoxOfficeApi().Quote(ticket);
            }
        }
        """;

    [Fact]
    public void PullUpIsBinaryCompatibleForCallers()
    {
        var plugin = CompilePlugin(CompileApi("Step1"));
        Assert.Equal("18.75/18.75", Run(plugin, CompileApi("Step1")));
        // StudentTicket.Price() znalezione w klasie bazowej
        Assert.Equal("18.75/18.75", Run(plugin, CompileApi("Step2")));
    }

    [Fact]
    public void GeneralizedParameterBreaksOldBinary()
    {
        var plugin = CompilePlugin(CompileApi("Step1"));
        var failure = Assert.Throws<MissingMethodException>(() => Run(plugin, CompileApi("Step3")));
        Assert.Contains("Quote", failure.Message);
    }

    [Fact]
    public void GeneralizedParameterIsSourceCompatible()
    {
        var api = CompileApi("Step3");
        // po rekompilacji wtyczka działa
        Assert.Equal("18.75/18.75", Run(CompilePlugin(api), api));
    }

    [Fact]
    public void DelegatingOverloadRestoresBinaryCompatibility()
    {
        var plugin = CompilePlugin(CompileApi("Step1"));
        Assert.Equal("18.75/18.75", Run(plugin, CompileApi("Step4")));
    }

    [Fact]
    public void PullUpHidesAnnotatedMethodFromDeclaredMethodsLookup()
    {
        Assert.True(HasDeclaredColumn(typeof(Training.Workshop.M5.S15Compatibility.Step1.StudentTicket)));
        // pułapka: eksporter oparty na GetMethods(DeclaredOnly) nie znalazłby kolumny po Pull Up
        Assert.False(HasDeclaredColumn(typeof(Training.Workshop.M5.S15Compatibility.Step2.StudentTicket)));
        Assert.True(HasDeclaredColumn(typeof(Training.Workshop.M5.S15Compatibility.Step2.Ticket)));
    }

    private static bool HasDeclaredColumn(Type type)
    {
        const BindingFlags declared = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance
            | BindingFlags.Static | BindingFlags.DeclaredOnly;
        return type.GetMethods(declared).Any(m => m.IsDefined(typeof(ColumnAttribute)));
    }

    private static InMemoryAssembly CompileApi(string variant)
    {
        var api = InMemoryAssembly.Compile("Api", SceneSources.Rewritten("S15Compatibility", variant, "Api"));
        Assert.True(api.Success, "kompilacja " + variant + ": " + api.ErrorIds);
        return api;
    }

    private static InMemoryAssembly CompilePlugin(InMemoryAssembly api)
    {
        var plugin = InMemoryAssembly.Compile("Plugin", [Plugin], api);
        Assert.True(plugin.Success, "kompilacja wtyczki: " + plugin.ErrorIds);
        return plugin;
    }

    /// <summary>Uruchamia wtyczkę z podanym Api.dll (jak podmiana pliku na serwerze).</summary>
    private static string Run(InMemoryAssembly plugin, InMemoryAssembly api)
    {
        var run = InMemoryAssembly.Load(plugin, api).GetType("Client.Plugin")!.GetMethod("Run")!;
        try
        {
            return (string)run.Invoke(null, null)!;
        }
        catch (TargetInvocationException e) when (e.InnerException != null)
        {
            throw e.InnerException;
        }
    }
}
