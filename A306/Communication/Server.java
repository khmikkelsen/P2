package Communication;

import java.io.*;
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
            console.println("Starting server...");
            ServerSocket server = new ServerSocket(140);
            console.println("Server started.");
            connectCommunicate(e -> e, true, server, sockets, readers, writers, console);
        }

        catch (IOException e)
        {
            console.println("Server not setup: " + e.getMessage());
        }
    }

    // Searches after connections.
    private static <T> void connectCommunicate(Tester<T> tester, T t, ServerSocket server, List<Socket> sockets, List<BufferedReader> readers, List<BufferedWriter> writers, PrintWriter console)
    {
        connectSocket(server, sockets, readers, writers, console);

        while (tester.test(t))
        {
            communicate(server, sockets, writers, readers, console);
        }
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
                console.println("Client connected.");
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
            // Empty.
        }
    }

    // Receive messages and send.
    private static void communicate(ServerSocket server, List<Socket> sockets, List<BufferedWriter> writers, List<BufferedReader> readers, PrintWriter console)
    {
        String message;
        int amountOfSockets = sockets.size();

        for (int i = 0; i < amountOfSockets; i++)
        {
            connectSocket(server, sockets, readers, writers, console);

            try
            {
                if (readers.get(i) != null)
                {
                    message = readers.get(i).readLine();
                    System.out.println(message);
                    sendMessage(writers, sockets.get(i).getInetAddress().getHostName() + ": " + message);
                }
            }

            catch (IOException e)
            {
                // Empty.
            }
        }
    }

    // Send message to connected clients.
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
                // Empty.
            }
        }
    }
}
