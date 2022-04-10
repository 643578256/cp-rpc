package com.cp.rpc.common.annotaion;


import java.lang.annotation.*;

/**
 * 服务端发布服务使用
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcService {


}
