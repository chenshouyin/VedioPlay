package vedioplay.csy.com.videoplay.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenshouyin on 2017/10/22.
 * 我的博客:http://blog.csdn.net/e_inch_photo
 * 我的Github:https://github.com/chenshouyin
 */

public class TimeUtils {

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort(Long time) {
        SimpleDateFormat formatter ;
        if (time>=60*60*60){
            formatter = new SimpleDateFormat("HH:mm:ss");
        }else{
            formatter = new SimpleDateFormat("mm:ss");
        }
        Date currentTime = new Date(time);
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
