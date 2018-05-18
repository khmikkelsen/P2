package Communication;

import RSA.*;
import robin.StringUtil;
import robin.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Arrays;

public class CommunicationSimulator
{
    private static final char KEYSEPERATOR = '_';

    // Entering receivers public key as address.
    public static int[] receiveKey(BufferedReader reader)
    {
        String key;

        try
        {
            key = reader.readLine();

            // Checking validity of entered key pair.
            if (!isKeyValid(key))
                throw new IOException();

            else
            {
                return new int[]{Integer.parseInt(getNumber(key, 0, KEYSEPERATOR)), Integer.parseInt(getNumber(key, KEYSEPERATOR))};
            }
        }

        catch (IOException e)
        {
            return new int[]{0, 0};
        }
    }

    // Checks if entered key pair is valid.
    public static boolean isKeyValid(String key)
    {
        char[] keyArray = key.toCharArray();
        int amountOfDash = 0;

        if (keyArray[0] == '-' || keyArray[keyArray.length - 1] == KEYSEPERATOR)
            return false;

        // Checking all characters.
        for (int i = 0; i < keyArray.length; i++)
        {
            if (((int) keyArray[i] < 48 || (int) keyArray[i] > 57) && keyArray[i] != KEYSEPERATOR)
                return false;

            if (keyArray[i] == KEYSEPERATOR)
                amountOfDash++;

            if (amountOfDash > 1)
                return false;
        }

        if (amountOfDash == 1)
            return true;

        else
            return false;
    }

    // Gets a number from a String.
    private static String getNumber(String text, char startIndex)
    {
        char[] number = text.toCharArray();
        String returnNumber = "";
        boolean indexFound = false;

        for (int i = 0; i < number.length; i++)
        {
            if (number[i] == startIndex)
            {
                indexFound = true;
                continue;
            }

            if (indexFound)
            {
                returnNumber = returnNumber + number[i];
            }
        }

        if (returnNumber.length() > 0)
            return returnNumber;

        else
            return "0";
    }

    private static String getNumber(String text, int startIndex)
    {
        char[] number = text.toCharArray();
        String returnNumber = "";

        for (int i = 0; i < number.length; i++)
        {
            if (i >= startIndex)
                returnNumber = returnNumber + number[i];
        }

        if (returnNumber.length() > 0)
            return returnNumber;

        else
            return "0";
    }

    // Gets a number from a String searching backwards.
    private static String getNumberBackwards(String text, char startIndex)
    {
        char[] number = text.toCharArray();

        for (int i = number.length - 1; i >= 0; i--)
        {
            if (number[i] == startIndex)
            {
                return getNumber(text, i);
            }
        }

        return null;
    }

    // Copy of previous getNumber, but using int as index.
    private static String getNumber(String text, int startIndex, char endIndex)
    {
        char[] number = text.toCharArray();
        String returnNumber = "";

        for (int i = startIndex; i < number.length && number[i] != endIndex; i++)
        {
            returnNumber = returnNumber + number[i];
        }

        if (returnNumber.length() > 0)
            return returnNumber;

        else
            return "0";
    }

    // Main method
    public static void main(String[] args)
    {
        // Verification test.
        /*final String message = "Hej";

        try
        {
            KeyPairGenerator senderKey = new KeyPairGenerator(2048);
            KeyPairGenerator receiverKey = new KeyPairGenerator(2048);

            Message m = new Message(message, senderKey.getPublicKey(), receiverKey.getPublicKey());

            // Signing and verifying.
            RSAOAEPSign signature = new RSAOAEPSign(m.calculateHash(), senderKey.getPrivateKey());
            RSAOAEPVerify verify = new RSAOAEPVerify(signature.getSignature(), m.calculateHash().getBytes(), senderKey.getPublicKey());

            System.out.println("Verified.");
        }

        catch (IOException | BadVerificationException e)
        {
            System.out.println("Not verified.");
        }*/

        simulateCommunication();
    }

    // Simulates a client-node communication.
    public static void simulateCommunication()
    {
        try
        {
            KeyPairGenerator keysReceiver = new KeyPairGenerator(2048);
            KeyPairGenerator keysSender = new KeyPairGenerator(2048);
            nodeSimulator(clientSimulator(keysReceiver, keysSender));
        }

        catch (IOException e)
        {
            System.out.println("Keys could not be generated.");
        }
    }

    // Simulates a client and return encrypted message.
    public static String clientSimulator(KeyPairGenerator receiverKeys, KeyPairGenerator senderKeys)
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Public key of receiver:\nn: " + receiverKeys.getPublicKey().getModulus() + "\ne : " + receiverKeys.getPublicKey().getExponent());
        System.out.print("\nMessage: ");

