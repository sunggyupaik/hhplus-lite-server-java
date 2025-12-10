package kr.hhplus.be.server.support.exception;

import kr.hhplus.be.server.support.response.ErrorCode;

public class InvalidParamException extends BaseException {
    public InvalidParamException() {
        super(ErrorCode.COMMON_INVALID_PARAMETER);
    }

    public InvalidParamException(String message) {
        super(message, ErrorCode.COMMON_INVALID_PARAMETER);
    }

    public InvalidParamException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
