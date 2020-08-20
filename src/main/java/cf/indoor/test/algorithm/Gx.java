package cf.indoor.test.algorithm;

// WGS����תΪxy����

public class Gx {
	static double R = 6378137; // ����뾶 6378137
	static double R1 = 6356752;
	public static GetX x(double lon, double lat) {
		double x = R * lon * Math.cos(lat/180 *Math.PI)/180 *Math.PI;
		double y = R * lat/180 * Math.PI;
		GetX xy = new GetX(x,y);
//		System.out.println(x+ "," + y);
        return xy ;
	}
}
