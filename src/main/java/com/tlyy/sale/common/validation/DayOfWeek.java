package com.tlyy.sale.common.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

/**
 * @author LeiDongxing
 * create on 2020/6/21 23:28
 */
@Documented
@Constraint(validatedBy = DayOfWeekValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DayOfWeek {
    String message() default "Unknown day of Week";

    Class[] groups() default {};

    Class[] playload() default {};
}
