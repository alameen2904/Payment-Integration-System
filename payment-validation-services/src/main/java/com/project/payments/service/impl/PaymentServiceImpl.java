package com.project.payments.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.project.payments.constant.ValidatorRuleEnum;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.service.interfaces.BusinessValidator;
import com.project.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${validator.rule-names}")
    private String validatorRuleNames;

    private final ApplicationContext applicationContext;

   private List<String> validatorRules;
private Map<String, Map<String, String>> validatorRuleConfig;
   @Override
    public String validateAndCreatePayment(PaymentRequest paymentRequest) {
        
    	log.info("validating and creating payment: {}|hmacSignature:{}", paymentRequest);
     
        String[] rules = validatorRuleNames.split(",");
        
        for (String rule : rules) {
            String trimmedRule = rule.trim();
            
            Optional<Class<? extends BusinessValidator>> validatorClass = 
                    ValidatorRuleEnum.getValidatorClassByRule(trimmedRule);

            if (!validatorClass.isPresent()) {
                log.warn("Rule [{}] skipped: No mapping found in ValidatorRuleEnum", trimmedRule);
                continue; 
            }
            BusinessValidator validator = applicationContext.getBean(validatorClass.get());

            if (validator == null) {
                log.warn("Rule [{}] skipped: No bean found for class {}", trimmedRule, validatorClass.get().getName());
                continue;
            }
            log.info("Executing validation rule: {}", trimmedRule);
            validator.validate(paymentRequest); 
            log.info("Rule [{}] passed.", trimmedRule);
        }

        log.info("Successfully passed all {} business validation rules.", rules.length);
        
        return "From Service Payment created successfully! " + paymentRequest;
    }

	

    @PostConstruct
    public void init() {
    }
}