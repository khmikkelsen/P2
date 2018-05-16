package Communication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommunicationSimulatorTest
{
    @Test
    void testValidKey()
    {
        assertEquals(CommunicationSimulator.isKeyValid("127-44"), true);
    }
}