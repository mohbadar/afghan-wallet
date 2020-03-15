/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package af.asr.notification.listener;


import af.asr.customer.CustomerEventConstants;
import af.asr.notification.ServiceConstants;
import af.asr.notification.service.NotificationService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


@SuppressWarnings("unused")
@Component
public class PortfolioEventListener {
	
	private final NotificationService notificationService;
	private final Logger logger;
	
	@Autowired
	public PortfolioEventListener(
			final NotificationService notificationService,
			@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger) {
		super();
		this.logger = logger;
		this.notificationService = notificationService;
	}
	
	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onOpen(final String payload) {
		logger.debug("Payload: " + payload);
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour loan request has been denied" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}
	
	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onDeny(final String payload) {
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour loan request has been denied" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}

	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onApprove(final String payload) {
		logger.info(payload);
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour loan has been Approved and waiting disbursal" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}
	
	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onDisburse(final String payload) {
		logger.info(payload.toString());
		this.notificationService.sendSMS("+23058409206",
			"Dear Valued Customer," +
					"\n\nYour loan has been disbursed" +
					"\n\nBest Regards" +
					"\nYour MFI");
	}

	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onAcceptPayment(final String payload) {
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour payment has been accepted" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}

	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onMarkLate(final String payload) {
		
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour payment for your loan is late" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}

	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onMarkInArrears(
	                            final String payload) {
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour payment has been marked as arrears for next payment" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}

	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onClose(
	                    final String payload) {
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour loan has been closed" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}

	@KafkaListener(topics = {CustomerEventConstants.DESTINATION}, groupId = "notification")
	public void onRecover(
	                      final String payload) {
		this.notificationService.sendSMS("+23058409206",
				"Dear Valued Customer," +
						"\n\nYour arrears have been recovered" +
						"\n\nBest Regards" +
						"\nYour MFI");
	}
}