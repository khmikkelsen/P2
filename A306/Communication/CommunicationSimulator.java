package Communication;

import RSA.*;
import robin.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Arrays;

public class CommunicationSimulator
{
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
                return new int[]{Integer.parseInt(getNumber(key, 0, '-')), Integer.parseInt(getNumber(key, '-'))};
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
        final char SEPERATOR = '_';

        if (keyArray[0] == '-' || keyArray[keyArray.length - 1] == SEPERATOR)
            return false;

        // Checking all characters.
        for (int i = 0; i < keyArray.length; i++)
        {
            if (((int) keyArray[i] < 48 || (int) keyArray[i] > 57) && keyArray[i] != SEPERATOR)
                return false;

            if (keyArray[i] == SEPERATOR)
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
        simulateCommunication();
    }

    // Simulates a client-node communication.
    public static void simulateCommunication()
    {
        KeyPairGenerator keysReceiver = new KeyPairGenerator(2048);
        KeyPairGenerator keysSender = new KeyPairGenerator(2048);

        nodeSimulator(clientSimulator(keysReceiver, keysSender));
    }

    // Simulates a client and return encrypted message.
    public static String clientSimulator(KeyPairGenerator receiverKeys, KeyPairGenerator senderKeys)
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // Sender keys
        RSAKey senderPrivate = senderKeys.getPrivateKey();
        RSAKey senderPublic = senderKeys.getPublicKey();

        System.out.println("Public key of receiver:\nn: " + receiverKeys.getPublicKey().getRSAMod() + "\ne : " + receiverKeys.getPublicKey().getExponent());
        System.out.print("\nMessage: ");

        try
        {
            String message = input.readLine();

            // Start preparing message to be send.
            return prepareMessage(message, receiverKeys, senderKeys);
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
            System.out.println("\nEncrypted message: " + showEncrypted(encrypt.getEncryptedMessage()) + "\n");

            String encryptedNodeMessage = showEncrypted(encrypt.getEncryptedMessage()) + " : " +
                    senderKeys.getPublicKey().getRSAMod() + "_" + senderKeys.getPublicKey().getExponent() + " : " +
                    receiverKeys.getPublicKey().getRSAMod() + "_" + receiverKeys.getPublicKey().getExponent();

            RSAOAEPSign signature = new RSAOAEPSign(encryptedNodeMessage, senderKeys.getPrivateKey());

            String preparedMessage = encryptedNodeMessage  + " : " + Arrays.toString(signature.getSignature());
            System.out.println("Prepared message with signature: " + preparedMessage);

            return preparedMessage;
        }

        catch (IOException e)
        {
            return "IOException.";
        }
    }

    // Returns a String of encrypted message.
    private static String showEncrypted(byte[] encrypted)
    {
        return Arrays.toString(encrypted);
    }

    // Returns a String of decrypted message.
    private static String showDecrypted(byte[] decrypted)
    {
        return new String(decrypted);
    }

    // Simulates a node.
    public static void nodeSimulator(String message)
    {
        System.out.println("\n\nNode:\n");

        try
        {
            RSAOAEPVerify verifySignature = new RSAOAEPVerify(getSignature(message).getBytes(), messageNoSignature(message).getBytes(), new RSAKey(getSenderN(message), getSenderE(message)));
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
        String number = getNumber(message, getCharStart(message, '_', 1), ' ');
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
        boolean isSignature = false;

        for (int i = 0; i < messageArray.length; i++)
        {
            if (messageArray[i] == '[' && i > 0)
                isSignature = true;

            if (!isSignature)
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
}
