package af.gov.anar.ebreshna.configuration.billing.service;

import af.gov.anar.ebreshna.configuration.billing.model.CustomerGroupMaster;
import af.gov.anar.ebreshna.configuration.billing.model.TariffCharge;
import af.gov.anar.ebreshna.configuration.billing.model.TariffChargeDetail;
import af.gov.anar.ebreshna.configuration.billing.repository.CustomerGroupRepository;
import af.gov.anar.ebreshna.configuration.billing.repository.TariffChargeDetailRepository;
import af.gov.anar.ebreshna.infrastructure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TariffChargeDetailService {

    @Autowired
    private TariffChargeDetailRepository repository;

    @Autowired
    private UserService userService;

    public TariffChargeDetail save(TariffChargeDetail obj)
    {
        return repository.save(obj);
    }

    public List<TariffChargeDetail> findall()
    {
        return repository.findAll();
    }

    public TariffChargeDetail findOne(long id)
    {
        return repository.getOne(id);
    }

    public void delete(long id)
    {
        TariffChargeDetail obj = repository.getOne(id);
        obj.setDeleted(true);
        obj.setUserId(userService.getId());
        obj.setDeletedAt(new Date());
        save(obj);
    }
}
