package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

final class NotificationHierarchyEquivalenceTest {
    @Test
    void allStagesPreserveEmailContractForEveryLegacyFlagAndStatus() {
        for (boolean deliveryReceipt : List.of(false, true)) {
            for (boolean successful : List.of(false, true)) {
                List<String> results = List.of(
                        new pl.training.module5.stage0.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage1.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage2.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage3.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful));
                String expected = "msg-1|OPS|Deployment ready|EMAIL|"
                        + (successful ? "SENT" : "FAILED");

                assertAllEqual(expected, results);
            }
        }
    }

    @Test
    void allStagesPreserveSmsContractForEveryReceiptAndStatus() {
        for (boolean deliveryReceipt : List.of(false, true)) {
            for (boolean successful : List.of(false, true)) {
                List<String> results = List.of(
                        new pl.training.module5.stage0.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage1.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage2.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage3.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful));
                String expected = "msg-2|OPS|Deploy now|SMS|"
                        + (successful ? "SENT" : "FAILED")
                        + (successful && deliveryReceipt ? "|RECEIPT" : "");

                assertAllEqual(expected, results);
            }
        }
    }

    @Test
    void allStagesPreserveValidationTypes() {
        assertExceptionType(
                IllegalArgumentException.class,
                List.of(
                        () -> new pl.training.module5.stage0.EmailNotification(
                                " ", "ops", "body", false),
                        () -> new pl.training.module5.stage1.EmailNotification(
                                " ", "ops", "body", false),
                        () -> new pl.training.module5.stage2.EmailNotification(
                                " ", "ops", "body", false),
                        () -> new pl.training.module5.stage3.EmailNotification(
                                " ", "ops", "body", false)));
        assertExceptionType(
                NullPointerException.class,
                List.of(
                        () -> new pl.training.module5.stage0.SmsNotification(
                                "msg", null, "body", false),
                        () -> new pl.training.module5.stage1.SmsNotification(
                                "msg", null, "body", false),
                        () -> new pl.training.module5.stage2.SmsNotification(
                                "msg", null, "body", false),
                        () -> new pl.training.module5.stage3.SmsNotification(
                                "msg", null, "body", false)));
    }

    private static void assertAllEqual(
            String expected,
            List<String> results) {
        results.forEach(result -> assertEquals(expected, result));
    }

    private static <T extends Throwable> void assertExceptionType(
            Class<T> expectedType,
            List<Executable> scenarios) {
        scenarios.forEach(scenario -> assertEquals(
                expectedType,
                assertThrows(expectedType, scenario).getClass()));
    }
}
