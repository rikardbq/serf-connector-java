package se.rikardbq.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import se.rikardbq.exception.ProtoPackageErrorException;

public class ProtoManager {

    public ProtoManager() {
    }

    public ProtoPackage encodeProto(Object dat, ClaimsUtil.Sub sub, String secret) throws ProtoPackageErrorException {
        ProtoPackage.Builder protoPackageBuilder = new ProtoPackage.Builder();

        return protoPackageBuilder
                .withSubject(sub)
                .withData(dat)
                .sign(secret);
    }

    public ProtoRequest.Request decodeProto(byte[] data, String secret, String signature) throws ProtoPackageErrorException, InvalidProtocolBufferException {
        ProtoPackageVerifier protoPackageVerifier = new ProtoPackageVerifier.Builder()
                .withIssuer(ClaimsUtil.Iss.SERVER)
                .withSubject(ClaimsUtil.Sub.DATA)
                .withSecret(secret)
                .withSignature(signature)
                .build();

        return protoPackageVerifier.verify(data);
    }
}
