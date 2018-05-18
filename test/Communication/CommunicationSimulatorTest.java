package Communication;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class CommunicationSimulatorTest
{
    private String messageExample = "[42, -51, 76, -9, 15, -42, -116, 38, 29, 88, -103, 39, 103, -78, -20, 21, -85, -101, -113, -28, -124, 59, 122, 44, 69, 81, -123, -36, -25, -32, 24, -1, 14, -76, 57, 96, 123, 28, -7, -15, 117, 34, -54, -34, -10, 63, 37, -66, -29, 7, -99, -101, 76, -96, -46, 47, -115, 57, -43, -93, 61, -126, -22, 72, 86, 101, 92, -38, 59, -97, 47, -1, -49, 112, 118, 107, -107, 18, -72, -86, 111, -48, 11, 16, 111, -40, 114, 2, -121, -53, -93, 35, 51, -95, -60, 18, -6, -90, 124, -128, 96, 70, -16, 106, 100, 107, 1, -124, -11, -82, -67, -31, -48, 62, -127, 59, -122, 50, -122, 38, -115, 106, -25, -71, 1, -112, -104, -4, -110, -12, -99, -108, 13, 84, 63, -85, -17, 74, 107, 114, -65, 14, -41, -35, -11, 2, -84, -99, -96, -26, -23, 118, 28, -74, 46, -10, 72, -99, -32, -24, -98, -104, -29, 124, -72, 25, -41, -17, -2, -34, -57, -117, -65, -14, 126, 62, -57, 33, -55, -6, -54, 34, -127, 34, -15, 105, 68, -98, 94, 90, -119, 67, 120, -99, -63, -59, 9, 71, 3, -18, -70, -83, 80, 18, -42, -114, 95, 13, 117, -73, -3, -54, 81, -113, 123, -111, -4, -113, -45, -18, -81, -116, -54, 31, 75, -45, -67, 27, 44, 22, 48, -7, -105, 85, -55, -20, 77, 98, -79, 125, -114, -54, 27, 18, -4, 35, 70, -71, 103, -44, -27, -111, -25, 12, -3, 108] : 16486091657655017584474310567988079588096087479730002640153224676346463508430122397206397335698999277000962626391517297388831670062925173066809185872832146657847799230398180821362758222597806469962146031671256451970286557210652137088536669882068913460415174526274517519848882156775671731270173730708635649278063737341420631502761875650033869490401048481989258941149446225414892021763262970187715014013149796658743424121392687925033027879177696303779389990065641174499260339213050456087998284708585641020976627147672669586421980745136826196468679586210248808378475548910749408256961769249353746095555762672918876666857_65537 : 21684645203737686997142664927056934949459526568993306341924774899658571376432298707232795578999233242103433306888982851405313122871747066870566817655265682761155731381632809425689603925939121183601482874354350783625791366610418416540664182960641056570225794699217952406130244456303655179344867882147252764735396348808570265416433545222383063839501449846098751478691733138890440516900460284239870369044463339669138721209947504149031363091970860351017126554898224663129712796252066606819300334841780263033712861137562618813344343337066214289018475532139940733122046070434747520563493789967354201661335742465925137410233_65537 : [78, -90, 62, -80, -1, -66, -38, 125, -97, 87, -120, -8, 4, 19, -126, 0, -93, 70, -56, 10, 38, 40, -14, -16, 101, -2, 91, -44, 76, -33, -125, 101, -58, -121, 95, 29, -97, -124, -51, -50, 66, 20, -62, 122, 1, -118, 58, -121, 3, 9, 82, -23, 60, -128, 41, 20, -91, 3, 108, 76, -36, 101, -26, 24, 123, 18, 126, 126, 127, 106, -103, 122, 24, 111, -87, 6, 95, -125, -113, 110, 112, -92, 110, 98, 64, -100, 2, 60, 95, 96, 10, 85, -93, 115, -65, 104, 92, 85, 0, -123, -121, -47, -32, 4, 57, 69, -64, 42, -5, -79, 25, -113, -18, 63, 120, -95, -29, -124, -42, -122, -18, 60, -59, 82, 33, -7, -12, 35, -12, -81, -85, 98, -37, 9, -9, -10, 104, -92, -106, 32, -69, 54, 46, 49, 96, 121, 92, -47, -83, -121, 55, 92, -124, -114, -57, -75, 88, -113, 22, -28, 41, -47, 38, -23, 86, 116, 79, -56, -119, -66, -73, 29, 108, -41, -29, 47, 125, -85, -62, -96, 106, 47, -56, -32, -71, 86, 31, -120, 126, 7, -109, 46, 105, 86, 49, -50, -104, -41, -80, 31, 12, 77, 26, -45, -53, -24, 117, 36, -47, -112, 54, -103, 71, -96, -43, -96, 105, 81, 58, 6, -27, -56, -42, 117, 17, -84, -71, 125, -69, -62, -117, 37, -97, 28, 37, 54, 85, -57, 43, -44, -22, 80, -39, -50, 67, -35, 119, -119, -56, -47, 64, -79, 102, -1, -102, 98]";

    // Tests, if an entered key as receivers address is valid.
    @Test
    void testValidKey()
    {
        assertEquals(CommunicationSimulator.isKeyValid("127_44"), true);
    }

    // Tests the getter of key n of the sender.
    @Test
    void testGetN()
    {
        BigInteger n = new BigInteger("16486091657655017584474310567988079588096087479730002640153224676346463508430122397206397335698999277000962626391517297388831670062925173066809185872832146657847799230398180821362758222597806469962146031671256451970286557210652137088536669882068913460415174526274517519848882156775671731270173730708635649278063737341420631502761875650033869490401048481989258941149446225414892021763262970187715014013149796658743424121392687925033027879177696303779389990065641174499260339213050456087998284708585641020976627147672669586421980745136826196468679586210248808378475548910749408256961769249353746095555762672918876666857");
        assertEquals(CommunicationSimulator.getSenderN(messageExample), n);
    }

    // Tests the getter of key e of the sender.
    @Test
    void testGetE()
    {
        BigInteger e = new BigInteger("65537");
        assertEquals(CommunicationSimulator.getSenderE(this.messageExample), e);
    }

    // Test the getter of the signature in a message.
    @Test
    void testGetSignature()
    {
        String signature = "[78, -90, 62, -80, -1, -66, -38, 125, -97, 87, -120, -8, 4, 19, -126, 0, -93, 70, -56, 10, 38, 40, -14, -16, 101, -2, 91, -44, 76, -33, -125, 101, -58, -121, 95, 29, -97, -124, -51, -50, 66, 20, -62, 122, 1, -118, 58, -121, 3, 9, 82, -23, 60, -128, 41, 20, -91, 3, 108, 76, -36, 101, -26, 24, 123, 18, 126, 126, 127, 106, -103, 122, 24, 111, -87, 6, 95, -125, -113, 110, 112, -92, 110, 98, 64, -100, 2, 60, 95, 96, 10, 85, -93, 115, -65, 104, 92, 85, 0, -123, -121, -47, -32, 4, 57, 69, -64, 42, -5, -79, 25, -113, -18, 63, 120, -95, -29, -124, -42, -122, -18, 60, -59, 82, 33, -7, -12, 35, -12, -81, -85, 98, -37, 9, -9, -10, 104, -92, -106, 32, -69, 54, 46, 49, 96, 121, 92, -47, -83, -121, 55, 92, -124, -114, -57, -75, 88, -113, 22, -28, 41, -47, 38, -23, 86, 116, 79, -56, -119, -66, -73, 29, 108, -41, -29, 47, 125, -85, -62, -96, 106, 47, -56, -32, -71, 86, 31, -120, 126, 7, -109, 46, 105, 86, 49, -50, -104, -41, -80, 31, 12, 77, 26, -45, -53, -24, 117, 36, -47, -112, 54, -103, 71, -96, -43, -96, 105, 81, 58, 6, -27, -56, -42, 117, 17, -84, -71, 125, -69, -62, -117, 37, -97, 28, 37, 54, 85, -57, 43, -44, -22, 80, -39, -50, 67, -35, 119, -119, -56, -47, 64, -79, 102, -1, -102, 98]";
        assertEquals(CommunicationSimulator.getSignature(this.messageExample), signature);
    }

    @Test
    void testGetSignature2()
    {
        byte[] signature = "[78, -90, 62, -80, -1, -66, -38, 125, -97, 87, -120, -8, 4, 19, -126, 0, -93, 70, -56, 10, 38, 40, -14, -16, 101, -2, 91, -44, 76, -33, -125, 101, -58, -121, 95, 29, -97, -124, -51, -50, 66, 20, -62, 122, 1, -118, 58, -121, 3, 9, 82, -23, 60, -128, 41, 20, -91, 3, 108, 76, -36, 101, -26, 24, 123, 18, 126, 126, 127, 106, -103, 122, 24, 111, -87, 6, 95, -125, -113, 110, 112, -92, 110, 98, 64, -100, 2, 60, 95, 96, 10, 85, -93, 115, -65, 104, 92, 85, 0, -123, -121, -47, -32, 4, 57, 69, -64, 42, -5, -79, 25, -113, -18, 63, 120, -95, -29, -124, -42, -122, -18, 60, -59, 82, 33, -7, -12, 35, -12, -81, -85, 98, -37, 9, -9, -10, 104, -92, -106, 32, -69, 54, 46, 49, 96, 121, 92, -47, -83, -121, 55, 92, -124, -114, -57, -75, 88, -113, 22, -28, 41, -47, 38, -23, 86, 116, 79, -56, -119, -66, -73, 29, 108, -41, -29, 47, 125, -85, -62, -96, 106, 47, -56, -32, -71, 86, 31, -120, 126, 7, -109, 46, 105, 86, 49, -50, -104, -41, -80, 31, 12, 77, 26, -45, -53, -24, 117, 36, -47, -112, 54, -103, 71, -96, -43, -96, 105, 81, 58, 6, -27, -56, -42, 117, 17, -84, -71, 125, -69, -62, -117, 37, -97, 28, 37, 54, 85, -57, 43, -44, -22, 80, -39, -50, 67, -35, 119, -119, -56, -47, 64, -79, 102, -1, -102, 98]".getBytes();
        byte[] testSignature = CommunicationSimulator.getSignature(this.messageExample).getBytes();
        assertEquals(testSignature[0], signature[0]);
        assertEquals(testSignature[1], signature[1]);
    }

    // Tests a getter that returns a message without the signature.
    @Test
    void testGetMessageNoSignature()
    {
        String message = "[42, -51, 76, -9, 15, -42, -116, 38, 29, 88, -103, 39, 103, -78, -20, 21, -85, -101, -113, -28, -124, 59, 122, 44, 69, 81, -123, -36, -25, -32, 24, -1, 14, -76, 57, 96, 123, 28, -7, -15, 117, 34, -54, -34, -10, 63, 37, -66, -29, 7, -99, -101, 76, -96, -46, 47, -115, 57, -43, -93, 61, -126, -22, 72, 86, 101, 92, -38, 59, -97, 47, -1, -49, 112, 118, 107, -107, 18, -72, -86, 111, -48, 11, 16, 111, -40, 114, 2, -121, -53, -93, 35, 51, -95, -60, 18, -6, -90, 124, -128, 96, 70, -16, 106, 100, 107, 1, -124, -11, -82, -67, -31, -48, 62, -127, 59, -122, 50, -122, 38, -115, 106, -25, -71, 1, -112, -104, -4, -110, -12, -99, -108, 13, 84, 63, -85, -17, 74, 107, 114, -65, 14, -41, -35, -11, 2, -84, -99, -96, -26, -23, 118, 28, -74, 46, -10, 72, -99, -32, -24, -98, -104, -29, 124, -72, 25, -41, -17, -2, -34, -57, -117, -65, -14, 126, 62, -57, 33, -55, -6, -54, 34, -127, 34, -15, 105, 68, -98, 94, 90, -119, 67, 120, -99, -63, -59, 9, 71, 3, -18, -70, -83, 80, 18, -42, -114, 95, 13, 117, -73, -3, -54, 81, -113, 123, -111, -4, -113, -45, -18, -81, -116, -54, 31, 75, -45, -67, 27, 44, 22, 48, -7, -105, 85, -55, -20, 77, 98, -79, 125, -114, -54, 27, 18, -4, 35, 70, -71, 103, -44, -27, -111, -25, 12, -3, 108] : 16486091657655017584474310567988079588096087479730002640153224676346463508430122397206397335698999277000962626391517297388831670062925173066809185872832146657847799230398180821362758222597806469962146031671256451970286557210652137088536669882068913460415174526274517519848882156775671731270173730708635649278063737341420631502761875650033869490401048481989258941149446225414892021763262970187715014013149796658743424121392687925033027879177696303779389990065641174499260339213050456087998284708585641020976627147672669586421980745136826196468679586210248808378475548910749408256961769249353746095555762672918876666857_65537 : 21684645203737686997142664927056934949459526568993306341924774899658571376432298707232795578999233242103433306888982851405313122871747066870566817655265682761155731381632809425689603925939121183601482874354350783625791366610418416540664182960641056570225794699217952406130244456303655179344867882147252764735396348808570265416433545222383063839501449846098751478691733138890440516900460284239870369044463339669138721209947504149031363091970860351017126554898224663129712796252066606819300334841780263033712861137562618813344343337066214289018475532139940733122046070434747520563493789967354201661335742465925137410233_65537";
        assertEquals(CommunicationSimulator.messageNoSignature(this.messageExample), message);
    }

    // Tests method that converts a byte array to a String.
    @Test
    void testByteToString()
    {
        byte[] testArray = new byte[]{2, 4, 6};
        assertEquals(CommunicationSimulator.bytesToString(testArray), "[2,4,6,]");
    }

    // Tests method that return a byte array from a String.
    @Test
    void testStringToByte()
    {
        String array = "[2,40,-61,]";
        byte[] resultArray = CommunicationSimulator.stringToByte(array);

        assertEquals(resultArray[0], 2);
        assertEquals(resultArray[1], 40);
        assertEquals(resultArray[2], -61);
    }

    // Tests the method that returns the size of byte array formatted as a String.
    @Test
    void testStringByteArraySize()
    {
        String byteArray = "[11, 12, 13, 14, 15, -16, -17, -18, -19, -20,]";
        assertEquals(CommunicationSimulator.stringByteArraySize(byteArray), 10);
    }
}