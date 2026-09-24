using Training.Workshop.M3.S13BoundaryCheck;

namespace Training.Workshop.Tests.M3.S13BoundaryCheck;

/// <summary>Testy samych narzędzi na stałych próbkach kodu (niezależnie od edycji Start na żywo).</summary>
public sealed class S13ToolsTest : IDisposable
{
    private readonly string _dir = Directory.CreateTempSubdirectory("s13-").FullName;

    public void Dispose() => Directory.Delete(_dir, true);

    [Fact]
    public void ProbeFindsTwoConceptsInOneClass()
    {
        var file = Path.Combine(_dir, "Mixed.cs");
        File.WriteAllText(file, """
            public sealed class Mixed
            {
                private readonly int _price;
                private readonly string _table;

                public Mixed(int price, string table)
                {
                    _price = price;
                    _table = table;
                }

                public int Total(int seats)
                {
                    return _price * seats + Fee();
                }

                private static int Fee()
                {
                    return 2;
                }

                public string Row(string title)
                {
                    return _table + ";" + title;
                }
            }
            """);
        Assert.Equal(new CohesionProbe.Result(2, ["Fee, Total", "Row"]), new CohesionProbe().Analyze(file));
    }

    [Fact]
    public void BoundaryRuleReportsForbiddenUsingsIncludingStatic()
    {
        File.WriteAllText(Path.Combine(_dir, "Policy.cs"), """
            using static App.Adapter.Db;
            using System.Data.Common;
            using System.Collections.Generic;

            namespace App.Domain;
            """);
        Assert.Equal(["Policy.cs: App.Adapter.Db", "Policy.cs: System.Data.Common"],
            new BoundaryRule("System.Data.", ".Adapter").Violations(_dir));
    }
}
