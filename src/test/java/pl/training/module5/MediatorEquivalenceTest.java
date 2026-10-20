package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module5.mediator.FormState;
import pl.training.module5.mediator.after.ApproverPicker;
import pl.training.module5.mediator.after.FormMediator;
import pl.training.module5.mediator.after.PublishButton;
import pl.training.module5.mediator.after.ReleaseFormMediator;
import pl.training.module5.mediator.after.StatusLabel;
import pl.training.module5.mediator.after.VersionField;
import pl.training.module5.mediator.before.LegacyReleaseForm;

final class MediatorEquivalenceTest {
    private static final List<String> EVENTS = List.of(
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
            "click");

    @Test
    void sameEventsLeadToTheSameFormStates() {
        var before = new LegacyReleaseForm();
        var after = new ReleaseFormMediator();
        List<FormState> beforeStates = new ArrayList<>(List.of(before.snapshot()));
        List<FormState> afterStates = new ArrayList<>(List.of(after.snapshot()));

        for (String event : EVENTS) {
            apply(before, event);
            apply(after, event);
            beforeStates.add(before.snapshot());
            afterStates.add(after.snapshot());
        }

        assertEquals(beforeStates, afterStates);
        assertEquals(
                new FormState("", true, false, "", false, "enter version"),
                afterStates.getFirst());
        assertEquals(
                new FormState("1.3.0", false, false, "bob", false, "published 1.3.0"),
                afterStates.getLast());
    }

    @Test
    void componentsKnowOnlyTheMediator() {
        for (Class<?> component : List.of(
                VersionField.class, ApproverPicker.class, PublishButton.class, StatusLabel.class)) {
            for (Field field : component.getDeclaredFields()) {
                Class<?> type = field.getType();
                assertTrue(
                        type == FormMediator.class || type == String.class || type.isPrimitive(),
                        component.getSimpleName() + "." + field.getName());
            }
        }
    }

    private static void apply(LegacyReleaseForm form, String event) {
        if (event.startsWith("type:")) {
            form.versionField().type(event.substring(5));
        } else if (event.startsWith("choose:")) {
            form.approverPicker().choose(event.substring(7));
        } else {
            form.publishButton().click();
        }
    }

    private static void apply(ReleaseFormMediator form, String event) {
        if (event.startsWith("type:")) {
            form.versionField().type(event.substring(5));
        } else if (event.startsWith("choose:")) {
            form.approverPicker().choose(event.substring(7));
        } else {
            form.publishButton().click();
        }
    }
}
