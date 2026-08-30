package pl.training.module5.extractsubclass;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pl.training.module5.extractsubclass.after.DeliveryJob;

final class DeliveryJobEquivalenceTest {
    private static final Instant SCHEDULED_AT =
            Instant.parse("2030-06-15T10:15:30Z");

    @Test
    void immediateJobIsSentAtTheRequestedTime() {
        Instant now = Instant.parse("2029-01-01T00:00:00Z");
        var before = pl.training.module5.extractsubclass.before.DeliveryJob
                .immediate();
        var after = DeliveryJob.immediate();

        assertAll(
                () -> assertEquals("SENT", before.dispatchAt(now)),
                () -> assertEquals("SENT", after.dispatchAt(now)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scheduledDispatchScenarios")
    void scheduledJobPreservesItsObservableResult(
            String scenario,
            Instant now,
            String expectedResult) {
        var before = pl.training.module5.extractsubclass.before.DeliveryJob
                .scheduled(SCHEDULED_AT);
        var after = DeliveryJob.scheduled(SCHEDULED_AT);

        assertAll(
                () -> assertEquals(expectedResult, before.dispatchAt(now)),
                () -> assertEquals(expectedResult, after.dispatchAt(now)),
                () -> assertEquals(
                        before.dispatchAt(now),
                        after.dispatchAt(now)));
    }

    private static Stream<Arguments> scheduledDispatchScenarios() {
        return Stream.of(
                Arguments.of(
                        "before the scheduled time",
                        SCHEDULED_AT.minusSeconds(1),
                        "WAITING_UNTIL " + SCHEDULED_AT),
                Arguments.of(
                        "at the scheduled time",
                        SCHEDULED_AT,
                        "SENT"),
                Arguments.of(
                        "after the scheduled time",
                        SCHEDULED_AT.plusSeconds(1),
                        "SENT"));
    }
}
