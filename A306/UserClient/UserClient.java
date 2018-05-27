package UserClient;

import RSA.KeyPairGenerator;
import RSA.RSAKey;
import com.google.gson.JsonSyntaxException;
import robin.Message;
import robin.commands.SendMessage;
import robin.json.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.file.Files.exists;

public class UserClient {
    /*static KeyPairGenerator newSenderKeys;
    String checkPrivate = "false";
    String checkPublic = "false";
    String isKeys;
    */


    public static void main(String args[]) {

        UserClient u = new UserClient();
        u.genCopyKeys();
    }

    void genCopyKeys() {
        KeyPairGenerator newGenKeys = null;
        {
            try {
                newGenKeys = new KeyPairGenerator(2048);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String keyPairString = JsonUtil.getParser().toJson(newGenKeys);
        writeToFile("C:\\Users\\M2\\IdeaProjects\\WorkingP2" +
                "\\A306\\UserClient\\user2.txt", keyPairString);
    }

    public static void writeToFile(String path, String Keys) {
        Path fileToCheckFor = Paths.get(path);
        if (!exists(fileToCheckFor)) {
            Charset charset = Charset.forName("UTF-8");

            try (BufferedWriter out = Files.newBufferedWriter(fileToCheckFor, charset, StandardOpenOption.CREATE_NEW)) {
                out.write(Keys);

            } catch (IOException e) {
                System.out.println("Error creating, and/or writing to file");
            }
        }
    }

    public void readFile(String path) {
        Path pt = Paths.get(path);
        String inputKeys = null;

        try (BufferedReader reader = Files.newBufferedReader(pt)) {
            String input = null;
            String line = null;
            while ((line = reader.readLine()) != null) {
                input += line;
            }
        } catch (IOException e) {
            System.out.println("Error trying to read file");
        }

        try {
            KeyPairGenerator savedKeyPair = JsonUtil.getParser
                    ().fromJson(inputKeys, KeyPairGenerator
                    .class);
            if (savedKeyPair != null) { /* Do something with keys */ }
            else {

            }
        } catch (JsonSyntaxException e) {
            // Create new keypair.
        }
    }

    /*
    private String findIfKeysInUse(String dirPath) throws
            IOException {

        File dir = new File(dirPath);

        if (exists(Paths.get(dir.getPath())) && dir.isDirectory()) {
            //list of files and directories in specified directory
            File[] listOfFiles = dir.listFiles();

            if (listOfFiles != null) {

                for (File listOfFile1 : listOfFiles) {
                    //test om filen indeholder key
                    readFile(listOfFile1.getAbsolutePath());
                }

                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isDirectory()) {
                        return findIfKeysInUse(listOfFile.getPath());
                    }
                }

            }
        }
        if()
                return
        else
            return
    }
    */



       /* private void sendMessage() {
        String jsonCommand = JsonUtil.getParser().toJson(new
                SendMessage());
    }*/
}
