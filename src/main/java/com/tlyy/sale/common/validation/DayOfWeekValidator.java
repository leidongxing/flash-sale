package com.tlyy.sale.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * @author LeiDongxing
 * create on 2020/6/21 23:36
 */
public class DayOfWeekValidator implements ConstraintValidator<DayOfWeek, String> {
    private final List<String> daysOfWeek = Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        String input = s.trim().toLowerCase();
        if (daysOfWeek.contains(input)) {
            return true;
        }
        return false;
    }
}
