package userclient;

import rsa.*;
import blockchain.message.Message;
import simulator.Simulator;
import node.network.commands.SendMessageCommand;
import json.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.Scanner;

public class UserClient {
    /*static KeyPairGenerator newSenderKeys;
    String checkPrivate = "false";
    String checkPublic = "false";
    String isKeys;
    */

    private final String keyPath = "C:\\p2_blockchain\\userclient\\user.txt";

    private RSAKeyPair keyPair;

    private Simulator simulator;

    public UserClient(Simulator simulator) {
        this.simulator = simulator;

        keyPair = getSavedKeys();

        if (keyPair == null) {
            keyPair = generateKeys();

            saveKeys(keyPair);
        }

        Message message = getUserMessage();

        broadcastMessage(message);
    }

    /**
     * Implementation uses simulator to broadcast send message request to nodes.
     */
    private void broadcastMessage(Message message) {

        SendMessageCommand command = new SendMessageCommand(message);
        String commandString = JsonUtil.getParser().toJson(command);

        // Broadcast message to nodes (through simulator)
        simulator.broadcastMessage(commandString);
    }

    private void readMessages() {
        String encryptedMessage = "FvYK9lfek6IQsWXqjo+RyP7rB/Hk9ugeTsVbPKYPISFNDaj4Q4scExMgAKrWvT7feTsDXUy3bEdbQEwbsK6cxt7bftXSeart8FjyatOVE+eslE/K4FiLYbF89P3j9jSNnDs/4h7aGGl1PjsvUaEoFeQoH+6aO6/6f3+CETtWfij92A+2V9K8QjoLF9ZlnMifNHw434FDEnNg7rNIvJSwvnwknz3DTNF+/mHWMX9L43boq5t43uvyzkhy+cc6TDVOXIUJZSlNU/3Mpat1twupABuwmIQDGkqSXV0FUYMzne0/hqtOfZAucCiWZBfo4g3DAL40fTCiE5QrJKud/x7H4Q==";
        String privateKey = "MIICCQKCAQEAgUraLjLy3IhyKDACLDPab+nNIDpvFUuHZdBeJod/33jun4rxMujrW75iuFTeYy+I0uf3V3znJucH/5pqF70bfdd2B7+Bwo2CqZRySzGDmIC3D/QN7sdL38k4wpdLh+6qeXhujlxmnj7L9ikzSYBSGbOXtlDU+H6/LwqMW92nhVAblIKFWzfpuiFLK/uWfOvUe1uMHf7gOFIPM5SFsoY5uV7GTM0SlJfGOM78Z0VNyKvMK2Wh6xg6P2RzlyoXcltmA4/CsZhOMDBNcuMZOrN5jmFW0+l/Tx1LXCrdiRcOj1wuPq65Rbh7AEeSaKIGdGQ0fxztu8HT0RhAD8lbsQTEwwKCAQAzzp8LWQ34sVHw6X/NX3+9TVWM/u0o/slD0lEPpkbmcGpJkp6glDVO30RmcVdlLhRo0ltJerqS24tYb/LoDTYZYD8izql7oEGFmv6LQ81jy9vOsC/vCHvOMa6lRoOU9dzobIn8UGksKqSGwC6VWq5LLyvw+YXFBuCrxFHGs9Qe9gvHnBG4E3Xq4R3jwFae7bvNT3wy64fTjB9dkS9Tkej/2bWEIzBCncxzYkXhCHtHrmEp9B4HFSWF77IURJkReX9ge8Z4oWpbLaV4u0ehNkRBZi/eW8oU2Ml5okn4jHCka98qKauFd9bgcIPtqXAIlfbkwYCLE0ygEC4PAHjwneG9";

        try {
            RSAOAEPDecrypt decrypt = new RSAOAEPDecrypt(Base64.getDecoder().decode(encryptedMessage), new RSAKey(privateKey));
            String message = new String(decrypt.getDecryptedMessage());

            System.out.println(message);
        } catch (InvalidRSAKeyException | IOException e) {

        }
    }

    private Message getUserMessage() {
        Scanner scanner = new Scanner(System.in);

        String messageText = null;

        while (messageText == null) {
            System.out.print("Enter your message: ");

            messageText = scanner.nextLine();

            if (messageText.getBytes().length > 190) {
                messageText = null;
                System.out.println("Message is too long. It must be less than 190 characters");
            }
        }

        RSAKey recipientPublicKey = null;

        while (recipientPublicKey == null) {
            System.out.print("Enter the base 64 representation of the recipient public key: ");

            try {
                recipientPublicKey = new RSAKey(scanner.nextLine());
            } catch (IllegalArgumentException | IOException | InvalidRSAKeyException e) {
                System.out.println("Invalid rsa public key");
            }
        }

        try {
            byte[] encryptedMessage = new RSAOAEPEncrypt(messageText, recipientPublicKey).getEncryptedMessage();

            Message message = new Message(Base64.getEncoder().encodeToString(encryptedMessage), keyPair.getPublicKey(), recipientPublicKey);
            message.signMessage(keyPair.getPrivateKey());

            return message;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private RSAKeyPair generateKeys() {
        try {
            KeyPairGenerator gen = new KeyPairGenerator(2048);

            return gen.generateKeyPair();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveKeys(RSAKeyPair keyPair) {
        Path path = Paths.get(keyPath);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            String keyJson = JsonUtil.getParser().toJson(keyPair);

            writer.write(keyJson);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private RSAKeyPair getSavedKeys() {
        Path path = Paths.get(keyPath);
        String fileInput = "";

        File file = new File(keyPath);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
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
