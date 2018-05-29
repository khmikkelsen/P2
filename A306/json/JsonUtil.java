package json;

import json.serializers.RSAKeyDeserializer;
import json.serializers.RSAKeySerializer;
import json.serializers.SignatureDeserializer;
import json.serializers.SignatureSerializer;
import rsa.RSAKey;
import rsa.Signature;
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
