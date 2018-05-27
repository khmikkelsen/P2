package robin.json;

import RSA.RSAKey;
import RSA.Signature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static Gson gsonParser;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RSAKey.class, new RSAKeySerializer());
        gsonBuilder.registerTypeAdapter(RSAKey.class, new RSAKeyDeserializer());
        gsonBuilder.registerTypeAdapter(Signature.class, new SignatureSerializer());
        gsonBuilder.registerTypeAdapter(Signature.class, new SignatureDeserializer());

        gsonParser = gsonBuilder.create();
    }

    public static Gson getParser() {
        return gsonParser;
    }
}
