package json.serializers;

import rsa.InvalidRSAKeyException;
import rsa.RSAKey;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;

public class RSAKeyDeserializer implements JsonDeserializer<RSAKey> {
    @Override
    public RSAKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return new RSAKey(json.getAsJsonPrimitive().getAsString());
        } catch (InvalidRSAKeyException | IOException e) {
            return null;
        }
    }
}