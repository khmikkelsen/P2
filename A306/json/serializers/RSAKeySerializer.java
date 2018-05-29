package json.serializers;

import rsa.RSAKey;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RSAKeySerializer implements JsonSerializer<RSAKey> {
    @Override
    public JsonElement serialize(RSAKey src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getBase64String());
    }
}



