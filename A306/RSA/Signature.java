package RSA;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class Signature {
    private byte[] bytes;
    private String base64String;

    public Signature(String base64String) {
        this.bytes = Base64.getDecoder().decode(base64String);
        this.base64String = base64String;
    }

    public Signature(byte[] bytes) {
        this.bytes = bytes;
        this.base64String = Base64.getEncoder().encodeToString(bytes);
    }

    public String getBase64String() {
        return base64String;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;
        return Arrays.equals(bytes, signature.bytes) &&
                Objects.equals(base64String, signature.base64String);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(base64String);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }
}
