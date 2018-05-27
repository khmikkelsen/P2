package Communication;

import RSA.*;
import robin.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommunicationSimulator
{
    public static final char KEYSEPERATOR = '_';

    // Entering receivers public key as address.
    public static BigInteger[] receiveKey(BufferedReader reader)
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
                return new BigInteger[]{new BigInteger(getNumber(key, 0, KEYSEPERATOR)), new BigInteger(getNumber(key, KEYSEPERATOR))};
            }
        }

        catch (IOException e)
        {
            return new BigInteger[]{new BigInteger("0"), new BigInteger("0")};
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

    public static String getNumber(String text, int startIndex)
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
    public static String getNumberBackwards(String text, char startIndex)
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

    public static String getNumberBackwards(String text, char startIndex, char endIndex)
    {
        char[] number = text.toCharArray();

        for (int i = number.length - 1; i >= 0; i--)
        {
            if (number[i] == startIndex)
            {
                return getNumber(text, i, endIndex);
            }
        }

        return null;
    }

    // Copy of previous getNumber, but using int as index.
    public static String getNumber(String text, int startIndex, char endIndex)
    {
        char[] number = text.toCharArray();
        String returnNumber = "";

        for (int i = startIndex; i < number.length && (number[i] != endIndex && number[i] != ']'); i++)
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
    public static Message clientSimulator(KeyPairGenerator receiverKeys, KeyPairGenerator senderKeys)
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
            return null;
        }
    }

    // Prepares message to be send to node.
    public static Message prepareMessage(String message, KeyPairGenerator receiver, KeyPairGenerator sender)
    {
        try
        {
            String encryptedMessage = bytesToString(new RSAOAEPEncrypt(message, receiver.getPublicKey()).getEncryptedMessage());
            Message returnMessage = new Message(encryptedMessage, sender.getPublicKey(), receiver.getPublicKey());
            String signedMessage = bytesToString(new RSAOAEPSign(encryptedMessage, sender.getPrivateKey()).getSignature());
            returnMessage.signMessage(signedMessage);

            return returnMessage;
        }

        catch (IOException e)
        {
            return null;
        }
    }

    // Simulates a node.
    public static void nodeSimulator(Message message)
    {
        // Chain
        Chain chain = new Chain();

        // Blocks
        List<Block> blocks = new ArrayList<>();

        System.out.println("Encrypted message: " + message.getMessage());
        System.out.println("Signature of message: " + message.getSignature() + "\n");
        System.out.println("\nNode:\n");

        try
        {
            // Genesis block
            blocks.add(new Block("0", chain.getTarget().getCompactTarget(), List.of(prepareMessage("Hello, world", new KeyPairGenerator(2048), new KeyPairGenerator(2048)))));
            blocks.get(blocks.size() - 1).mineBlock();

            // Creating second block.
            blocks.add(new Block(blocks.get(blocks.size() - 1).calculateHash(), chain.getTarget().getCompactTarget(), List.of(message)));
            blocks.get(blocks.size() - 1).mineBlock();

            // Third block as incoming block.
            blocks.add(incomingBlock(chain, blocks.get(blocks.size() - 1).calculateHash()));

            // Mining genesis and newly created blocks and prints results.
            printMerkleHashesAndNonce(blocks);

            // Validating all incoming blocks.
            System.out.println("Validating incoming blocks...\nResult: " +
                    (validateBlocks(List.of(blocks.get(blocks.size() - 1)), chain.getTarget().getCompactTarget()) &&
                            validateBlockMessages(List.of(blocks.get(blocks.size() - 1))) ?  "validated" : "not validated"));

            // Start writing to database after incoming block.
            //writeToDatabase(blocks);
        }

        catch (IOException e)
        {
            System.out.println("Message not verified.");
            e.printStackTrace();
        }
    }

    // Returns an incoming block.
    private static Block incomingBlock(Chain chain, String previousHash) throws IOException
    {
        Block minedBlock = new Block(previousHash, chain.getTarget().getCompactTarget(), getMessages());
        minedBlock.mineBlock();
        return minedBlock;
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

    // Returns receiver's key N.
    public static BigInteger getReceiverN(String message)
    {
        int colonCounter = 0;

        for (int i = 0; i < message.length(); i++)
        {
            if (message.charAt(i) == ':')
                colonCounter++;

            if (colonCounter == 2)
                return new BigInteger(getNumber(message, i + 2, '_'));
        }

        return null;
    }

    // Returns receiver's key E.
    public static BigInteger getReceiverE(String message)
    {
        int underscoreCounter = 0;

        for (int i = 0; i < message.length(); i++)
        {
            if (message.charAt(i) == '_')
                underscoreCounter++;

            if (underscoreCounter == 2)
                return new BigInteger(getNumber(message, i + 1, ' '));
        }

        return null;
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

    // Converts byte array into a String array.
    public static String bytesToString(byte[] array)
    {
        String result = "[";

        for (int i = 0; i < array.length; i++)
        {
            if (i == array.length - 1)
            {
                result = result + String.valueOf(array[i]);
                continue;
            }

            result = result + String.valueOf(array[i]) + ", ";
        }

        return result + "]";
    }

    // Converts byte array String to byte array.
    public static byte[] stringToByte(String array)
    {
        String arrayNoSpaces = removeSpaces(array);
        byte[] result = new byte[stringByteArraySize(arrayNoSpaces)];
        char[] charArray = arrayNoSpaces.toCharArray();

        for (int i = 0, j = 0; i < arrayNoSpaces.length() - 2; i++)
        {
            if (charArray[i] == '[' || charArray[i] == ',')
            {
                result[j] = (byte) Integer.parseInt(getNumber(arrayNoSpaces, i + 1, ','));
                j++;
            }
        }

        return result;
    }

    // Removes spaces from a String.
    private static String removeSpaces(String str)
    {
        String result = "";

        for (int i = 0; i < str.length(); i++)
        {
            if (str.charAt(i) != ' ')
                result = result + str.charAt(i);
        }

        return result;
    }

    // Converts byte array to String.
    public static String toString(byte[] array)
    {
        String result = "";

        for (int i = 0; i < array.length; i++)
        {
            result = result + (char) array[i];
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

        return commas + 1;
    }

    // Adds some blocks to a list.
    private static List<Message> getMessages() throws IOException
    {
        String m1 = "Hej";
        String m2 = "Hvordan går det?";
        String m3 = "A306.";

        // Keys for each message.
        KeyPairGenerator m1Key = new KeyPairGenerator(2048);
        KeyPairGenerator m2Key = new KeyPairGenerator(2048);
        KeyPairGenerator m3Key = new KeyPairGenerator(2048);

        // Sending messages to each other.
        Message m1Prepared = prepareMessage(m1, m2Key, m1Key);
        Message m2Prepared = prepareMessage(m2, m3Key, m2Key);
        Message m3Prepared = prepareMessage(m3, m1Key, m3Key);

        return List.of(m1Prepared, m2Prepared, m3Prepared);
    }

    // Mines all blocks on a node.
    public static void mineBlocks(List<Block> blocks)
    {
        for (int i = 0; i < blocks.size(); i++)
        {
            blocks.get(i).mineBlock();
        }
    }

    // Prints merkle root hashes for all blocks.
    public static void printMerkleHashesAndNonce(List<Block> blocks)
    {
        for (int i = 0; i < blocks.size(); i++)
        {
            System.out.println("Block " + (i + 1) + " merkle root hash: " + blocks.get(i).getMerkleRootHash());
            System.out.println("Nonce: " + blocks.get(i).getNonce() + "\n");
        }
    }

    // Validates all messages in all blocks.
    public static boolean validateBlockMessages(List<Block> blocks)
    {
        try
        {
            for (int i = 0; i < blocks.size(); i++)
            {
                for (int j = 0; j < blocks.get(i).getMessages().size(); j++)
                {
                    Message message = blocks.get(i).getMessages().get(j);
                    new RSAOAEPVerify(stringToByte(message.getSignature()), message.getMessage().getBytes(), message.getSender());
                }
            }

            return true;
        }

        catch (IOException | BadVerificationException e)
        {
            return false;
        }
    }

    // Validates a block.
    public static boolean validateBlocks(List<Block> blocks, String compactTarget)
    {
        for (int i = 0; i < blocks.size(); i++)
        {
            if (new BigInteger(blocks.get(i).calculateHash(), 16).compareTo(Target.calculateBigIntergerTarget(compactTarget)) == 1 ||
                    !BlockUtil.calculateMerkleRootHash(blocks.get(i).getMessages()).equals(blocks.get(i).getMerkleRootHash()))
                return false;
        }

        return true;
    }

    // Writes a list of blocks to a database.
    public static void writeToDatabase(List<Block> blocks) throws SQLException
    {
        DatabaseConnection database = new DatabaseConnection();

        database.setup();
        database.createBlockTable();
        database.createMessageTable();

        for (int i = 0; i < blocks.size(); i++)
        {
            database.addBlock(blocks.get(i));
        }
    }
}
