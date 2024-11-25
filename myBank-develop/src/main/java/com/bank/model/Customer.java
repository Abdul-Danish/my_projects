package com.bank.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bank.enums.Database;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Document(collection = "customer")
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Customer {

	@Id
	@javax.persistence.Id
	private long customerId;
	
	@Size(min = 2, max = 35)
	private String customerName;
	private long phoneNumber;
	private long adhaarNumber;
	
	@NotBlank(message = "Email cannot be empty")
	@Email(message = "invalid Email")
	private String email;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private DateOfBirth dob;
	
//	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@Type(type = "jsonb")
	@Column(name = "address", columnDefinition = "jsonb")
	private Address address;
	
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
	
	private Database database;
	
}
