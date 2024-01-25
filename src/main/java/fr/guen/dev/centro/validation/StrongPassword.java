package fr.guen.dev.centro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {

    String message() default "Doit comporter au moins 8 caractères dont un majiscule, un miniscule, un chiffre et un caractère spécial.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
