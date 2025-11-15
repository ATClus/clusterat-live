package com.clusterat.live.config;

import java.lang.annotation.*;

/**
 * Anotação para indicar que um endpoint requer autenticação via API Key do Brightdata
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BrightdataApiKeyRequired {
    /**
     * Mensagem de erro customizada
     */
    String errorMessage() default "API Key inválida ou ausente";
}

