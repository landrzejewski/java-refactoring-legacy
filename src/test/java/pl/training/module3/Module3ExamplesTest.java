package pl.training.module3;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

final class Module3ExamplesTest {
    @Test
    void runsAllModuleExamples() {
        assertDoesNotThrow(() -> Module3Examples.main(new String[0]));
    }
}
