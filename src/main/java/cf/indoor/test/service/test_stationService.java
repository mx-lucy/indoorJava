package cf.indoor.test.service;

import cf.indoor.test.dao.test_stationDAO;
import cf.indoor.test.pojo.test_station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class test_stationService {
    @Autowired
    test_stationDAO test_stationDAO;

    public List<test_station> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return test_stationDAO.findAll(sort);
    }

    public void addOrUpdate(test_station test_station) {
        test_stationDAO.save(test_station);
    }

    public void deleteById(int id) {
        test_stationDAO.deleteById(id);
    }

    public Optional<test_station> findStation(int id){
        return test_stationDAO.findById(id);
    }
}
