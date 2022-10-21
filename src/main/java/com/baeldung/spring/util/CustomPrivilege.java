package com.baeldung.spring.util;

public enum CustomPrivilege {
    READ ("READ_PRIVILEGE"),
    WRITE ("WRITE_PRIVILEGE"),
    CHANGE_PASSWORD ("CHANGE_PASSWORD_PRIVILEGE");
    CustomPrivilege(String privilege) {
        this.privilege = privilege;
    }
    private final String privilege;

    public String value() {
        return privilege;
    }
}
