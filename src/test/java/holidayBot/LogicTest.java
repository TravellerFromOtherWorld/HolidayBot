/*
package holidayBot;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicTest {
    Logic testLogic = new Logic();
    MessageFromBot testMessage = new MessageFromBot();

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
        testMessage = testLogic.work();
        assertFalse(testMessage.toExit());
        testLogic.cantDetect("Help");
        testMessage = testLogic.work();
        assertFalse(testMessage.toExit());
        testLogic.cantDetect("пока");
        testMessage = testLogic.work();
        assertTrue(testMessage.toExit());
        testLogic.cantDetect("Пока");
        testMessage = testLogic.work();
        assertTrue(testMessage.toExit());
    }
}

 */