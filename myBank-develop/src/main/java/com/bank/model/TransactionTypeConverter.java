package com.bank.model;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {

	@Override
	public String convertToDatabaseColumn(TransactionType transactionType) {
		if (transactionType == null) {
            return null;
        }
        return transactionType.getType();
	}

	@Override
	public TransactionType convertToEntityAttribute(String type) {
		if (type == null) {
            return null;
        }
		
        return Stream.of(TransactionType.values())
          .filter(c -> c.getType().equals(type))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
	}

	
}
