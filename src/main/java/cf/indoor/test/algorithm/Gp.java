package cf.indoor.test.algorithm;

// xy����תΪWGS����

public class Gp {
	static double R = 6378137; 
	public static GetX p(double x, double y) {
		double lat = y/R *180 /Math.PI;
		double lon = x/(R * Math.cos(lat/180 *Math.PI))*180 /Math.PI;
		GetX xy = new GetX(lon, lat);
//		System.out.println(x+ "," + y);
        return xy ;
	}
}
