package cc.powind.security.core.transfer;

public enum ReturnTypeEnum {

    JSON_TYPE("jsonType");
    
    final String value;

    ReturnTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
