package cc.powind.security.core.proxy;

public enum RequestParameterEnum {

    SCHEME("X-Original-SCHEME"),

    HOST("X-Original-HOST"),

    SERVER_PORT("X-Original-SERVER-PORT"),

    URI("X-Original-URI"),

    METHOD("X-Original-METHOD"),

    NGINX_FORWARD("X-NGINX-FORWARD"),

    NGINX_AUTH("X-NGINX-AUTH"),

    LOGIN_ID("X-LOGIN-ID")

    ;

    final String value;

    RequestParameterEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
