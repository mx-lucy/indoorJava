package cf.indoor.test.controller;


import cf.indoor.test.algorithm.Centroid;
import cf.indoor.test.algorithm.Trilateral;
import cf.indoor.test.pojo.Location;
import cf.indoor.test.pojo.envFactors;
import cf.indoor.test.pojo.test_station;
import cf.indoor.test.service.ThreePointFixService;
import cf.indoor.test.service.test_stationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class AlgorithmController {

    @Autowired
    ThreePointFixService ThreePointFixService;

    @Autowired
    test_stationService test_stationService;

    public static Centroid Centroid;

    public static Trilateral dealer;
    @GetMapping("/api/Algorithm")
    public List<envFactors> list() throws Exception
    {
        dealer = new Trilateral();
//        dealer.test();
        return ThreePointFixService.list();
    }

    @GetMapping("/api/Algorithm1")
    public List<test_station> list1() throws Exception{
        return ThreePointFixService.list2();
    }

    @GetMapping("/api/Algorithm2")
    public String list2() throws Exception{
        List<envFactors> envFactorsTemp;
        envFactorsTemp = ThreePointFixService.list();
        String s = null;
        envFactors env = envFactorsTemp.get(0);
        double h = env.getHeight();
        s += h;
//        for(envFactors a : envFactorsTemp) {
//            s = s + a;
//        }
        return s;
    }

    @GetMapping("/api/Algorithm3")
    public String list3() throws Exception{
        Location Location;
        String str = "1,-713;6,-716;3,-919;4,-717;869511023026828;";
        Location = ThreePointFixService.getLocation(str);
//        System.out.print(1);
//        return Location;
        return str;
//        return ThreePointFixService.list2();
    }

    //查询指定id的基站
    @GetMapping("/api/Algorithm4")
    public Optional<test_station> list4() throws Exception{
        return test_stationService.findStation(1);
    }

    @GetMapping("/api/Algorithm5")
    public Optional<envFactors> list5() throws Exception{
        return ThreePointFixService.list3(0);
//        return ThreePointFixService.list2();
    }
}
