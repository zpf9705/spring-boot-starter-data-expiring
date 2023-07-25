//
// Copyright: Hangzhou Boku Network Co., Ltd
// ......
// Copyright Maintenance Date: 2022-2023
// ......
// Direct author: Zhang Pengfei
// ......
// Author email: 929160069@qq.com
// ......
// Please indicate the source for reprinting use
//


package io.github.zpf9705.expiring.spring_jdk.example_sdk.annotation;

import java.lang.annotation.*;

/**
 * When converting an object to a map, an alias needs to be given to the key of the map
 *
 * @author zpf
 * @since 3.1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapFiled {

    String name();
}
