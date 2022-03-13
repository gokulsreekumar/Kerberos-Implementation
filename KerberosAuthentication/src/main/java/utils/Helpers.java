package utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

public class Helpers {
    public Helpers() {
    }

    public static int generateNonce(int noOfBits) {
        Random random = new Random();
        return random.nextInt(noOfBits);
    }

    public static Timestamp addDays(Timestamp time, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.DATE, days);
        return new Timestamp(cal.getTime().getTime());
    }
}
