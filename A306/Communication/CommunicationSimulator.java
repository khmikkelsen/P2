package Communication;

import RSA.KeyPairGenerator;
import RSA.RSAKey;
import RSA.RSAOAEPDecrypt;
import RSA.RSAOAEPEncrypt;
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

        if (keyArray[0] == '-' || keyArray[keyArray.length - 1] == '-')
            return false;

        // Checking all characters.
        for (int i = 0; i < keyArray.length; i++)
        {
            if (((int) keyArray[i] < 48 || (int) keyArray[i] > 57) && keyArray[i] != '-')
                return false;

            if (keyArray[i] == '-')
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
        int j = 0;

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
                j++;
            }
        }

        if (returnNumber.length() > 0)
            return returnNumber;

        else
            return "0";
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
                    senderKeys.getPublicKey().getRSAMod() + "-" + senderKeys.getPublicKey().getExponent() + " : " +
                    receiverKeys.getPublicKey().getRSAMod() + "-" + receiverKeys.getPublicKey().getExponent();

            // Hashing encrypted message.
            String hashedMessage = StringUtil.applySha256(encryptedNodeMessage );

            // Decryption of hashedMessage using the private key from the sender.
            RSAOAEPDecrypt decryption = new RSAOAEPDecrypt(hashedMessage.getBytes(), new byte[]{1, 2}, senderKeys.getPrivateKey());
            String decryptedHashedMessage = showDecrypted(decryption.getDecryptedMessage());

            String preparedMessage = encryptedNodeMessage  + " : " + decryptedHashedMessage;
            System.out.println(preparedMessage);

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
        System.out.println("\nNode:\n");
    }
}
