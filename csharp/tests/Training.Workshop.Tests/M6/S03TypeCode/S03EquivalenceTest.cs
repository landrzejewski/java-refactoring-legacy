using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S03TypeCode;

/// <summary>Odczyt CSV, zachowanie formatu i zapis zwrotny tego samego kodu int.</summary>
public sealed class S03EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", Safe(new Training.Workshop.M6.S03TypeCode.Start.ScreeningCsv().Describe))
        .Variant("step1", Safe(new Training.Workshop.M6.S03TypeCode.Step1.ScreeningCsv().Describe))
        .Variant("step2", Safe(new Training.Workshop.M6.S03TypeCode.Step2.ScreeningCsv().Describe))
        .Variant("step3", Safe(new Training.Workshop.M6.S03TypeCode.Step3.ScreeningCsv().Describe))
        .Expect("2D", "Amator;1", "Amator|2D|25.00|okulary:nie|csv=Amator;1")
        .Expect("3D", "Kraina Lodu;2", "Kraina Lodu|3D|32.00|okulary:tak|csv=Kraina Lodu;2")
        .Expect("IMAX ze spacją", "Diuna; 3", "Diuna|IMAX|40.00|okulary:nie|csv=Diuna;3")
        .Expect("nieznany kod", "Diuna;7", "ERROR: unknown format code: 7")
        .Expect("kod 0", "Diuna;0", "ERROR: unknown format code: 0")
        .Expect("kod nie jest liczbą", "Diuna;x", "ERROR: The input string 'x' was not in a correct format.");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepReadsAndWritesTheSameCsv(string test) => Scene.Run(test);

    /// <summary>
    /// W Javie NumberFormatException dziedziczy po IllegalArgumentException; w .NET FormatException
    /// nie dziedziczy po ArgumentException, więc łapiemy oba.
    /// </summary>
    private static Func<string, string> Safe(Func<string, string> describe)
    {
        return line =>
        {
            try
            {
                return describe(line);
            }
            catch (Exception exception) when (exception is ArgumentException or FormatException)
            {
                return "ERROR: " + exception.Message;
            }
        };
    }
}
