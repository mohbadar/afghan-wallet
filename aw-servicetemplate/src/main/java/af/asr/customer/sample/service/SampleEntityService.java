package af.asr.customer.sample.service;

import af.asr.customer.sample.model.SampleEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Component
public interface SampleEntityService {

    public SampleEntity save(SampleEntity sampleEntity);
    public SampleEntity findById(long id);
    public List<SampleEntity> findAll();

    public void delete(long id);
}
