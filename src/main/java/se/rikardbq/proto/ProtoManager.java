package se.rikardbq.proto;

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
    public ProtoPackage encodeProto(Object dat, ClaimsUtil.Sub sub, String secret) throws Exception {
        ProtoPackage.Builder protoPackageBuilder = new ProtoPackage.Builder();

        return protoPackageBuilder
                .withSubject(sub)
                .withData(dat)
                .sign(secret);
    }

    public ProtoRequest.Claims decodeProto(byte[] data, String secret, String signature) throws Exception {
        ProtoPackageVerifier protoPackageVerifier = new ProtoPackageVerifier.Builder()
                .withIssuer(ClaimsUtil.Iss.SERVER)
                .withSubject(ClaimsUtil.Sub.DATA)
                .withSecret(secret)
                .withSignature(signature)
                .build();

        return protoPackageVerifier.verify(data);
    }
}
