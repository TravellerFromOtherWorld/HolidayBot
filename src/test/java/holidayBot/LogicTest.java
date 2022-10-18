package holidayBot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicTest {
    Logic testLogic = new Logic();

    @Test
    void cantDetect() {
        assertFalse(testLogic.cantDetect("help"));
        assertFalse(testLogic.cantDetect("Help"));
        assertFalse(testLogic.cantDetect("пока"));
        assertFalse(testLogic.cantDetect("Пока"));
        assertTrue(testLogic.cantDetect("hepl"));
    }

    @Test
    void work() {
        testLogic.cantDetect("help");
        assertFalse(testLogic.work());
        testLogic.cantDetect("Help");
        assertFalse(testLogic.work());
        testLogic.cantDetect("пока");
        assertTrue(testLogic.work());
        testLogic.cantDetect("Пока");
        assertTrue(testLogic.work());
    }
}