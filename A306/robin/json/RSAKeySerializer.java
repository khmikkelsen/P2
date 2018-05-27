package robin.json;

import RSA.InvalidRSAKeyException;
import RSA.RSAKey;
import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;

public class RSAKeySerializer implements JsonSerializer<RSAKey> {
    @Override
    public JsonElement serialize(RSAKey src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getBase64String());
    }
}



