package io.github.zpf9705.expiring.spring_jdk.example_sdk;


/**
 * Sdk uniformly throws exceptions, including status and status information, as shown in {@link SdkAssert}
 *
 * @author zpf
 * @since 3.1.0
 */
public class SdkException extends RuntimeException {

    public static final String format
            = "{\"note\":\"Custom exception\",\"code\":\"%s\",\"msg\":\"%s\"}";

    private static final long serialVersionUID = 5913991753884029933L;

    private final Integer code;

    private final String msg;

    /**
     * Construction method with code and message
     *
     * @param code Request Status
     * @param msg  Request Result Information
     */
    public SdkException(Integer code, String msg) {
        super(String.format(format, code, msg));
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}