package Communication;

import RSA.RSAKey;
import RSA.RSAOAEPEncrypt;
import RSA.RSAOAEPSign;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import robin.Message;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

// Client Main.
public class Client
{
    // Field
    private static DataOutput writer = null;
    private static DataInput reader = null;
    private static Socket connection = null;

    // Main method.
    public static void main(String[] args)
    {
        PrintWriter console = new PrintWriter(System.out, true);

        // Setting up.
        console.println("Connecting to server...");
        setupConnection(console, "127.0.0.1", 140);
        console.println("Connected.\nChat started.");

        try
        {
            if (writer != null && reader != null)
                chat(console);

            else
                endSession(console, "Session ended: No streams setup.");
        }

        catch (IOException e)
        {
            console.println("Chat exception.");
        }
    }

    // Ending session.
    private static void endSession(PrintWriter console, String message)
    {
        console.println("\n" + message);
        System.exit(1);
    }

    // Closes streams and connections.
    private static void closeStreams(PrintWriter console)
    {
        try
        {
            if (connection != null && reader != null && writer != null)
            {
                connection.close();
            }
        }

        catch (IOException e)
        {
            endSession(console, "Streams could not be closed before ending session.");
        }
    }

    // Sets up connection and streams.
    private static void setupConnection(PrintWriter console, String IP, int port)
    {
        try
        {
            connection = new Socket(IP, port);
            setupStreams(console);
        }

        catch (IOException e)
        {
            console.println("Could not connect to server.");
            endSession(console, "Session ended.");
        }
    }

    // Setting up streams.
    private static void setupStreams(PrintWriter console)
    {
        try
        {
            writer = new DataOutputStream(connection.getOutputStream());
            reader = new DataInputStream(connection.getInputStream());
        }

        catch (IOException e)
        {
            closeStreams(console);
            endSession(console, "Session ended: Could not setup streams.");
        }
    }

    // chatting with server.
    private static void serverWrite(Message message) throws IOException
    {
        writer.writeChars(message.getMessage() + " : " + message.getSender().getModulus().toString() + "_" +
                message.getSender().getExponent().toString() + " : " + message.getRecipient().getModulus().toString() +
                message.getRecipient().getExponent().toString() + " : " + message.getSignature());
    }

    // Receive message from server.
    private static Message getMessage() throws IOException
    {
        Message message;
        String receiverMessage;

        receiverMessage = reader.readLine();

        if (receiverMessage.length() > 0)
        {
            message = new Message(CommunicationSimulator.getNumber(receiverMessage, 0, ']') + "]",
                    new RSAKey(CommunicationSimulator.getSenderN(receiverMessage), CommunicationSimulator.getSenderE(receiverMessage)),
                    new RSAKey(CommunicationSimulator.getReceiverN(receiverMessage), CommunicationSimulator.getReceiverE(receiverMessage)));

            return message;
        }

        return new Message("Error", new RSAKey(new BigInteger("0"), new BigInteger("0")), new RSAKey(new BigInteger("0"), new BigInteger("0")));
    }

    // Chat.
    private static void chat(PrintWriter console) throws IOException
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String message;
        BigInteger[] senderKey;
        BigInteger[] keyPair;

        // Entering address.
        console.print("Your address (format xxx_xxx): ");
        senderKey = CommunicationSimulator.receiveKey(input);
        RSAKey sender = new RSAKey(senderKey[0], senderKey[1]);

        console.print("\nAddress (format: xxx_xxx): ");
        keyPair = CommunicationSimulator.receiveKey(input);
        RSAKey receiver = new RSAKey(keyPair[0], keyPair[1]);

        while (true)
        {
            try
            {
                console.print(": ");
                console.flush();
                message = input.readLine();
                Message preparedMessage;

                // Start encryption and hashing.
                RSAOAEPEncrypt encryptedMessage = new RSAOAEPEncrypt(message, new RSAKey(keyPair[0], keyPair[1]));
                preparedMessage = new Message(CommunicationSimulator.bytesToString(encryptedMessage.getEncryptedMessage()), sender, receiver);
                preparedMessage.signMessage(CommunicationSimulator.bytesToString(new RSAOAEPSign(preparedMessage.getMessage(), sender).getSignature()));

                if (message.length() > 0)
                    serverWrite(preparedMessage);

                // Decrypt this.
                console.println(getMessage().getMessage());
            }

            catch (IOException e)
            {
                console.println("IOException: " + e.getMessage());
                break;
            }
        }
    }
}
