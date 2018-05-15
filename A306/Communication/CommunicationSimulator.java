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

        String encryptedMessage = clientSimulator(keysReceiver, keysSender);

        // Add nodeSimulator.
    }

    // Simulates a client and return encrypted message.
    private static String clientSimulator(KeyPairGenerator receiverKeys, KeyPairGenerator senderKeys)
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // Receiver keys
        RSAKey receiverPrivate = receiverKeys.getPrivateKey();
        RSAKey receiverPublic = receiverKeys.getPublicKey();

        // Sender keys
        RSAKey senderPrivate = senderKeys.getPrivateKey();
        RSAKey senderPublic = senderKeys.getPublicKey();

        System.out.println("Public key of receiver:\nn: " + receiverPublic.getRSAMod() + "\ne : " + receiverPublic.getExponent());
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
    private static String prepareMessage(String message, RSAKey )
    {
        try
        {
            // Encrypting message.
            RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, new byte[]{1, 2}, receiverKey.getPublicKey(), receiverKey.getPublicE());
            System.out.println("\nEncrypted message: " + showEncrypted(encrypt.getEncryptedMessage()) + "\n");

            String hashedMessage = StringUtil.applySha256(showEncrypted(encrypt.getEncryptedMessage()) + " : " +
                    senderKey.getPublicKey() + "-" + senderKey.getPublicE() + " : " + receiverKey.getPublicKey() + "-" + receiverKey.getPublicE());

            // Class not fixed.
            // Decryption of hashedMessage using the private key from the sender.
            RSAOAEPDecrypt decryption = new RSAOAEPDecrypt(hashedMessage.getBytes(), new byte[]{1, 2}, senderKey.getPublicKey(), senderKey.getPrivateKey());
            String decryptedMessage = showDecrypted(decryption.getDecryptedMessage());
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
}
