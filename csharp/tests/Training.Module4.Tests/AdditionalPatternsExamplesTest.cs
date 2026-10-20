using Training.Module4.Behavioral.Iterator;
using Training.Module4.Behavioral.Memento.Accounts;
using Training.Module4.Behavioral.State;
using Training.Module4.Behavioral.Strategy.Movies;
using Training.Module4.Creational.AbstractFactory.Ftp;
using Training.Module4.Structural.Adapter;
using Training.Module4.Structural.Flyweight;
using MovieOrder = Training.Module4.Behavioral.Strategy.Movies.Order;
using StateOrder = Training.Module4.Behavioral.State.Order;

namespace Training.Module4.Tests;

[Collection(ConsoleCollection.Name)]
public sealed class AdditionalPatternsExamplesTest
{
    [Fact]
    public void ConvertsTemperatureDifferencesWithoutAnAbsoluteScaleOffset()
    {
        var controller = new RecordingAirConditioningController();
        var adapter = new TemperatureControllerAdapter(controller);

        adapter.TemperatureUp(10);
        adapter.TemperatureDown(10);

        Assert.Equal([18.0, -18.0], controller.Deltas);
    }

    [Fact]
    public void DoesNotSubtractAChargeDuringTheFreeRentalPeriod()
    {
        Assert.Equal(2.0,
                new MovieOrder(MovieType.REGULAR).GetTotalValue(1));
        Assert.Equal(1.5,
                new MovieOrder(MovieType.CHILDREN).GetTotalValue(1));
        Assert.Equal(3.0,
                new MovieOrder(MovieType.NEW_RELEASE).GetTotalValue(1));
        Assert.Throws<ArgumentException>(
                () => new MovieOrder(MovieType.REGULAR).GetTotalValue(-1));
    }

    [Fact]
    public void UsesTheRegisteredFtpControlPort()
    {
        Assert.Equal(21, new FtpConnection().Port);
    }

    [Fact]
    public void RestoresTheBalanceRecordedInTheMemento()
    {
        var account = new Account(Guid.NewGuid());
        var memento = account.CreateMemento();

        account.Deposit(10m);
        Assert.Equal(10m, account.Balance);

        account.RestoreMemento(memento);
        Assert.Equal(decimal.Zero, account.Balance);
    }

    [Fact]
    public void SharesOneFlyweightInstancePerIntrinsicState()
    {
        Assert.Same(
                TreeFactory.GetTreeType("Oak", "green"),
                TreeFactory.GetTreeType("Oak", "green"));
    }

    [Fact]
    public void OrderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions()
    {
        var order = new StateOrder();
        Assert.Same(OrderStatus.NEW, order.State);
        order.Pay();
        Assert.Same(OrderStatus.PAID, order.State);
        order.Ship();
        Assert.Same(OrderStatus.SHIPPED, order.State);
        Assert.Throws<InvalidOperationException>(order.Cancel);
        Assert.Same(OrderStatus.SHIPPED, order.State);

        var cancelled = new StateOrder();
        cancelled.Cancel();
        Assert.Same(OrderStatus.CANCELLED, cancelled.State);
        Assert.Throws<InvalidOperationException>(cancelled.Pay);
    }

    [Fact]
    public void FunctionalStateDemoAlternatesTransitions()
    {
        Assert.Equal(
                [
                    "Locked → unlocking",
                    "Unlocked → locking",
                    "Locked → unlocking",
                ],
                OutputOf(Fn.FunctionalStyleState.Run));
    }

    [Fact]
    public void IteratorDemoWalksThePlaylistForwardAndBackward()
    {
        Assert.Equal(
                [
                    "Playing: Intro (90s)",
                    "Playing: Main Theme (225s)",
                    "Playing: Outro (130s)",
                    "Reversed: Outro",
                    "Reversed: Main Theme",
                    "Reversed: Intro",
                ],
                OutputOf(Behavioral.Iterator.Application.Run));
    }

    [Fact]
    public void PlaylistIteratorIsExhaustedAfterTheLastSong()
    {
        var playlist = new Playlist();
        playlist.Add(new Song("Only", 60));
        using var iterator = playlist.GetEnumerator();

        Assert.True(iterator.MoveNext());
        Assert.Equal(new Song("Only", 60), iterator.Current);
        Assert.False(iterator.MoveNext());
        Assert.Throws<InvalidOperationException>(() => iterator.Current);
        using var reversed = new Playlist().Reversed().GetEnumerator();
        Assert.False(reversed.MoveNext());
    }

    [Fact]
    public void BridgeCombinesEveryMessageKindWithEverySender()
    {
        Assert.Equal(
                [
                    "Email to anna: Server restarted",
                    "SMS to anna: Server restarted",
                    "Email to anna: [URGENT] SERVER RESTARTED",
                    "SMS to anna: [URGENT] SERVER RESTARTED",
                ],
                OutputOf(Structural.Bridge.Application.Run));
    }

    [Fact]
    public void MediatorDeliversMessagesToEveryoneExceptTheSender()
    {
        Assert.Equal(
                [
                    "Alice sends: Hello everyone",
                    "Bob received from Alice: Hello everyone",
                    "Carol received from Alice: Hello everyone",
                    "Bob sends: Hi Alice",
                    "Alice received from Bob: Hi Alice",
                    "Carol received from Bob: Hi Alice",
                ],
                OutputOf(Behavioral.Mediator.Application.Run));
    }

    private static List<string> OutputOf(Action action)
    {
        var original = Console.Out;
        using var output = new StringWriter();
        try
        {
            Console.SetOut(output);
            action();
        }
        finally
        {
            Console.SetOut(original);
        }
        return output.ToString().Split(Environment.NewLine).SkipLast(1).ToList();
    }

    private sealed class RecordingAirConditioningController : AirConditioningController
    {
        public List<double> Deltas { get; } = [];

        public override void ChangeTemperature(double deltaInFahrenheit)
        {
            Deltas.Add(deltaInFahrenheit);
        }
    }
}
