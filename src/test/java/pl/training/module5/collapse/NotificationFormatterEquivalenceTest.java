package pl.training.module5.collapse;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pl.training.module5.collapse.after.NotificationFormatter;

final class NotificationFormatterEquivalenceTest {
    @Test
    void collapsedFormatterPreservesValidation() {
        var before = new pl.training.module5.collapse.before
                .NotificationFormatter();
        var after = new pl.training.module5.collapse.after
                .NotificationFormatter();

        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> before.format(null, "message")),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> after.format(null, "message")),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> before.format("recipient", null)),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> after.format("recipient", null)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notifications")
    void collapsedFormatterPreservesTheFormattedNotification(
            String scenario,
            String recipient,
            String message,
            String expectedNotification) {
        var before = new pl.training.module5.collapse.before
                .NotificationFormatter();
        var after = new NotificationFormatter();

        assertAll(
                () -> assertEquals(
                        expectedNotification,
                        before.format(recipient, message)),
                () -> assertEquals(
                        expectedNotification,
                        after.format(recipient, message)),
                () -> assertEquals(
                        before.format(recipient, message),
                        after.format(recipient, message)));
    }

    private static Stream<Arguments> notifications() {
        return Stream.of(
                Arguments.of(
                        "operational notification",
                        "ops@example.com",
                        "Deployment finished",
                        "To: ops@example.com\nMessage: Deployment finished"),
                Arguments.of(
                        "Unicode content",
                        "zespół@example.pl",
                        "Zażółć gęślą jaźń",
                        "To: zespół@example.pl\nMessage: Zażółć gęślą jaźń"));
    }
}
