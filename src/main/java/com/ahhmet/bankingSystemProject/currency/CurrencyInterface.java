package com.ahhmet.bankingSystemProject.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface CurrencyInterface {
		public double exchange(double amount, String ownerAccountType, String transferredAccountType) throws JsonMappingException, JsonProcessingException;
}
