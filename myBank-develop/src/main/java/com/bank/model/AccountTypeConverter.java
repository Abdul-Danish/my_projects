package com.bank.model;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<AccountType, String> {

	@Override
	public String convertToDatabaseColumn(AccountType accountType) {
		if (accountType == null) {
            return null;
        }
        return accountType.getType();
	}

	@Override
	public AccountType convertToEntityAttribute(String type) {
		if (type == null) {
            return null;
        }
		
        return Stream.of(AccountType.values())
          .filter(c -> c.getType().equals(type))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
	}
	
	
}
