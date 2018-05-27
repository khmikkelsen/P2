package Communication;

import RSA.RSAKey;
import robin.Block;
import robin.Chain;
import robin.Message;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Server
public class Server
{
    // Main method.
    public static void main(String[] args)
    {
        // Streams.
        List<BufferedReader> readers = new ArrayList<>();
        List<BufferedWriter> writers = new ArrayList<>();
        PrintWriter console = new PrintWriter(System.out, true);

        //Connections.
        List<Socket> sockets = new ArrayList<>();

        try
        {
            console.println("Starting node...");
            ServerSocket server = new ServerSocket(140);
            console.println("Node started.");
            connectCommunicate(server, sockets, readers, writers, console);
        }

        catch (IOException e)
        {
            console.println("Node not setup: " + e.getMessage());
        }
    }

    // Searches after connections.
    private static <T> void connectCommunicate(ServerSocket server, List<Socket> sockets, List<BufferedReader> readers, List<BufferedWriter> writers, PrintWriter console)
    {
        // At least one connection is needed.
        connectSocket(server, sockets, readers, writers, console);
        communicate(server, sockets, writers, readers, console);
    }

    // Sets up a single connection and a stream.
    private static void connectSocket(ServerSocket server, List<Socket> sockets, List<BufferedReader> readers, List<BufferedWriter> writers, PrintWriter console)
    {
        try
        {
            Socket s = server.accept();

            if (s.isConnected())
            {
                sockets.add(s);
                setupStreams(s, readers, writers);
                console.println("Client connected.\n");
            }
        }

        catch (IOException e)
        {
            return;
        }
    }

    // Setting up streams.
    private static void setupStreams(Socket socket, List<BufferedReader> readers, List<BufferedWriter> writers)
    {
        try
        {
            readers.add(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            writers.add(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        }

        catch (IOException e)
        {
            return;
        }
    }

    // Receive messages and send.
    private static void communicate(ServerSocket server, List<Socket> sockets, List<BufferedWriter> writers, List<BufferedReader> readers, PrintWriter console)
    {
        Chain chain = new Chain();
        List<Block> blocks = new ArrayList<>();
        blocks.add(new Block("0", chain.getTarget().getCompactTarget(), List.of(makeMessage("[1] : 1_1 : 1_1 : [2]"))));    // Genesis block.

        List<Message> messages = new ArrayList<>();
        String message;
        int amountOfSockets = sockets.size();   // Optimization.

        while (true)
        {
            for (int i = 0; i < amountOfSockets; i++)
            {
                try
                {
                    if (readers.get(i) != null)
                    {
                        message = readers.get(i).readLine();
                        System.out.println(message);

                        // Add mining and validation.
                        if (messages.size() < 10)
                            messages.add(makeMessage(message));

                        else
                        {
                            blocks.add(createBlock(blocks.get(blocks.size()).calculateHash(), chain.getTarget().getCompactTarget(), messages));
                            messages.clear();
                        }
                    }
                }

                catch (IOException e)
                {
                    continue;   // Nothing.
                }

                // Must connect to new streams trying to connect to node.
                //connectSocket(server, sockets, readers, writers, console);
            }
        }
    }

    // Send message to connected streams.
    private static void sendMessage(List<BufferedWriter> writers, String message)
    {
        for (int i = 0; i < writers.size(); i++)
        {
            try
            {
                writers.get(i).write(message);
                writers.get(i).newLine();
                writers.get(i).flush();
            }

            catch (IOException e)
            {
                continue;   // Nothing.
            }
        }
    }

    // Creates a new message instance.
    private static Message makeMessage(String message)
    {
        try
        {
            Message m = new Message(CommunicationSimulator.getNumber(message, 0, ']') + "]",
                    new RSAKey(CommunicationSimulator.getSenderN(message), CommunicationSimulator.getSenderE(message)),
                    new RSAKey(CommunicationSimulator.getReceiverN(message), CommunicationSimulator.getReceiverE(message)));

            return m;
        }

        catch (IOException e)
        {
            return null;
        }
    }

    // Creates a new block.
    private static Block createBlock(String previousHash, String compactTarget, List<Message> messages)
    {
        return new Block(previousHash, compactTarget, messages);
    }
}
