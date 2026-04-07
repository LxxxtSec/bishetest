package com.uav.audit.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditRecord {
    /**
     * 操作类型
     */
    String operationType();

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 操作对象
     */
    String operationObject() default "";
}
