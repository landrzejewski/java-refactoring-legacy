package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

final class Module5ExamplesTest {
    @Test
    void runsAllModuleExamples() {
        assertDoesNotThrow(() -> Module5Examples.main(new String[0]));
    }
}
