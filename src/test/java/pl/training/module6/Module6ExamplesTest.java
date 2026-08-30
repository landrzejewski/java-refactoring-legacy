package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

final class Module6ExamplesTest {
    @Test
    void runsAllModuleExamples() {
        assertDoesNotThrow(() -> Module6Examples.main(new String[0]));
    }
}
