using System.Globalization;
using Training.Workshop.M4.S06InlineMethod;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S06InlineMethod;

/// <summary>Test równoważności: kasa i internet - te same kwoty po każdym kroku. Podklasa jest częścią testu!</summary>
public sealed class S06EquivalenceTest
{
    internal static readonly Ticket ImaxEvening = new(3, new TimeOnly(20, 0));
    internal static readonly Ticket Morning3D = new(2, new TimeOnly(10, 0));
    internal static readonly Ticket Noon2D = new(1, new TimeOnly(12, 0));

    private static readonly Scene<Ticket, string> BoxOfficeScene = Scene.Variants<Ticket, string>()
        .Variant("start", t => Plain(new Training.Workshop.M4.S06InlineMethod.Start.TicketPricing().Total(t)))
        .Variant("step1", t => Plain(new Training.Workshop.M4.S06InlineMethod.Step1.TicketPricing().Total(t)))
        .Variant("step2", t => Plain(new Training.Workshop.M4.S06InlineMethod.Step2.TicketPricing().Total(t)))
        .Expect("kasa: IMAX wieczorem", ImaxEvening, "40.00")
        .Expect("kasa: 3D rano", Morning3D, "27.00")
        .Expect("kasa: 2D 12:00", Noon2D, "25.00");

    private static readonly Scene<Ticket, string> OnlineScene = Scene.Variants<Ticket, string>()
        .Variant("start", t => Plain(new Training.Workshop.M4.S06InlineMethod.Start.OnlineTicketPricing().Total(t)))
        .Variant("step1", t => Plain(new Training.Workshop.M4.S06InlineMethod.Step1.OnlineTicketPricing().Total(t)))
        .Variant("step2", t => Plain(new Training.Workshop.M4.S06InlineMethod.Step2.OnlineTicketPricing().Total(t)))
        .Expect("online: IMAX wieczorem", ImaxEvening, "42.00")
        .Expect("online: 3D rano", Morning3D, "29.00")
        .Expect("online: 2D 12:00", Noon2D, "27.00");

    public static TheoryData<string> BoxOfficeCases => BoxOfficeScene.Tests();

    public static TheoryData<string> OnlineCases => OnlineScene.Tests();

    [Theory]
    [MemberData(nameof(BoxOfficeCases))]
    public void BoxOfficeTotalsStayTheSame(string test) => BoxOfficeScene.Run(test);

    [Theory]
    [MemberData(nameof(OnlineCases))]
    public void OnlineTotalsStayTheSame(string test) => OnlineScene.Run(test);

    internal static string Plain(decimal amount) => amount.ToString(CultureInfo.InvariantCulture);
}
