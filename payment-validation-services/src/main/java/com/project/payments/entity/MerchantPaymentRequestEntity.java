package com.project.payments.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class MerchantPaymentRequestEntity {

    private Integer id;

    private String endUserID;

    private String merchantTxnReference;

    private String transactionRequest;

    private Timestamp creationDate;
}