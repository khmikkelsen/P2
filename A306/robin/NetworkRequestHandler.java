package robin;

import RSA.BadVerificationException;
import RSA.RSAOAEPVerify;
import com.google.gson.JsonSyntaxException;
import robin.commands.*;
import robin.json.JsonUtil;

import java.io.IOException;

public class NetworkRequestHandler {

       public static void handleIncomingRequest(String request) {

//        String request = "{\"command\":\"sendmessage\",\"message\":{\"message\":\"Message\",\"sender\":\"MIIBCgKCAQEAqZbHeEeMLkN6/pDewlOPE8aZwY5cuasV3NIGi6SYTGARqNRDFkmBLV/L0YL+bBjBtbDZyASJwcySlIK3cOtcQ9T0qyT/e+vhfrAREiCUg1A9ePA+eZKz1T6TJSSiuboXkkacH+tANehjQVchjKh9k2IUQyulapCTg3yh/tT7DHkI8ou1u/cJe8PiW12IL7XJtkNz1oWZamK8Y9XEOpCwxVvTr28lzSyWequF6AXuRt6LKizBy6PQco6zLK3p/uo7jnWPALoS65pBf7++34wt118GbnMxXokLiX13CL77R7InRbPI97MTPU13TZEUKENHrdfG+IZGK0g7prRdIYyXLQIDAQAB\",\"recipient\":\"MIIBCgKCAQEAlH3MNtkg/T0rHUby3p1cUZz0pWQh5lqppseibWcDLnTUdOlnT2uLv6N6kmdEa3HEZ+qhhQ6lhN5rk0ydSNQQ93F4mF0ksCQp0tU6+3xMj/WD8VEJ8WdSjAa4yTRfEG1KXdR81cd9PTmshiWKlXtKpxY0CEaCnDJsW3h9n9SPg3uWXhgUesBNnmZOKzltu4RItxwx62OKMLhUiZmyDdmYEixGwVE/kGD7U+2vBl+3v1ivkkxIX0gzW7cx0QsK4UkGo5c2pns6zxs3ZJ65TXDbaaddKCFTtk+OsULyLhUeWKjSO6iayRZlSCX7A+4mn5ATkorPjKeiu+Fy1eX1W14FUwIDAQAB\",\"signature\":\"aosijd\"}}";

        // Find out which command was invoked by mapping to NetworkCommand which has an Enum field.
        NetworkCommand c = JsonUtil.getParser().fromJson(request, NetworkCommand.class);

        switch (c.getCommand()) {
            case SEND_MESSAGE:
                Message incomingMessage;

                try {
                    SendMessage messageCommand = JsonUtil.getParser().fromJson(request, SendMessage.class);
                    incomingMessage = messageCommand.getMessage();
                } catch (JsonSyntaxException e) {
                    break;
                }

                sendMessage(incomingMessage);

                break;
            case GET_BLOCK_COUNT:

                break;
            case GET_BLOCK:
                break;

            default:
                // Unsupported command.
                break;
        }
    }

    private static void sendMessage(Message message) {
        try {
            RSAOAEPVerify verify = new RSAOAEPVerify(message.getSignature().getBytes(), message.getMessage().getBytes(), message.getSender());
        } catch (IOException | BadVerificationException e) {
            // Invalid signature.
            return;
        }

        // TODO: Add to queue of messages and begin mining at some point.
    }

    private void sendBlockCount(long blockCount) {
        // Create socket and respond.
    }
}
