package robin.json;

import RSA.RSAKey;
import RSA.Signature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static Gson gsonParser;
    private static Gson prettyParser;


    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RSAKey.class, new RSAKeySerializer());
        gsonBuilder.registerTypeAdapter(RSAKey.class, new RSAKeyDeserializer());
        gsonBuilder.registerTypeAdapter(Signature.class, new SignatureSerializer());
        gsonBuilder.registerTypeAdapter(Signature.class, new SignatureDeserializer());

        gsonParser = gsonBuilder.disableHtmlEscaping().create();
        prettyParser = gsonBuilder.disableHtmlEscaping().setPrettyPrinting().create();
    }

    public static Gson getParser() {
        return gsonParser;
    }

    public static Gson getPrettyParser() {
        return prettyParser;
    }
}
