using Training.Workshop.M6.S16TemplateMethod;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S16TemplateMethod;

/// <summary>Oba raporty (CSV i HTML) identyczne w każdym kroku - także sortowanie i escapowanie.</summary>
public sealed class S16EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<Sale>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<Sale>, string>()
            .Variant("start", s => new Training.Workshop.M6.S16TemplateMethod.Start.CsvSalesReport().Render(s)
                + new Training.Workshop.M6.S16TemplateMethod.Start.HtmlSalesReport().Render(s))
            .Variant("step1", s => new Training.Workshop.M6.S16TemplateMethod.Step1.CsvSalesReport().Render(s)
                + new Training.Workshop.M6.S16TemplateMethod.Step1.HtmlSalesReport().Render(s))
            .Variant("step2", s => new Training.Workshop.M6.S16TemplateMethod.Step2.CsvSalesReport().Render(s)
                + new Training.Workshop.M6.S16TemplateMethod.Step2.HtmlSalesReport().Render(s))
            .Expect("sprzedaż dnia (nieposortowana na wejściu)", [
                new Sale(new TimeOnly(18, 0), "Diuna", 3, Money.Of("120.00")),
                new Sale(new TimeOnly(10, 0), "Kraina Lodu", 2, Money.Of("54.00")),
                new Sale(new TimeOnly(20, 30), "Szybcy & Wsciekli; reedycja", 1, Money.Of("25.00"))], Text("""
                godzina;film;bilety;kwota
                10:00;Kraina Lodu;2;54.00
                18:00;Diuna;3;120.00
                20:30;"Szybcy & Wsciekli; reedycja";1;25.00
                SUMA;;6;199.00
                <table>
                <tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>
                <tr><td>10:00</td><td>Kraina Lodu</td><td>2</td><td>54.00</td></tr>
                <tr><td>18:00</td><td>Diuna</td><td>3</td><td>120.00</td></tr>
                <tr><td>20:30</td><td>Szybcy &amp; Wsciekli; reedycja</td><td>1</td><td>25.00</td></tr>
                <tr><td colspan="2">Suma</td><td>6</td><td>199.00</td></tr>
                </table>
                """))
            .Expect("brak sprzedaży", [], Text("""
                godzina;film;bilety;kwota
                SUMA;;0;0.00
                <table>
                <tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>
                <tr><td colspan="2">Suma</td><td>0</td><td>0.00</td></tr>
                </table>
                """));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepRendersBothReportsTheSame(string test) => Scene.Run(test);

    /// <summary>Jak Javowy text block: znaki końca linii LF i końcowy znak nowej linii.</summary>
    private static string Text(string block) => block.ReplaceLineEndings("\n") + "\n";
}
