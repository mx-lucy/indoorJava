package cf.indoor.test.controller;

import cf.indoor.test.dao.test_stationDAO;
import cf.indoor.test.pojo.Book;
import cf.indoor.test.pojo.test_station;
import cf.indoor.test.service.test_stationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class test_stationController {
    @Autowired
    test_stationService test_stationService;

    //查询显示全部信息
    @GetMapping("/api/test_station")
    public List<test_station> list() throws Exception {
        return test_stationService.list();
    }

    // 添加或者修改信息
    @PostMapping("/api/test_station")
    public test_station addOrUpdate(@RequestBody test_station test_station) throws Exception {
        test_stationService.addOrUpdate(test_station);
        return test_station;
    }

    //删除指定id信息
    @PostMapping("/api/test_station/delete/{id}")
    public void delete(@PathVariable("id") int id) throws Exception {
        test_stationService.deleteById(id);
    }

//    // 查询指定信息
//    @GetMapping("/api/test_station")
//    public List<test_station> list(@RequestBody test_station test_station) throws Exception {
//        return test_stationService.list();
//    }

    //查询指定id的基站
    @GetMapping("/api/test_station/search/{id}")
    public Optional<test_station> search(@PathVariable("id") int id) throws Exception{
        return test_stationService.findStation(id);
    }
}
