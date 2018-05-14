package Communication;

import RSA.KeyPairGenerator;
import RSA.RSAOAEPDecrypt;
import RSA.RSAOAEPEncrypt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

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
        KeyPairGenerator keys = new KeyPairGenerator(2048);

        clientSimulator(keys);

        // Add nodeSimulator
    }

    private static void clientSimulator(KeyPairGenerator keys)
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        BigInteger[] key = new BigInteger[]{keys.getPublicKey(), keys.getPublicE()};

        System.out.println("Public key of receiver:\nn: " + key[0] + "\ne : " + key[1]);
        System.out.print("\nMessage: ");

        try
        {
            String message = input.readLine();

            // Start encryption.
            RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, new byte[]{1, 2}, key[0], key[1]);
            System.out.println("\nEncrypted message: " + Arrays.toString(encrypt.getEncryptedMessage()));
        }

        catch (IOException e)
        {
            System.out.println(e.getCause());
        }
    }

    public static String showDecrypted(byte[] encrypted)
    {
        return new String(encrypted);
    }
}
