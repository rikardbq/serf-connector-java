package se.rikardbq.proto;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;

public class ProtoPackageUtil {

    public static String generateSignature(byte[] data, byte[] secret) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, secret);
        mac.update(data);
        byte[] result = mac.doFinal();

        return Hex.encodeHexString(result);
    }
}
