
package af.asr.notification.service;

import af.asr.notification.ServiceConstants;
import af.asr.notification.domain.Template;
import af.asr.notification.mapper.TemplateMapper;
import af.asr.notification.model.TemplateEntity;
import af.asr.notification.repository.TemplateRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class TemplateService {
	
	
	private final TemplateRepository templateRepository;
	private Logger logger;
	
	@Autowired
	public TemplateService(final TemplateRepository templateRepository,
	                       @Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger) {
		super();
		this.logger = logger;
		this.templateRepository = templateRepository;
	}
	public Optional<Template> findTemplateWithIdentifier(final String identifier) {
		return this.templateRepository.findByTemplateIdentifier(identifier).map(TemplateMapper::map);
	}
	
	public Boolean templateExists(final String identifier) {
		return this.templateRepository.existsByTemplateIdentifier(identifier);
	}
	
	public Boolean deleteTemplate(final String identifier) {
		//Todo: Remove html template and template record from repository
		return false;
	}

	@Transactional
	public String process(final Template template) {
		final TemplateEntity entity = TemplateMapper.map(template);
		this.templateRepository.save(entity);
		return template.getTemplateIdentifier();
	}
}
