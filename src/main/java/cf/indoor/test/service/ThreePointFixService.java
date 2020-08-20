package cf.indoor.test.service;

import cf.indoor.test.dao.envFactorsDAO;
import cf.indoor.test.dao.test_stationDAO;
import cf.indoor.test.pojo.*;
import cf.indoor.test.algorithm.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import java.sql.Timestamp;

@Service
public class ThreePointFixService {
    @Autowired
    envFactorsDAO envFactorsDAO;

    @Autowired
    test_stationDAO test_stationDAO;

    /*所有组合的总权值*/
    private double totalWeight;

    /*定位结果*/
    private Location location;

    public List<envFactors> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return envFactorsDAO.findAll(sort);
    }

    public List<test_station> list2() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return test_stationDAO.findAll(sort);
    }

    public Optional<envFactors> list3(int id){
        return envFactorsDAO.findById(id);
    }

    public Optional<test_station> list4(int id){
        return test_stationDAO.findById(id);
    }

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

        String maxRssiBaseId = uniqueBases.get(0).getId();

        /*根据rssi值最大的基站去数据库中查找相应的房间id(暂时使用默认值)*/
        int rooId = 1;
        location.setRoomId(rooId);

        /*拿到终端id*/
        String[] str1 = str.split(";");
        String terminalId = str1[str1.length-1];

        /*查找该终端对应的员工id(暂时使用默认值)*/
        String empId = "test";
        location.setEmpId(empId);

        /*求组合数*/
        Integer[] a = doGrouper.getA();
        CombineAlgorithm ca = null;

        try {
            ca = new CombineAlgorithm(a,3);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Object[][] c = ca.getResult();

        double[] tempLocation = new double[2];
        double[] weightLocation = calculate(uniqueBases);

//        for(int i = 0; i<c.length; i++){
//
//            /*创建一个列表，用来对每个组合进行计算*/
//            List<BleBase> triBases = new ArrayList<BleBase>();
//
//            for(int j = 0; j< uniqueBases.size(); j++){
//                System.out.println((int) c[i][j]);
//                BleBase bb = uniqueBases.get((int) c[i][j]);
//                System.out.println(bb);
//                triBases.add(bb);
//            }

//            /*三个基站为一组通过距离加权后求出的坐标*/
//            double[] weightLocation = calculate(triBases);
//
//            tempLocation[0]+=weightLocation[0];
//            tempLocation[1]+=weightLocation[1];
//        }
//
//        location.setxAxis(tempLocation[0]/totalWeight);
//        location.setxAxis(tempLocation[1]/totalWeight);
//
//        /*设置定位结果的时间戳*/
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
//		/*new Date()所做的事情其实就是调用了System.currentTimeMillis()。
//		如果仅仅是需要或者毫秒数，那么完全可以使用System.currentTimeMillis()
//		去代替new Date()，效率上会高一点。*/
//        location.setTimeStamp(ts);
        Coordinate rawCoor = triCentroid(new Round(2,1,1),new Round(1,2,1),new Round(1,3,2));
        location.setxAxis(rawCoor.getX());
        location.setyAxis(rawCoor.getY());
        return location;
    }

    /**
     * 求出通过该组基站距离加权后的坐标
     *
     * @param  bases 接收到的一组基站对象列表(此处列表中的基站应当是id各异的)
     * @return  返回通过该组基站距离加权后的坐标
     */
    public double[] calculate(List<BleBase> bases){

        /*基站的id与坐标*/
        Map<String, double[]> basesLocation =new HashMap<String, double[]>();

        /*距离数组*/
        double[] distanceArray = new double[3];

        String[] ids = new String[3];

        double[] rawLocation;

        double[] loc;

        int j = 0;

        /*得到环境影响因素的值*/

//        Optional<envFactors> envFactors = this.list3(0)this.list3(location.getRoomId());
        /*如果没有指定的环境因子,就用默认的*/
        Optional<envFactors> envFactors = this.list3(0);
//        if(envFactors == Optional.empty()){
//            envFactors = this.list3(0);
//        }

        double height = envFactors.get().getHeight();
        double n =  envFactors.get().getAtten_factor();
        double p0 =  envFactors.get().getP0();

        /*获得基站id*/
        for (BleBase base : bases) {
            ids[j] = base.getId();
            distanceArray[j] = base.getDistance(height, n, p0);
            j++;
//            System.out.println(base.getId());
//            System.out.println(base.getDistance(height, n, p0));
        }

        /*基站的坐标信息应当根据id去数据库中查找*/
        /*如果每次参加运算的基站数大于3，可以用StringBuilder拼接sql语句*/
        /*数组a初始化*/
        Round r[] = new Round[3];
        for(int i = 0 ;i< 3 ;i++) {
            int a = Integer.parseInt(ids[i]);
            Optional<test_station> station = this.list4(a);
            r[i] = new Round(station.get().getLat(), station.get().getLng(), distanceArray[i]);
            System.out.println(r[i]);
//            System.out.println(station.get().toString());
        }
//        basesLocation = test_server.baseStationLocs;
//		this.jdbcTemplate.query("select base_id,x_axis,y_axis from base_station where base_id in (?,?,?)",
//                new Object[] { ids[0],ids[1],ids[2]},
//                new RowCallbackHandler() {
//
//                    @Override
//                    public void processRow(ResultSet rs) throws SQLException {
//                    	double[] loc1 = new double[2];
//        				loc1[0]=rs.getDouble(2);
//        				loc1[1]=rs.getDouble(3);
//        				basesLocation.put(rs.getString(1), loc1);
//                    }
//        });
//


        for(int i = 0; i < 3; i ++ ) {
        }

		Coordinate rawCoor = triCentroid(r[0],r[1],r[2]);
//        Coordinate rawCoor = triCentroid(new Round(2,1,1),new Round(1,2,1),new Round(1,3,2));
        System.out.println(rawCoor);
        /*给未加权的结果数组赋值*/
        if(rawCoor == null) {
            return null;
        } else {
            rawLocation = new double[]{rawCoor.getX(),rawCoor.getY()};
            /*对应的权值*/
            double weight = 0;

            for(int i = 0; i<3; i++){
                weight+=(1.0/distanceArray[i]);
            }

            totalWeight+=weight;

            /*实例化坐标数组*/
            loc = new double[2];

            /*计算加权过后的坐标*/
            for(int i = 0; i < 2; i++) {
                loc[i] = rawLocation[i]*weight;
            }
            return loc;
        }
    }

