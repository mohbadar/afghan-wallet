package af.gov.anar.ebreshna.configuration.network.area;

import af.gov.anar.ebreshna.configuration.network.area.AreaMaster;
import af.gov.anar.ebreshna.configuration.network.area.AreaMasterRepository;
import af.gov.anar.ebreshna.infrastructure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AreaMasterService {

    @Autowired
    private AreaMasterRepository repository;

    @Autowired
    private UserService userService;

    public AreaMaster save(AreaMaster obj)
    {
        return repository.save(obj);
    }

    public List<AreaMaster> findAll()
    {
        return repository.findAll();
    }

    public AreaMaster findOne(long id)
    {
        return repository.getOne(id);
    }

    public void delete(long id)
    {
        AreaMaster obj = repository.getOne(id);
        obj.setDeleted(true);
        obj.setUserId(userService.getId());
        obj.setDeletedAt(new Date());
        save(obj);
    }
}
