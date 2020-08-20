package cf.indoor.test.pojo;

import java.util.ArrayList;
import java.util.List;

//封装的分组
public class Group {

    /*rssi值列表*/
    private List<Integer> rssis = new ArrayList<Integer>();

    public List<Integer> getRssis() {
        return rssis;
    }

    public void setRssis(List<Integer> rssis) {
        this.rssis = rssis;
    }
}