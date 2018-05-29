package json.serializers;

import rsa.Signature;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SignatureDeserializer implements JsonDeserializer<Signature> {
    @Override
    public Signature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return new Signature(json.getAsJsonPrimitive().getAsString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}