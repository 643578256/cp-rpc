package com.cp.rpc.common.annotaion;


import java.lang.annotation.*;


/**
 * 客户端注入服务使用
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcReference {
}
