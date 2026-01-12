package com.oguz.tekrar.constant;

public final class SiteApiPath {

    private SiteApiPath() {
    }


    public static final String VERSION = "/v1";
    public static final String BASE_URL = "/api" + VERSION + "/site";
    public static final String CREATE = "/create-site";
    public static final String UPDATE = "/update-site/{id}";
    public static final String DELETE = "/delete-site/{id}";
    public static final String GET_ALL = "/get-all-site";
    public static final String GET_BY_ID = "/get-site-by-id/{id}";
}
