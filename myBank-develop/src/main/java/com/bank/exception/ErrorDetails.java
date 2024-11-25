package com.bank.exception;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@AllArgsConstructor
public class ErrorDetails {

	private Date timeStamp;
	private int status;
	private String error;
	private String message;
	private String path;
	
	
}
