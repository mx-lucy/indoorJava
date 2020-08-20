package cf.indoor.test.service;

import cf.indoor.test.dao.envFactorsDAO;
import cf.indoor.test.pojo.envFactors;

import cf.indoor.test.pojo.test_station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class envFactorsService {
    @Autowired
    envFactorsDAO envFactorsDAO;

    public List<envFactors> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return envFactorsDAO.findAll(sort);
    }

    public Optional<envFactors> findEnv(int id){
        return envFactorsDAO.findById(id);
    }
}
