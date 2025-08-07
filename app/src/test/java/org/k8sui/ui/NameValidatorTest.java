package org.k8sui.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameValidatorTest {
    @Test
    public void testValidate() {
        assertTrue(NameValidator.validName("yes"));
        assertTrue(NameValidator.validName("ye-.s"));
        assertTrue(NameValidator.validName("yes"));
        assertTrue(NameValidator.validName("yes123yes"));

        assertFalse(NameValidator.validName("not valid"));
        assertFalse(NameValidator.validName("n"));
        assertFalse(NameValidator.validName("-no"));
        assertFalse(NameValidator.validName("-no."));
        assertFalse(NameValidator.validName("Notvalid"));
    }
}
