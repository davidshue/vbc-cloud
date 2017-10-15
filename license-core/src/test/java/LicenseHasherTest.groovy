import org.junit.Test
import z9.license.LicenseHasher

import java.time.LocalDate

/**
 * Created by david on 10/8/17.
 */
class LicenseHasherTest {
    @Test
    void test() {
        long epochdays = LocalDate.now().minusDays(30).toEpochDay();
        String s = '1|' + epochdays

        String encrypted = LicenseHasher.encode(s)

        println encrypted

        String decrypted = LicenseHasher.decode(encrypted)

        println decrypted

    }
}
