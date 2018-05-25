package Communication;

import java.io.*;
import java.net.Socket;

// Client Main.
public class Client
{
    // Field
    private static BufferedWriter writer = null;
    private static BufferedReader reader = null;
    private static Socket connection = null;

    // Main method.
    public static void main(String[] args)
    {
        PrintWriter console = new PrintWriter(System.out, true);

        // Setting up.
        console.println("Connecting to server...");
        setupConnection(console, "127.0.0.1", 140);
        console.println("Connected.\nChat started.");

        if (writer != null && reader != null)
            chat(console);

        else
            endSession(console, "Session ended: No streams setup.");
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
                reader.close();
                writer.close();
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
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }

        catch (IOException e)
        {
            closeStreams(console);
            endSession(console, "Session ended: Could not setup streams.");
        }
    }

    // chatting with server.
    private static void serverWrite(String message) throws IOException
    {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    // Receive message from server.
    private static String getMessage() throws IOException
    {
        String message;

        message = reader.readLine();

        if (message.length() > 0)
            return message;

        return "";
    }

    // Chat.
    private static void chat(PrintWriter console)
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String message;
        int[] keyPair = new int[2];

        // Entering address.
        console.print("Address (format: xxx_xxx): ");
        keyPair = CommunicationSimulator.receiveKey(input);

        while (true)
        {
            try
            {
                console.print(": ");
                console.flush();
                message = input.readLine();

                // Start encryption and hashing.

                if (message.length() > 0)
                    serverWrite(message);

                console.println(getMessage());
            }

            catch (IOException e)
            {
                console.println("IOException: " + e.getMessage());
                break;
            }
        }
    }
}
