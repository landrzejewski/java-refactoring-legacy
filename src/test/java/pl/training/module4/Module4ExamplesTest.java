package pl.training.module4;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

final class Module4ExamplesTest {
    @Test
    void runsAllModuleExamples() {
        assertDoesNotThrow(() -> Module4Examples.main(new String[0]));
    }
}
