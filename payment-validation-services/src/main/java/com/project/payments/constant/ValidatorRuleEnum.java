package com.project.payments.constant;

import java.util.Optional;

import com.project.payments.service.impl.businessvalidators.DuplicateTxnValidator;
import com.project.payments.service.interfaces.BusinessValidator;

public enum ValidatorRuleEnum {

	DUPLICATE_TXN_RULE(
			"DUPLICATE_TXN_RULE", DuplicateTxnValidator.class);

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

			if (v.name().equalsIgnoreCase(rule)) { 
				return Optional.of(v.getValidatorClass());
			}
		}
		return Optional.empty();
	}
}
