package com.project.payments.repository.impl;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.entity.MerchantPaymentRequestEntity;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.repository.interfaces.MerchantPaymentRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MerchantPaymentRequestRepositoryImpl implements MerchantPaymentRequestRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String INSERT_SQL = """
			INSERT INTO merchant_payment_request
			(endUserID, merchantTxnReference, transactionRequest)
			VALUES (:endUserID, :merchantTxnReference, :transactionRequest)
			""";

	@Override
	public int saveMerchantPaymentRequest(MerchantPaymentRequestEntity merchantPaymentRequestEntity) {
		log.info("Saving merchant payment request entity: {}", merchantPaymentRequestEntity);

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("endUserID", merchantPaymentRequestEntity.getEndUserID());
		params.addValue("merchantTxnReference", merchantPaymentRequestEntity.getMerchantTxnReference());
		params.addValue("transactionRequest", merchantPaymentRequestEntity.getTransactionRequest());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {

			namedParameterJdbcTemplate.update(
					INSERT_SQL,
					params,
					keyHolder,
					new String[]{"id"}   
					);

			Number generatedId = keyHolder.getKey();

			if (generatedId != null) {
				log.info("Merchant payment request inserted with id : {}", generatedId);
				return generatedId.intValue();
			}

			log.error("Failed to retrieve generated id after inserting merchant payment request : {}",
					merchantPaymentRequestEntity);
			throw new PaymentValidationException(
					ErrorCodeEnum.FAILED_TO_SAVE_PAYMENT_REQUEST.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_SAVE_PAYMENT_REQUEST.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (DuplicateKeyException ex) {

			log.error("Duplicate merchantTxnReference detected : {}",
					merchantPaymentRequestEntity.getMerchantTxnReference(), ex);

			return -1;
		}
	}}