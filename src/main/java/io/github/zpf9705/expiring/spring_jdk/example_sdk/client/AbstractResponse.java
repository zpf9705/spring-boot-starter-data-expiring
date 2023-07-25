package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;


import com.alibaba.fastjson.JSON;

/**
 * Public Abstract Response Class
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class AbstractResponse implements Response {

    private static final long serialVersionUID = 6922151145018976148L;

    private static final Integer DATA_ERROR_CODE = 600558;

    private static final Integer UNKNOWN_ERROR_CODE = 500358;

    private static final boolean DEFAULT_IS_SUCCESS = false;

    private static final String DEFAULT_MESSAGE = "Please inherited [AbstractResponse]";

    @Override
    public boolean isSuccess() {
        return DEFAULT_IS_SUCCESS;
    }

    @Override
    public String getMessage() {
        return buildUnknownResponse(DEFAULT_MESSAGE).getMessage();
    }

    @Override
    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static DefaultResponse buildResponse(Integer code, String message) {
        return new DefaultResponse(code, message);
    }

    public static DefaultResponse buildUnknownResponse(String message) {
        return new DefaultResponse(UNKNOWN_ERROR_CODE, String
                .format("happen unknown exception,message=[%s]", message));
    }

    public static DefaultResponse buildDataErrorResponse(String message) {
        return new DefaultResponse(DATA_ERROR_CODE, String
                .format("happen data_error exception,message=[%s]", message));
    }
}
