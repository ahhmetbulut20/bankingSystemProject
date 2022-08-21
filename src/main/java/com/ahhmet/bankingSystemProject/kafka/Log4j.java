package com.ahhmet.bankingSystemProject.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j {

	private Logger log = LogManager.getLogger(Log4j.class);
	
	public void logging(String message) {
		System.out.println(message);
		log.info(message);
	} 
}
