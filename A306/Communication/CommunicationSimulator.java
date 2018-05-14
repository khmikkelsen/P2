package Communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommunicationSimulator
{
    public static void main(String[] args)
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Message: ");

        try
        {
            String message = input.readLine();
            System.out.println("Message was \"" + message + "\".");
        }

        catch (IOException e)
        {
            System.out.println(e.getCause());
        }
    }
}
