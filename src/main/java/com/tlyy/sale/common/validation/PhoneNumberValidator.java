package com.tlyy.sale.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author LeiDongxing
 * create on 2020/6/21 23:42
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null) {
            return true;
        }
        return s.length() > 8 && s.length() < 14 && s.matches("[0-9]+");
    }
}
