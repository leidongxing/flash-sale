package com.tlyy.sale.api.common.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

/**
 * @author LeiDongxing
 * create on 2020/6/21 23:41
 */
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";
    Class[] groups() default {};
    Class[] payload() default {};
}
