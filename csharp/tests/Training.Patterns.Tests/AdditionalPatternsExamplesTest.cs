using Training.Patterns.Behavioral.Memento.Accounts;
using Training.Patterns.Behavioral.State;
using Training.Patterns.Behavioral.Strategy.Movies;
using Training.Patterns.Creational.AbstractFactory.Ftp;
using Training.Patterns.Structural.Adapter;
using Training.Patterns.Structural.Flyweight;
using MovieOrder = Training.Patterns.Behavioral.Strategy.Movies.Order;
using StateOrder = Training.Patterns.Behavioral.State.Order;

namespace Training.Patterns.Tests;

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
    public void IteratorDemoHasDeterministicOrder()
    {
        Assert.Equal(
                ["1", "2", "3", "4", "5"],
                OutputOf(Behavioral.Iterator.Application.Run));
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
