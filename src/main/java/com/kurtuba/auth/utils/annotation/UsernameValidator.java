package com.kurtuba.auth.utils.annotation;

import com.kurtuba.auth.utils.Utils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<UserName, String> {

    boolean notBlank;

    @Override
    public void initialize(UserName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.notBlank = constraintAnnotation.notBlank();
    }

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {

        if (notBlank && (userName == null || userName.isEmpty())) {
            return false;
        }

        if (!notBlank && (userName == null || userName.isEmpty())) {
            return true;
        }

        //userName is not null or empty
        Pattern pattern = Pattern.compile(Utils.USERNAME_REGEX);
        Matcher matcher = pattern.matcher(userName);
        if(matcher.find()){
            return true;
        }

        return false;
    }
}
