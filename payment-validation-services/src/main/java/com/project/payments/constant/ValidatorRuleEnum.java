package com.project.payments.constant;

import java.util.Arrays;
import java.util.Optional;

import com.project.payments.service.interfaces.BusinessValidator;
import com.project.payments.service.impl.businessvalidators.ValidatorRule1;
import com.project.payments.service.impl.businessvalidators.ValidatorRule2;
import com.project.payments.service.impl.businessvalidators.ValidatorRule3;

public enum ValidatorRuleEnum {

    VALIDATOR_RULE1("VALIDATOR RULE1", ValidatorRule1.class),
    VALIDATOR_RULEZ("VALIDATOR RULEZ", ValidatorRule2.class),
    VALIDATOR_RULE3("VALIDATOR RULE3", ValidatorRule3.class);

    private final String ruleName;
    private final Class<? extends BusinessValidator> validatorClass;

    ValidatorRuleEnum(String ruleName, Class<? extends BusinessValidator> validatorClass) {
        this.ruleName = ruleName;
        this.validatorClass = validatorClass;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Class<? extends BusinessValidator> getValidatorClass() {
        return validatorClass;
    }

    
    public static Optional<Class<? extends BusinessValidator>> getValidatorClassByRule(String rule) {
        for (ValidatorRuleEnum v : values()) {
            // Use equalsIgnoreCase to be safe!
            if (v.name().equalsIgnoreCase(rule)) { 
                return Optional.of(v.getValidatorClass());
            }
        }
        return Optional.empty();
    }
}
