package com.bank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

//@Entity
public class Address {

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@JsonIgnore
//	@Transient
//	private int id;
	
	private String houseNumber;
	private int zipCode;
	private String city;
	
}
