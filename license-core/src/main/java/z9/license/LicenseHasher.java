package z9.license;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Key;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by david on 10/8/17.
 */
public class LicenseHasher {
    private static final String ALGO = "AES";

    //This needs to be exactly 16 characters
    private static final String SECRET_KEY = "Hi Ni Hao Maomi!";

    private static byte[] keyValue = SECRET_KEY.getBytes(Charset.forName("UTF-8"));


    public static String encode(String data) {
        Cipher c = createCipher(Cipher.ENCRYPT_MODE);
        try {
            byte[] encVal = c.doFinal(data.getBytes());
            return new BASE64Encoder().encode(encVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String data) {
        Cipher c = createCipher(Cipher.DECRYPT_MODE);
        try {
            byte[] decordedValue = new BASE64Decoder().decodeBuffer(data);
            byte[] decValue = c.doFinal(decordedValue);
            return new String(decValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Command: java -jar LicenseGenerator.jar domain, type expiration_date");
            System.out.println("domain can be have wild card only once. It must contain at least two parts without * unless it is just *");
            System.out.println("valid domain like *.xyz.com, xyz.com. A domain with just * is a master license");
            System.out.println("0-trial, 1-limited, 2-enterprise");
            System.out.println("expiration_date format of yyyy-mm-dd, like 2018-10-10");
            System.exit(1);
        }
        String domain = args[0];
        String type = args[1];
        LocalDate date = LocalDate.parse(args[2], DateTimeFormatter.ISO_LOCAL_DATE);
        long epochDays = date.toEpochDay();
        String key = encode(domain + "|" + type + "|" + epochDays);
        System.out.println(key);
    }
}
