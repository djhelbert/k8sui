package org.k8sui.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyValidatorTest {
    @Test
    public void testValidate() {
        assertTrue(KeyValidator.validName("yes"));
        assertTrue(KeyValidator.validName("ye-.s"));
        assertTrue(KeyValidator.validName("yes"));
        assertTrue(KeyValidator.validName("yes123yes"));
        assertTrue(KeyValidator.validName("Notvalid"));
        assertTrue(KeyValidator.validName("IS_VALID"));
        assertFalse(KeyValidator.validName("y"));

        assertFalse(KeyValidator.validName("not valid"));
        assertFalse(KeyValidator.validName("$no"));
        assertFalse(KeyValidator.validName("!!"));
    }
}
