package com.swyp8team2.common.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier(ShareUrlCryptoService.QUALIFIER)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
public @interface ShareUrlCryptoService {

    String QUALIFIER = "shareUrlCryptoService";
}
