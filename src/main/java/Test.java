import java.util.Calendar;
import java.util.Random;

/**
 * Created by Azigar on 17.07.2015.
 */
public class Test {
    public static void main(String[] args){
        Calendar c = Calendar.getInstance();
        final int todayYear = c.get(Calendar.YEAR);
        final int todayMonth = c.get(Calendar.MONTH);
        final int todayDay = c.get(Calendar.DAY_OF_MONTH);

        String m = "07";
        int mi = Integer.parseInt(m);

        System.out.println(7==mi);

        System.out.println(todayDay + "." + (todayMonth + 1) + "." + todayYear);

        String a = "WHAT_BOUNTY", b = "WHAT_BOUNTy", d = "Name";
        System.out.println(a.equalsIgnoreCase(b));
    }
}
