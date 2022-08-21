package com.ahhmet.bankingSystemProject.currency;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class CollectApiExchange implements CurrencyInterface{
	@Override
	public double exchange(double amount, String ownerAccountType, String transferredAccountType) throws JsonMappingException, JsonProcessingException {
		
		if(ownerAccountType.equals("TL") && transferredAccountType.equals("Dolar") ) {
			return amount/dollarToTl();
		}
		
		else if(ownerAccountType.equals("TL") && transferredAccountType.equals("Altın") ) {
			return amount/goldenToTl();
		}
		
		else if(ownerAccountType.equals("Dolar") && transferredAccountType.equals("TL") ) {
			return amount*dollarToTl();
		}
		
		else if(ownerAccountType.equals("Dolar") && transferredAccountType.equals("Altın") ) {
			double dollarToTL=amount*dollarToTl();
			return dollarToTL/goldenToTl();
		}
		
		else if(ownerAccountType.equals("Altın") && transferredAccountType.equals("Dolar") ) {
			double goldenToTL=amount*goldenToTl();
			return goldenToTL/dollarToTl();
		}
		else if(ownerAccountType.equals("Altın") && transferredAccountType.equals("TL") ){
			return amount*goldenToTl();
		}
		else
			return amount;
	}
	
	public double dollarToTl() throws JsonMappingException, JsonProcessingException {
		RestTemplate client = new RestTemplate();
		HttpHeaders headers= new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "AUTHORIZATION APIKEY");
		String url= "https://api.collectapi.com/economy/singleCurrency?int=1&tag=USD";
		HttpEntity<?>requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String>response=client.exchange(url, HttpMethod.GET, requestEntity,String.class);
		System.out.print(response.getBody());
		double tlValue = 0;
		String r=response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node= objectMapper.readTree(r);
		JsonNode resultNode=node.get("result");
		
		if(resultNode.isArray()) {
			ArrayNode exchangeNode = (ArrayNode) resultNode;
			JsonNode singleNode = exchangeNode.get(0);
			String temp=singleNode.get("buying").toString();
			tlValue=Double.parseDouble(temp);
			System.out.println("\n"+tlValue);
		}
	
		return tlValue;
	}
	
	public double goldenToTl() throws JsonMappingException, JsonProcessingException {
		RestTemplate client = new RestTemplate();
		HttpHeaders headers= new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 3gRwwiyHvrD6Wm4N1gyBN1:6apbrXKSgFKxvUedGBZYZL");
		String url= "https://api.collectapi.com/economy/goldPrice";
		HttpEntity<?>requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String>response=client.exchange(url, HttpMethod.GET, requestEntity,String.class);
		System.out.print(response.getBody());
		double tlValue = 0;
		String r=response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node= objectMapper.readTree(r);
		JsonNode resultNode=node.get("result");
		if(resultNode.isArray()) {
			ArrayNode exchangeNode = (ArrayNode) resultNode;
			JsonNode singleNode = exchangeNode.get(0);
			String temp=singleNode.get("buying").toString();
			tlValue=Double.parseDouble(temp);
			System.out.println("\n"+tlValue);
		}
		return tlValue;
	}
}
