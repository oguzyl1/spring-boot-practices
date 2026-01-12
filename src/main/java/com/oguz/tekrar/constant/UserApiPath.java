package com.oguz.tekrar.constant;

public final class UserApiPath {

    private UserApiPath() {
    }

    public static final String VERSION = "/v1";
    public static final String BASE_URL = "/api" + VERSION + "/users";
    public static final String CREATE = "/create-user";
    public static final String UPDATE = "/update-user/{id}";
    public static final String DELETE = "/delete-user/{id}";
    public static final String GET_ALL = "/get-all-users";
    public static final String GET_BY_ID = "/get-user-by-id/{id}";
    public static final String DELETE_ROLE = "/delete-role/{roleId}";

}