package af.gov.anar.ebreshna.common.nsc.service;

import af.gov.anar.ebreshna.common.nsc.model.ApplianceMaster;
import af.gov.anar.ebreshna.common.nsc.repository.ApplainceRepository;
import af.gov.anar.ebreshna.common.office.model.DesignationMaster;
import af.gov.anar.ebreshna.common.office.repository.DesignationMasterRepository;
import af.gov.anar.ebreshna.infrastructure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApplianceService {

    @Autowired
    private ApplainceRepository repository;

    @Autowired
    private UserService userService;

    public ApplianceMaster save(ApplianceMaster obj)
    {
        return repository.save(obj);
    }

    public List<ApplianceMaster> findall()
    {
        return repository.findAll();
    }

    public ApplianceMaster findOne(long id)
    {
        return repository.getOne(id);
    }

    public void delete(long id)
    {
        ApplianceMaster obj = repository.getOne(id);
        obj.setDeleted(true);
        obj.setUserId(userService.getId());
        obj.setDeletedAt(new Date());
        save(obj);
    }
}
