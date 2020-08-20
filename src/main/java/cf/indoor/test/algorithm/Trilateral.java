package cf.indoor.test.algorithm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cf.indoor.test.algorithm.Dealer; // 父接口
import cf.indoor.test.pojo.BleBase; // 基站信息类
import cf.indoor.test.pojo.Location; // 定位结果类
import cf.indoor.test.pojo.envFactors;
import cf.indoor.test.pojo.test_station;
import cf.indoor.test.service.ThreePointFixService;
//import org.hqu.indoor_pos.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import Jama.Matrix;
import cf.indoor.test.algorithm.GetX;
import cf.indoor.test.algorithm.Gp;
import cf.indoor.test.algorithm.Gx;
import org.thymeleaf.engine.IterationStatusVar;

/**
 * 三边定位算法
 */
public class Trilateral implements Dealer {
    @Autowired
    ThreePointFixService ThreePointFixService;

    /*定位结果*/
    private Location location;
    private static JdbcTemplate jdbcTemplate;

    @Override
    public Location getLocation(String str){

        /*实例化定位结果*/
        location = new Location();

        /*分组*/
        DoGroup doGrouper = new DoGroup();
        ArrayList<BleBase> uniqueBases = doGrouper.doGroup(str);

        /*如果收到的基站个数小于3，不能定位，直接返回*/
        if(uniqueBases==null){
            return null;
        }

        /*拿到终端id*/
        String[] str1 = str.split(";");
        String terminalId = str1[str1.length-1];

        return calculate(uniqueBases,terminalId);
    }

    /**
     * 计算定位坐标
     * @return  返回定位坐标
     */
    public Location calculate(List<BleBase> bases, String terminalId){

        /*获取bases列表中的对象个数。size()就是获取到ArrayList中存储的对象的个数，*/
        int baseNum = bases.size();

        /*距离数组*/
        double[] distanceArray = new double[baseNum];

        String[] ids = new String[baseNum];

        int j = 0;

        /*得到环境影响因素的值*/
        List<envFactors> envFactorsTemp;
        envFactorsTemp = ThreePointFixService.list();
        envFactors env = envFactorsTemp.get(location.getRoomId());
        if(env == null){
            env = envFactorsTemp.get(0);
        }
        double height = env.getHeight();
        double n =  env.getAtten_factor();
        double p0 =  env.getP0();

//        Double[] envFactors = test_server.envFactors.get(location.getRoomId());
//        /*如果没有指定的环境因子,就用默认的*/
//        if(envFactors == null){
//            envFactors = test_server.envFactors.get(0);
//        }
//        double height = envFactors[0];
//        double n =  envFactors[1];
//        double p0 =  envFactors[2];

        /*获得基站id*/
        for (BleBase base : bases) {
            ids[j] = base.getId();
            distanceArray[j] = base.getDistance(height, n, p0);
            j++;
        }

        int disArrayLength = distanceArray.length;

        double[][] a = new double[baseNum-1][2];

        double[][] b = new double[baseNum-1][1];

        /*获取基站信息*/
        List<test_station> test_stationTemp;
        test_stationTemp = ThreePointFixService.list2();

//
//        /*数组a初始化*/
//        for(int i = 0; i < 2; i ++ ) {
//            a[i][0] = 2*(test_server.baseStationLocs.get(ids[i])[0]-test_server.baseStationLocs.get(ids[baseNum-1])[0]);
//            a[i][1] = 2*(test_server.baseStationLocs.get(ids[i])[1]-test_server.baseStationLocs.get(ids[baseNum-1])[1]);
//        }
//
//        /*数组b初始化*/
//        for(int i = 0; i < 2; i ++ ) {
//            b[i][0] = Math.pow(test_server.baseStationLocs.get(ids[i])[0], 2)
//                    - Math.pow(test_server.baseStationLocs.get(ids[baseNum-1])[0], 2)
//                    + Math.pow(test_server.baseStationLocs.get(ids[i])[1], 2)
//                    - Math.pow(test_server.baseStationLocs.get(ids[baseNum-1])[1], 2)
//                    + Math.pow(distanceArray[disArrayLength-1], 2)
//                    - Math.pow(distanceArray[i],2);
//        }
//
//        /*将数组封装成矩阵*/
//        Matrix b1 = new Matrix(b);
//        Matrix a1 = new Matrix(a);
//
//        /*求矩阵的转置*/
//        Matrix a2  = a1.transpose();
//
//        /*求矩阵a1与矩阵a1转置矩阵a2的乘积*/
//        Matrix tmpMatrix1 = a2.times(a1);
//        Matrix reTmpMatrix1 = tmpMatrix1.inverse();
//        Matrix tmpMatrix2 = reTmpMatrix1.times(a2);
//
//        /*中间结果乘以最后的b1矩阵*/
//        Matrix resultMatrix = tmpMatrix2.times(b1);
//        double[][] resultArray = resultMatrix.getArray();
//
//        GetX p1 = Gp.p(resultArray[0][0], resultArray[1][0]); // 坐标转换
//        location.setxAxis(p1.getXc());
//        location.setyAxis(p1.getYc());
//
//        /*根据rssi值最大的基站查找相应的坐标系id*/
//        location.setRoomId(test_server.roomIds.get(ids[baseNum-1]));
////		System.out.print(location.getRoomId());
//
//        /*查找该终端对应的员工id*/
//        location.setEmpId(test_server.empIds.get(terminalId));
//
//        /*设置定位结果的时间戳*/
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
//        location.setTimeStamp(ts);
//        intomysql(location, terminalId);
        return location;
    }
//
//    public void intomysql( Location location, String terminalId) {
//        jdbcTemplate = (JdbcTemplate) SpringUtil.context.getBean("jdbcTemplate");
//        System.out.println(location.getRoomId() + ","+location.getxAxis()+","+ location.getyAxis() + ","+ terminalId);
//        String sql = "INSERT INTO test_result(num_id, Lng, Lat, room_id) VALUES("+terminalId+","+location.getxAxis()+","+ location.getyAxis()+","+ location.getRoomId()  +")";//使用拼接的方式传递参数
//        jdbcTemplate.update(sql);
//    }
}
