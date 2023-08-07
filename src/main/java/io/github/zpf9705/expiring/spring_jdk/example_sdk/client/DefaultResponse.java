package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;


/**
 * Default response impl for {@link Response}
 *
 * @author zpf
 * @since 3.1.0
 */
public class DefaultResponse extends AbstractResponse {

    private static final long serialVersionUID = -6303939513087992265L;

    private Integer code;

    private String message;

    public DefaultResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
