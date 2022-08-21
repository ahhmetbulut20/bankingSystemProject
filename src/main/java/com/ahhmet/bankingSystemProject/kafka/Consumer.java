package com.ahhmet.bankingSystemProject.kafka;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class Consumer {
	
	private static final Logger log = LogManager.getLogger(Log4j.class);
	
	@KafkaListener(topics = "logs", groupId = "logs_consumer_group")
	public void listenTransfer(
			  @Payload String message) throws IOException {
		
		System.out.println(message);
		log.info(message);
	
	}
}

