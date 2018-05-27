package robin;

import RSA.KeyPairGenerator;
import RSA.RSAKey;
import RSA.Signature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import robin.commands.*;
import robin.json.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        KeyPairGenerator sender = new KeyPairGenerator(2048);
        KeyPairGenerator recipient = new KeyPairGenerator(2048);
        RSAKey senderPublicKey = sender.getPublicKey();
        RSAKey senderPrivateKey = sender.getPrivateKey();
        RSAKey recipientPublicKey = recipient.getPublicKey();


        Message m = new Message("My text message", senderPublicKey, recipientPublicKey, new Signature("MIIBCgKCAQEAqZbHeEeMLkN6/pDewlOPE8aZwY5cuasV3NIGi6SYTGARqNRDFkmBLV/L0YL+bBjBtbDZyASJwcySlIK3cOtcQ9T0qyT/e+vhfrAREiCUg1A9ePA+eZKz1T6TJSSiuboXkkacH+tANehjQVchjKh9k2IUQyulapCTg3yh/tT7DHkI8ou1u/cJe8PiW12IL7XJtkNz1oWZamK8Y9XEOpCwxVvTr28lzSyWequF6AXuRt6LKizBy6PQco6zLK3p/uo7jnWPALoS65pBf7++34wt118GbnMxXokLiX13CL77R7InRbPI97MTPU13TZEUKENHrdfG+IZGK0g7prRdIYyXLQIDAQAB"));
        m.signMessage(senderPrivateKey);
        SendMessage com = new SendMessage(m);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RSAKey.class, new RSAKeySerializer());
        gsonBuilder.registerTypeAdapter(RSAKey.class, new RSAKeyDeserializer());
        gsonBuilder.registerTypeAdapter(Signature.class, new SignatureSerializer());
        gsonBuilder.registerTypeAdapter(Signature.class, new SignatureDeserializer());
        Gson gson = gsonBuilder
//                .setPrettyPrinting()
                .create();

        String json = gson.toJson(com);//"{\"command\":\"sendmessage\",\"message\":{\"message\":\"Message\",\"sender\":\"MIIBCgKCAQEAqZbHeEeMLkN6/pDewlOPE8aZwY5cuasV3NIGi6SYTGARqNRDFkmBLV/L0YL+bBjBtbDZyASJwcySlIK3cOtcQ9T0qyT/e+vhfrAREiCUg1A9ePA+eZKz1T6TJSSiuboXkkacH+tANehjQVchjKh9k2IUQyulapCTg3yh/tT7DHkI8ou1u/cJe8PiW12IL7XJtkNz1oWZamK8Y9XEOpCwxVvTr28lzSyWequF6AXuRt6LKizBy6PQco6zLK3p/uo7jnWPALoS65pBf7++34wt118GbnMxXokLiX13CL77R7InRbPI97MTPU13TZEUKENHrdfG+IZGK0g7prRdIYyXLQIDAQAB\",\"recipient\":\"MIIBCgKCAQEAlH3MNtkg/T0rHUby3p1cUZz0pWQh5lqppseibWcDLnTUdOlnT2uLv6N6kmdEa3HEZ+qhhQ6lhN5rk0ydSNQQ93F4mF0ksCQp0tU6+3xMj/WD8VEJ8WdSjAa4yTRfEG1KXdR81cd9PTmshiWKlXtKpxY0CEaCnDJsW3h9n9SPg3uWXhgUesBNnmZOKzltu4RItxwx62OKMLhUiZmyDdmYEixGwVE/kGD7U+2vBl+3v1ivkkxIX0gzW7cx0QsK4UkGo5c2pns6zxs3ZJ65TXDbaaddKCFTtk+OsULyLhUeWKjSO6iayRZlSCX7A+4mn5ATkorPjKeiu+Fy1eX1W14FUwIDAQAB\",\"signature\":\"aosijd\"}}";


        NetworkRequestHandler.handleIncomingRequest(json);

//        // Find out which command was invoked by mapping to NetworkCommand which has an Enum field.
//        NetworkCommand c = gson.fromJson(json, NetworkCommand.class);
//
//        switch (c.getCommand()) {
//            case SEND_MESSAGE:
//                SendMessage messageCommand = gson.fromJson(json, SendMessage.class);
//                Message incomingMessage = messageCommand.getMessage();
//
//                // TODO: Validate signature.
//
//
//                // TODO: Add to queue of messages and begin mining at some point.
//
//                break;
//            case GET_BLOCK_COUNT:
//
//                break;
//            case GET_BLOCK:
//                break;
//
//            default:
//                // Unsupported command.
//                break;
//        }
//
//
//        System.out.println(json);

    }

    public void main2(String [] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
    }

}
