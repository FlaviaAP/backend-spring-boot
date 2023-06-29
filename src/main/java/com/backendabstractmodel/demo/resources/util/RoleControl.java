package com.backendabstractmodel.demo.resources.util;

public class RoleControl {

    private RoleControl() {
        throw new IllegalStateException("Utility class");
    }

    // Composites
    public static final String ADMIN = "Admin";
    public static final String EMPLOYED = "Employed";
    public static final String CUSTOMER = "Customer";

    // No Composites
    public static final String PRODUCT_ADD = "product:add";
    public static final String PRODUCT_EDIT = "product:edit";
    public static final String PRODUCT_DELETE = "product:delete";
    public static final String PRODUCT_LIST = "product:list";
    public static final String PRODUCT_VIEW = "product:view";

}
