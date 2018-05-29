package UserClient;

import RSA.KeyPairGenerator;
import RSA.RSAKey;
import RSA.RSAKeyPair;
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

    private final String keyPath = "C:\\p2_blockchain\\userclient\\user.txt";

    public static void main(String args[]) {
        UserClient u = new UserClient();
//        u.genCopyKeys();
    }

    private void saveKeys(RSAKeyPair keyPair) {
        Path pt = Paths.get(keyPath);


    }

    private RSAKeyPair getSavedKeys() {
        Path pt = Paths.get(keyPath);
        String fileInput = null;
        try (BufferedReader reader = Files.newBufferedReader(pt)) {

            String line = null;
            while ((line = reader.readLine()) != null) {
                fileInput += line;
            }
        } catch (IOException e) {
            System.out.println("Error trying to read file");
            return null;
        }

        try {
            RSAKeyPair savedKeyPair = JsonUtil.getParser().fromJson(fileInput, RSAKeyPair.class);

            return savedKeyPair;
        } catch (Exception e) {
            return null;
            // Create new keypair.
        }
    }
}
