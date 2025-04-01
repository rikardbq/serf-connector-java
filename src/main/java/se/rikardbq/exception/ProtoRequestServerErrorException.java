package se.rikardbq.exception;

import se.rikardbq.proto.ProtoRequest;

public class ProtoRequestServerErrorException extends ProtoPackageVerifyErrorException {

    public ProtoRequestServerErrorException(ProtoRequest.Error error) {
        super(error.getMessage(), error.getSource().name());
    }
}