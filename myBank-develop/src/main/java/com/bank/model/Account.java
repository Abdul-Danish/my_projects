package com.bank.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bank.enums.Database;
import com.bank.repository.AccountRepository;
import com.bank.repository.mongo.AccountRepositoryMongo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder


@Document(collection = "account")
@Entity
public class Account {
	
	@Id
	@javax.persistence.Id
	private long accountNumber;
	private long customerId;
	
	private AccountType accountType;
	private long balance;
	private int pinNumber;
	@Builder.Default
	private boolean isActive = true;
	
	@CreatedDate
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@LastModifiedDate
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	
	@Version
	@JsonIgnore
	@javax.persistence.Transient
	private Integer version;
	
}
