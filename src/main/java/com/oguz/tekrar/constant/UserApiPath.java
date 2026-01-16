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
    public static final String TUM_KULLANICILARI_GETIR = "/tum-kullanicilari-getir";
    public static final String GET_USER_NAMES = "/get-user-names";
    public static final String SEARCH_BY_PART = "/search";
    public static final String SEARCH_ADVANCED = "/search/advanced";
    public static final String GET_MAAS_AZALAN_SIRALAMA = "/maasa-gore-azalan-siralama";
    public static final String FIND_USERS_BY_ISIMLER = "/find-users-by-isimler";
    public static final String COUNT_USER_BY_AGE = "/count-user-by-age";
    public static final String MAAS_ARALIGINA_GORE_GETIR = "/find-maas-araligi";
    public static final String FIND_USERS_NAME_IS_NULL = "/find-users-name-is-null";
    public static final String GET_MAAS_SUM = "/get-maas-sum";
}