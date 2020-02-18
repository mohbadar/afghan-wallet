package af.gov.anar.ebreshna.common.network.service;

import af.gov.anar.ebreshna.common.network.model.MeterReaderMaster;
import af.gov.anar.ebreshna.common.network.repository.MeterReaderMasterRepository;
import af.gov.anar.ebreshna.common.office.model.DesignationMaster;
import af.gov.anar.ebreshna.common.office.repository.DesignationMasterRepository;
import af.gov.anar.ebreshna.infrastructure.service.UserService;
import io.micrometer.core.instrument.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MeterReaderMasterService {

    @Autowired
    private MeterReaderMasterRepository repository;

    @Autowired
    private UserService userService;

    public MeterReaderMaster save(MeterReaderMaster obj)
    {
        return repository.save(obj);
    }

    public List<MeterReaderMaster> findall()
    {
        return repository.findAll();
    }

    public MeterReaderMaster findOne(long id)
    {
        return repository.getOne(id);
    }

    public void delete(long id)
    {
        MeterReaderMaster obj = repository.getOne(id);
        obj.setDeleted(true);
        obj.setUserId(userService.getId());
        obj.setDeletedAt(new Date());
        save(obj);
    }
}
