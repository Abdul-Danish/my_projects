package com.batch.process.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private long id;
		
	@Column(name =  "FIRST_NAME")
	private String firstName;
	
	@Column(name = "LAST_NAME") 
	private String lastName;
	
	@Column(name = "EMAIL")
    private String email;
	
    @Column(name = "GENDER")
    private String gender;
    
    @Column(name = "CONTACT")
    private String contactNo;
    
    @Column(name = "COUNTRY")
    private String country;
    
    @Column(name = "BATCH_STEP")
    private String batchStep;
	
}
