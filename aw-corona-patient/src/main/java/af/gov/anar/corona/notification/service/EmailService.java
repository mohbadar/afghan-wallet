
package af.gov.anar.corona.notification.service;


import af.gov.anar.corona.notification.ServiceConstants;
import af.gov.anar.corona.notification.domain.EmailConfiguration;
import af.gov.anar.corona.notification.mapper.EmailConfigurationMapper;
import af.gov.anar.corona.notification.model.EmailGatewayConfigurationEntity;
import af.gov.anar.corona.notification.repository.EmailGatewayConfigurationRepository;
import af.gov.anar.corona.notification.util.MailBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Component
public class EmailService {
	
	private final EmailGatewayConfigurationRepository emailGatewayConfigurationRepository;
	boolean isConfigured;
	private JavaMailSenderImpl mailSender;
	private MailBuilder mailBuilder;
	private Logger logger;
	
	@Autowired
	public EmailService(final EmailGatewayConfigurationRepository emailGatewayConfigurationRepository,
	                    final MailBuilder mailBuilder,
	                    @Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger) {
		super();
		this.isConfigured = false;
		this.logger = logger;
		this.mailSender = new JavaMailSenderImpl();
		this.emailGatewayConfigurationRepository = emailGatewayConfigurationRepository;
		this.mailBuilder = mailBuilder;
	}
	
	public boolean configureEmailGatewayWithDefaultGateway() {
		EmailConfiguration configuration = getDefaultEmailConfigurationEntity().get();
		return setNewConfiguration(configuration);
	}
	
	public List<EmailConfiguration> findAllEmailConfigurationEntities() {
		return EmailConfigurationMapper.map(this.emailGatewayConfigurationRepository.findAll());
	}
	
	public Optional<EmailConfiguration> getDefaultEmailConfigurationEntity() {
		return this.emailGatewayConfigurationRepository.defaultGateway().map(EmailConfigurationMapper::map);
	}
	
	public Optional<EmailConfiguration> findEmailConfigurationByIdentifier(final String identifier) {
		return this.emailGatewayConfigurationRepository.findByIdentifier(identifier).map(EmailConfigurationMapper::map);
	}
	
	public Boolean emailConfigurationExists(final String identifier) {
		return this.emailGatewayConfigurationRepository.existsByIdentifier(identifier);
	}
	
	boolean setNewConfiguration(String identifier) {
		EmailConfiguration configuration = findEmailConfigurationByIdentifier(identifier).get();
		return setNewConfiguration(configuration);
	}
	
	private boolean setNewConfiguration(EmailConfiguration configuration) {
		try {
			this.mailSender.setHost(configuration.getHost());
			this.mailSender.setPort(Integer.parseInt(configuration.getPort()));
			this.mailSender.setUsername(configuration.getUsername());
			this.mailSender.setPassword(configuration.getApp_password());
			
			Properties properties = new Properties();
			properties.put(ServiceConstants.MAIL_TRANSPORT_PROTOCOL_PROPERTY, configuration.getProtocol());
			properties.put(ServiceConstants.MAIL_SMTP_AUTH_PROPERTY, configuration.getSmtp_auth());
			properties.put(ServiceConstants.MAIL_SMTP_STARTTLS_ENABLE_PROPERTY, configuration.getStart_tls());
			this.mailSender.setJavaMailProperties(properties);
			this.isConfigured = true;
			return true;
		} catch (RuntimeException ignore) {
			logger.error("Failed to configure the Email Gateway");
		}
		return false;
	}
	
	public String sendPlainEmail(String to, String subject, String message) {
		SimpleMailMessage mail = new SimpleMailMessage();
		
		try {
			mail.setTo(to);
			mail.setSubject(subject);
			mail.setText(message);
			this.mailSender.send(mail);
			return to;
		} catch (MailException exception) {
			logger.debug("Caused by:" + exception.getCause().toString());
		}
		return to;
	}
	
	public String sendFormattedEmail(String to,
	                                 String subject,
	                                 Map<String, Object> message,
	                                 String emailTemplate) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			String content = mailBuilder.build(message, emailTemplate);
			messageHelper.setText(content, true);
		};
		try {
			this.mailSender.send(messagePreparator);
			return to;
		} catch (MailException e) {
			logger.error("Failed to send Formatted email{}", e.getMessage());
		}
		return null;
	}



	@Transactional
	public String createEmailConfiguration(final EmailConfiguration emailConfiguration) {
		final EmailGatewayConfigurationEntity entity = EmailConfigurationMapper.map(emailConfiguration);
		this.emailGatewayConfigurationRepository.save(entity);

		return emailConfiguration.getIdentifier();
	}

	@Transactional
	public String updateEmailConfiguration(final EmailConfiguration emailConfiguration) {
		final EmailGatewayConfigurationEntity newEntity = EmailConfigurationMapper.map(emailConfiguration);
		this.emailGatewayConfigurationRepository.deleteEmailGatewayConfigurationEntityByIdentifier(newEntity.getIdentifier());
		this.emailGatewayConfigurationRepository.save(newEntity);

		return emailConfiguration.getIdentifier();
	}

	@Transactional
	public String deleteEmailConfiguration(final String identifier) {
		this.emailGatewayConfigurationRepository.deleteEmailGatewayConfigurationEntityByIdentifier(identifier);
		return identifier;
	}
}