        try
        {
            Message m = new Message(input.readLine(), senderKeys.getPublicKey(), receiverKeys.getPublicKey());

            // Start preparing message to be send.
            return prepareMessage(m.calculateHash(), receiverKeys, senderKeys);
        }

        catch (IOException e)
        {
            return e.getCause().toString();
        }
    }

    // Prepares message to be send to node.
    private static String prepareMessage(String message, KeyPairGenerator receiverKeys, KeyPairGenerator senderKeys)
    {
        try
        {
            // Encrypting message.
            RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, new byte[]{1, 2}, receiverKeys.getPublicKey());
            System.out.println("\nEncrypted message: " + bytesToString(encrypt.getEncryptedMessage()) + "\n");

            String encryptedNodeMessage = bytesToString(encrypt.getEncryptedMessage()) + " : " +
                    senderKeys.getPublicKey().getModulus() + KEYSEPERATOR + senderKeys.getPublicKey().getExponent() + " : " +
                    receiverKeys.getPublicKey().getModulus() + KEYSEPERATOR + receiverKeys.getPublicKey().getExponent();

            // Singing message.
            RSAOAEPSign signature = new RSAOAEPSign(encryptedNodeMessage, senderKeys.getPrivateKey());

            String preparedMessage = encryptedNodeMessage  + " : " + bytesToString(signature.getSignature());
            System.out.println("Prepared message with signature: " + preparedMessage);

            return preparedMessage;
        }

        catch (IOException e)
        {
            return "IOException.";
        }
    }

    // Simulates a node.
    public static void nodeSimulator(String message)
    {
        System.out.println("\n\nNode:\n");

        try
        {
            RSAOAEPVerify verifySignature = new RSAOAEPVerify(stringToByte(getSignature(message)), messageNoSignature(message).getBytes(), new RSAKey(getSenderN(message), getSenderE(message)));
            System.out.println("Message verified.");
        }

        catch (IOException | BadVerificationException e)
        {
            System.out.println("Message not verified.");
            e.printStackTrace();
        }
    }

    // Gets the sender key N.
    public static BigInteger getSenderN(String message)
    {
        String number = getNumber(message, getCharStart(message, ':', 2), '_');
        return new BigInteger(number);
    }

    // Gets start index of N in sender's key pair.
    private static int getCharStart(String message, char c, int range)
    {
        char[] messageArray = message.toCharArray();

        for (int i = 0; i < message.length(); i++)
        {
            if (messageArray[i] == c)
                return i + range;
        }

        return 0;
    }

    // Gets the sender key E.
    public static BigInteger getSenderE(String message)
    {
        String number = getNumber(message, getCharStart(message, KEYSEPERATOR, 1), ' ');
        return new BigInteger(number);
    }

    // Returns the signature of a message.
    public static String getSignature(String message)
    {
        return getNumberBackwards(message, '[');
    }

    // Returns message without signature.
    public static String messageNoSignature(String message)
    {
        char[] messageArray = message.toCharArray();
        String result = "";

        for (int i = 0; i < messageArray.length; i++)
        {
            if (messageArray[i] == '[' && i > 0)
                break;

            result = result + messageArray[i];
        }

        return removeEndSeparator(result);
    }

    // Removes separator at end of String.
    private static String removeEndSeparator(String text)
    {
        String result = "";
        char[] textCopy = text.toCharArray();

        for (int i = 0; i < text.length() - 3; i++)
        {
            result = result + textCopy[i];
        }

        return result;
    }

    // Converts byte array into a String.
    public static String bytesToString(byte[] array)
    {
        String result = "[";

        for (int i = 0; i < array.length; i++)
        {
            result = result + String.valueOf(array[i]) + ",";
        }

        return result + "]";
    }

    // Converts byte array String to byte array.
    public static byte[] stringToByte(String array)
    {
        byte[] result = new byte[stringByteArraySize(array)];
        char[] charArray = array.toCharArray();

        for (int i = 0, j = 0; i < array.length() - 2; i++)
        {
            if (charArray[i] == '[' || charArray[i] == ',')
            {
                result[j] = (byte) Integer.parseInt(getNumber(array, i + 1, ','));
                j++;
            }
        }

        return result;
    }

    // Returns amount of commas in String byte array.
    public static int stringByteArraySize(String array)
    {
        int commas = 0;

        for (int i = 0; i < array.length(); i++)
        {
            if (array.charAt(i) == ',')
                commas++;
        }

        return commas;
    }
}
