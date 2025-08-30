package com.trick.backend.common.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 注解只能用于方法上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留，以便AOP可以读取
@Documented
public @interface LogRecord {

    /**
     * 操作模块，例如 "充电桩管理"
     */
    String module();

    /**
     * 操作类型，例如 "新增充电桩", "修改价格"
     */
    String type();

    /**
     * 操作描述，支持SpEL表达式，可以动态获取参数
     * 例如: "'修改了充电桩 #'+ #pileId +' 的状态'"
     */
    String description() default "";
}
