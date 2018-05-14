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
        console.print("Address (format: xxx-xxx): ");
        keyPair = receiveKey(input);

        while (true)
        {
            try
            {
                console.print(": ");
                console.flush();
                message = input.readLine();     // Reads String from console.

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

    // Entering receivers public key as address.
    private static int[] receiveKey(BufferedReader reader)
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
    private static boolean isKeyValid(String key)
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
}
