package com.kakaotech.ott.ott.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ImageCountValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidImageCount {

    String message() default "최대 5개의 이미지까지 업로드할 수 있습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
