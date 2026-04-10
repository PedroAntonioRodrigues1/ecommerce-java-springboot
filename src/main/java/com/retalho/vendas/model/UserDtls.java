package com.retalho.vendas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserDtls {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String nome;

	    @Column(nullable = false, unique = true)
	    private String loginEmail;

	    @Column(nullable = false)
	    private String senha;

	    private String telefone;

	    private String role;
	    private Boolean isEnable;
	    private Boolean accountNonLocked;
	    private Integer failedAttempt;
	    private Date lockTime;
	    private String resetToken;

	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<OrderAdress> adress = new ArrayList<>();

	    @OneToMany(mappedBy = "user")
	    private List<Order> orders = new ArrayList<>();

	    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    private Cart cart;
}
