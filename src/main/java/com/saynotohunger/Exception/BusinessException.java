package com.saynotohunger.Exception;

public class BusinessException extends RuntimeException 
{

    private final String errorCode;
    private final int httpStatus;

    //throw new BusinessException("Donation not found");
    public BusinessException(String message) 
    {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = 400;
    }

    //throw new BusinessException("Token expired", "TOKEN_ERROR");
    public BusinessException(String message, String errorCode) 
    {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }

    //throw new BusinessException("Unauthorized access", "AUTH_ERROR", 403);
    public BusinessException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
