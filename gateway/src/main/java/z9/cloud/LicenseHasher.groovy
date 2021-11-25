package z9.cloud

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.Charset
import java.security.Key

/**
 * Created by david on 10/8/17.
 */
class LicenseHasher {
    private static final String ALGO = 'AES'

    //This needs to be exactly 16 characters
    private static final String SECRET_KEY = 'Hi Ni Hao Maomi!'

    private static byte[] keyValue = SECRET_KEY.getBytes(Charset.forName('UTF-8'))

    static String decode(String data) {
        Cipher c = createCipher(Cipher.DECRYPT_MODE)
        byte[] decordedValue = Base64.decoder.decode(data)
        byte[] decValue = c.doFinal(decordedValue)
        new String(decValue)
    }

    private static Cipher createCipher(int mode) {
        Key _key = new SecretKeySpec(keyValue, ALGO)
        Cipher c = Cipher.getInstance(ALGO)
        c.init(mode, _key)
        return c
    }
}
