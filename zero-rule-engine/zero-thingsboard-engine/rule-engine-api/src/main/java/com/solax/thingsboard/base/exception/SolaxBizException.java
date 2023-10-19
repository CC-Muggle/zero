package com.solax.thingsboard.base.exception;

public class SolaxBizException extends Exception{


    private SolaxBizException errorCode;

    public SolaxBizException() {
        super();
    }

    public SolaxBizException(ThingsboardErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public SolaxBizException(String message, ThingsboardErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SolaxBizException(String message, Throwable cause, ThingsboardErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public SolaxBizException(Throwable cause, ThingsboardErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

}
