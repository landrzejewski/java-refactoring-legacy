using System.Reflection;
using Training.Module5.Mediator;
using Training.Module5.Mediator.After;
using Training.Module5.Mediator.Before;
using ApproverPicker = Training.Module5.Mediator.After.ApproverPicker;
using PublishButton = Training.Module5.Mediator.After.PublishButton;
using StatusLabel = Training.Module5.Mediator.After.StatusLabel;
using VersionField = Training.Module5.Mediator.After.VersionField;

namespace Training.Module5.Tests;

public sealed class MediatorEquivalenceTest
{
    private static readonly IReadOnlyList<string> Events =
    [
        "choose:early",
        "type:1.2",
        "type:1.2.0",
        "choose:anna",
        "type:oops",
        "click",
        "type:1.3.0",
        "choose:bob",
        "click",
        "type:2.0.0",
        "choose:carol",
        "click"
    ];

    [Fact]
    public void SameEventsLeadToTheSameFormStates()
    {
        var before = new LegacyReleaseForm();
        var after = new ReleaseFormMediator();
        var beforeStates = new List<FormState> { before.Snapshot() };
        var afterStates = new List<FormState> { after.Snapshot() };

        foreach (var @event in Events)
        {
            Apply(before, @event);
            Apply(after, @event);
            beforeStates.Add(before.Snapshot());
            afterStates.Add(after.Snapshot());
        }

        Assert.Equal(beforeStates, afterStates);
        Assert.Equal(
            new FormState("", true, false, "", false, "enter version"),
            afterStates[0]);
        Assert.Equal(
            new FormState("1.3.0", false, false, "bob", false, "published 1.3.0"),
            afterStates[^1]);
    }

    [Fact]
    public void ComponentsKnowOnlyTheMediator()
    {
        foreach (var component in new[]
                 {
                     typeof(VersionField), typeof(ApproverPicker), typeof(PublishButton), typeof(StatusLabel)
                 })
        {
            foreach (var field in component.GetFields(
                         BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public
                         | BindingFlags.NonPublic | BindingFlags.DeclaredOnly))
            {
                var type = field.FieldType;
                Assert.True(
                    type == typeof(IFormMediator) || type == typeof(string) || type.IsPrimitive,
                    component.Name + "." + field.Name);
            }
        }
    }

    private static void Apply(LegacyReleaseForm form, string @event)
    {
        if (@event.StartsWith("type:", StringComparison.Ordinal))
        {
            form.VersionField.Type(@event[5..]);
        }
        else if (@event.StartsWith("choose:", StringComparison.Ordinal))
        {
            form.ApproverPicker.Choose(@event[7..]);
        }
        else
        {
            form.PublishButton.Click();
        }
    }

    private static void Apply(ReleaseFormMediator form, string @event)
    {
        if (@event.StartsWith("type:", StringComparison.Ordinal))
        {
            form.VersionField.Type(@event[5..]);
        }
        else if (@event.StartsWith("choose:", StringComparison.Ordinal))
        {
            form.ApproverPicker.Choose(@event[7..]);
        }
        else
        {
            form.PublishButton.Click();
        }
    }
}
