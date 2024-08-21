package co.ke.daletsys.azyma.control;

import android.app.Activity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static String getFormatedNumber(String number){
        if(!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        }else{
            return "0";
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean isEmpty(String string){
        return string.equals("");
    }
    public static boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }
}
