package Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
    public static Date format(String time) throws ParseException {
        return simpleDateFormat.parse(time);
    }
}
