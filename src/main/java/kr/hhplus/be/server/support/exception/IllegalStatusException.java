package kr.hhplus.be.server.support.exception;

import kr.hhplus.be.server.support.response.ErrorCode;

public class IllegalStatusException extends BaseException {
    public IllegalStatusException() {
        super(ErrorCode.COMMON_ILLEGAL_STATUS);
    }

    public IllegalStatusException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
