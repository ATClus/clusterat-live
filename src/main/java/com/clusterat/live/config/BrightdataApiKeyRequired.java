package com.clusterat.live.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BrightdataApiKeyRequired {
    String errorMessage() default "Invalid or missing API Key";
}
