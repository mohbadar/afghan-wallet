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
package af.asr.notification.api;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.notification.service.ServiceConstants;
import org.apache.fineract.cn.notification.service.internal.command.InitializeServiceCommand;
import org.apache.fineract.cn.notification.service.internal.service.NotificationService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/")
public class NotificationRestController {
	
	private final Logger logger;
	private final CommandGateway commandGateway;
	private final NotificationService notificationService;
	
	@Autowired
	public NotificationRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
	                                  final CommandGateway commandGateway,
	                                  final NotificationService notificationService) {
		super();
		this.logger = logger;
		this.commandGateway = commandGateway;
		this.notificationService = notificationService;
	}
	
	@Permittable(value = AcceptedTokenType.SYSTEM)
	@RequestMapping(
			value = "/initialize",
			method = RequestMethod.POST,
			consumes = MediaType.ALL_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public
	@ResponseBody
    ResponseEntity<Void> initialize() throws InterruptedException {
		this.commandGateway.process(new InitializeServiceCommand());
		return ResponseEntity.accepted().build();
	}
	
}