//    求三角形质心算法
    public static Coordinate triCentroid(Round r1, Round r2, Round r3) {

        /*有效交叉点1*/
        Coordinate p1 = null;
        /*有效交叉点2*/
        Coordinate p2 = null;
        /*有效交叉点3*/
        Coordinate p3 = null;

        /*三点质心坐标*/
        Coordinate centroid = new Coordinate();

        /*r1,r2交点*/
        List<Coordinate> intersections1 = intersection(r1.getX(), r1.getY(), r1.getR(),
                r2.getX(), r2.getY(), r2.getR());

        if (intersections1 != null && !intersections1.isEmpty()) {
            for (Coordinate intersection :intersections1) {
                if (p1==null&&Math.pow(intersection.getX()-r3.getX(),2)
                        + Math.pow(intersection.getY()-r3.getY(),2) <= Math.pow(r3.getR(),2)) {
                    p1 = intersection;
                }else if(p1!=null){
                    if(Math.pow(intersection.getX()-r3.getX(),2) + Math.pow(intersection.getY()
                            -r3.getY(),2)<= Math.pow(r3.getR(),2)){
                        if(Math.sqrt(Math.pow(intersection.getX()-r3.getX(),2)
                                + Math.pow(intersection.getY()-r3.getY(),2))>Math.sqrt(Math.pow(p1.getX()
                                -r3.getX(),2) + Math.pow(p1.getY()-r3.getY(),2))){
                            p1 = intersection;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            return null;
        }

        /*r1,r3交点*/
        List<Coordinate> intersections2 = intersection(r1.getX(), r1.getY(), r1.getR(),
                r3.getX(), r3.getY(), r3.getR());

        if (intersections2 != null && !intersections2.isEmpty()) {
            for (Coordinate intersection : intersections2) {//有交点
                if (p2==null&&Math.pow(intersection.getX()-r2.getX(),2)
                        + Math.pow(intersection.getY()-r2.getY(),2) <= Math.pow(r2.getR(),2)) {
                    p2 = intersection;

                }else if(p2!=null){
                    if(Math.pow(intersection.getX()-r2.getX(),2) + Math.pow(intersection.getY()
                            -r2.getY(),2) <= Math.pow(r2.getR(),2)){
                        if(Math.pow(intersection.getX()-r2.getX(),2) + Math.pow(intersection.getY()
                                -r2.getY(),2)>Math.sqrt(Math.pow(p2.getX()-r2.getX(),2)
                                + Math.pow(p2.getY()-r2.getY(),2))){
                            p1 = intersection;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            return null;
        }

        /*r1,r2交点*/
        List<Coordinate> intersections3 = intersection(r2.getX(), r2.getY(), r2.getR(),
                r3.getX(), r3.getY(), r3.getR());

        if (intersections3 != null && !intersections3.isEmpty()) {
            for (Coordinate intersection : intersections3) {//有交点
                if (Math.pow(intersection.getX()-r1.getX(),2)
                        + Math.pow(intersection.getY()-r1.getY(),2) <= Math.pow(r1.getR(),2)) {
                    p3 = intersection;
                }else if(p3!=null){
                    if(Math.pow(intersection.getX()-r1.getX(),2) + Math.pow(intersection.getY()
                            -r1.getY(),2) <= Math.pow(r1.getR(),2)){
                        if(Math.pow(intersection.getX()-r1.getX(),2) + Math.pow(intersection.getY()
                                -r1.getY(),2)>Math.sqrt(Math.pow(p3.getX()-r1.getX(),2)
                                + Math.pow(p3.getY()-r1.getY(),2))){
                            p3 = intersection;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            return null;
        }

        /*质心*/
        centroid.setX((p1.getX()+p2.getX()+p3.getX())/3);
        centroid.setY((p1.getY()+p2.getY()+p3.getY())/3);

        return centroid;
    }

//    求两个圆的交点
    public static List<Coordinate> intersection(double x1, double y1, double r1,
                                                double x2, double y2, double r2) {

        double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));// 两圆心距离

        if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) < (r1 + r2)) {// 两圆相交

        }

        List<Coordinate> points  =new ArrayList<Coordinate>();//交点坐标列表

        Coordinate coor;

        if (d > r1 + r2 || d < Math.abs(r1 - r2)) {//相离或内含
            return null;
        } else if (x1 == x2 && y1 == y2) {//同心圆
            return null;
        } else if (y1 == y2 && x1 != x2) {
            double a = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2)) / (2 * x2 - 2 * x1);
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {// 只有一个交点时
                coor=new Coordinate();
                coor.setX(a);
                coor.setY(y1);
                points.add(coor);
            } else{// 两个交点
                double t = r1 * r1 - (a - x1) * (a - x1);
                coor=new Coordinate();
                coor.setX(a);
                coor.setY(y1 + Math.sqrt(t));
                points.add(coor);
                coor = new Coordinate();
                coor.setX(a);
                coor.setY(y1 - Math.sqrt(t));
                points.add(coor);
            }
        } else if (y1 != y2) {
            double k, disp;
            k = (2 * x1 - 2 * x2) / (2 * y2 - 2 * y1);
            disp = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2) - (y1 * y1 - y2 * y2))
                    / (2 * y2 - 2 * y1);// 直线偏移量
            double a, b, c;
            a = (k * k + 1);
            b = (2 * (disp - y1) * k - 2 * x1);
            c = (disp - y1) * (disp - y1) - r1 * r1 + x1 * x1;
            double disc;
            disc = b * b - 4 * a * c;// 一元二次方程判别式
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {
                coor=new Coordinate();
                coor.setX((-b) / (2 * a));
                coor.setY(k * coor.getX() + disp);
                points.add(coor);
            } else {
                coor=new Coordinate();
                coor.setX(((-b) + Math.sqrt(disc)) / (2 * a));
                coor.setY(k * coor.getX() + disp);
                points.add(coor);
                coor=new Coordinate();
                coor.setX(((-b) - Math.sqrt(disc)) / (2 * a));
                coor.setY(k * coor.getX() + disp);
                points.add(coor);
            }
        }
        return points;
    }

}