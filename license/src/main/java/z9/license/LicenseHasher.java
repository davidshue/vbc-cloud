package z9.license;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Key;

/**
 * Created by david on 10/8/17.
 */
public class LicenseHasher {
    private static final String ALGO = "AES";

    //This needs to be exactly 16 characters
    private static final String SECRET_KEY = "Hi Ni Hao Maomi!";

    private static byte[] keyValue = SECRET_KEY.getBytes(Charset.forName("UTF-8"));


    public static String encode(Data data) {
        Cipher c = createCipher(Cipher.ENCRYPT_MODE);
        try {
            byte[] encVal = c.doFinal(data.toString().getBytes());
            return new BASE64Encoder().encode(encVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Data decode(String data) {
        Cipher c = createCipher(Cipher.DECRYPT_MODE);
        try {
            byte[] decordedValue = new BASE64Decoder().decodeBuffer(data);
            byte[] decValue = c.doFinal(decordedValue);
            String s =  new String(decValue);

            return Data.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher createCipher(int mode) {
        Key _key = new SecretKeySpec(keyValue, ALGO);
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(mode, _key);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
