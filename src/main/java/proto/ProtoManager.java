package proto;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import se.rikardbq.models.Enums;
import serf_proto.*;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

public class ProtoManager {

    public ProtoManager() {
    }

    /**
     * let mut resp = MutationResponse::default();
     * resp.last_insert_row_id = 123;
     * resp.rows_affected = 1;
     * <p>
     * let mut buf = Vec::new();
     * buf.reserve(resp.encoded_len());
     * resp.encode(&mut buf).unwrap();
     * <p>
     * let mut mac = Hmac::<Sha256>::new_from_slice(b"my_secret_key").expect("Something went wrong!");
     * mac.update(&buf);
     * let result = mac.finalize();
     * let result_bytes = result.into_bytes();
     * let data_signature = base16ct::lower::encode_string(&result_bytes);
     */

    // make use of the builder pattern here and spit out a small "ProtoPackage" with the claims and the signature
    // ProtoPackageBuilder has a small set of methods that basically abstracts the ClaimsBuilder functions as masks them as its own
    // .sign should more or less only sign the claims proto bytes with a secret and set its signature field
    // as a user of the lib i will get a ProtoPackage and call for the claims and signature separately and add to where needed in the request.

    public byte[] encodeProto(Object dat, ClaimsOuterClass.Sub sub) throws Exception {
        ClaimsOuterClass.Claims.Builder claimsBuilder = ClaimsOuterClass.Claims.newBuilder();
        Instant now = Instant.now();
        claimsBuilder
                .setIss(ClaimsOuterClass.Iss.CLIENT)
                .setSub(sub)
                .setIat(now.getEpochSecond())
                .setExp(now.plusSeconds(30).getEpochSecond());

        switch (dat) {
//            some are not needed from this side of the transaction. Only the request parts are important.. possibly... I NEED TO THINK OK!?
//            case FetchResponseOuterClass.FetchResponse v -> claimsBuilder.setFetchResponse(v);
//            case MigrationResponseOuterClass.MigrationResponse v -> claimsBuilder.setMigrationResponse(v);
//            case MutationResponseOuterClass.MutationResponse v -> claimsBuilder.setMutationResponse(v);
            case MigrationRequestOuterClass.MigrationRequest v -> claimsBuilder.setMigrationRequest(v);
            case QueryRequestOuterClass.QueryRequest v -> claimsBuilder.setQueryRequest(v);
            default -> throw new Exception("err");
        }

        return claimsBuilder.build().toByteArray();
    }

    public <T> T decodeProto(byte[] data) throws JWTCreationException {

        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(Enums.Issuer.CLIENT.name())
                .withSubject(subject.name())
                .withClaim("dat", dat)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(30))
                .sign(algorithm);
    }

    public String generateDataSignature(byte[] data, byte[] secret) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, secret);
        mac.update(data);
        byte[] result = mac.doFinal();

        return DigestUtils.sha256Hex(result);
    }

    private boolean verifyDataSignature(byte[] data, String signature, String secret) {

    }
}
