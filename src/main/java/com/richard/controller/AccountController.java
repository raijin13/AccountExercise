package com.richard.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.richard.dao.AccountDao;
import com.richard.entity.Account;
import com.richard.request.TransferRequest;

@RestController
public class AccountController {

	@Autowired
	private AccountDao accountDao;

	@RequestMapping("/ping")
	public String ping() {
		return "pong";
	}

	@RequestMapping("/getAccountById/{id}")
	public Account getAccountById(@PathVariable("id") Long id) {
		return accountDao.findOne(id);
	}
	
	@RequestMapping(value = "/pessimiticTransfer", method = RequestMethod.POST)
	public String pessimisticTransfer(@RequestBody TransferRequest request) {
		Account accountFrom = accountDao.lockAccount(request.getAccountFrom());
		BigDecimal newBalanceFrom = sub(accountFrom.getBalance(), request.getAmount());
		accountFrom.setBalance(newBalanceFrom);
		accountDao.save(accountFrom);
		
		Account accountTo = accountDao.lockAccount(request.getAccountTo());	
		BigDecimal newBalanceTo = add(accountTo.getBalance(), request.getAmount());
		accountTo.setBalance(newBalanceTo);
		accountDao.save(accountTo);
		
		return "Transfer Complete";
	}
	
	private BigDecimal sub(BigDecimal v1, BigDecimal v2) {
		return roundDown(v1.subtract(v2), 2);
	}
	
	private BigDecimal add(BigDecimal v1, BigDecimal v2) {
		return roundDown(v1.add(v2), 2);
	}
	
	private BigDecimal roundDown(BigDecimal num, int scale) {
		if (num != null) {
			num = num.setScale(scale, BigDecimal.ROUND_DOWN);
		}
		return num;
	}
}
