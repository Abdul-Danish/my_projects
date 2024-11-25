package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

	private long sendersAccountNumber;
	private long receiversAccountNumber;
	private long amount;
	private int senderPinNumber;
}
