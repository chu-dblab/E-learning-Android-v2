package tw.edu.chu.csie.dblab.uelearning.android.util;

import java.security.MessageDigest;

/**
 * Created by afu730917 on 2015/3/15.
 */
public class EncryptUtils {

    public static String sha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] out = digest.digest(input.getBytes("UTF-8"));
            return android.util.Base64.encodeToString(out, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
