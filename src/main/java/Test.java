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

        Random rand = new Random();
        String pass = "";
        String[] s = new String[]{"q","Q","w","W","e","E","r","R","t","T","y","Y","u","U","i","I","o","O","p","P",
                "a","A","s","S","d","D","f","F","g","G","h","H","j","J","k","K","l","L",
                "z","Z","x","X","c","C","v","V","b","B","n","N","m","M",
                "1","2","3","4","5","6","7","8","9","0"};
        for (int i = 1; i <= 15; i++) {
            pass = pass + (s[rand.nextInt(s.length)]);
        }
        System.out.println(pass);

        String s2 = "www.mysite@gmail.com";
        System.out.println(s2.endsWith("@gmail.com"));

        int [][] objects  = new int[10][3];
        for (int i = 0; i < objects.length; i++) {
            objects[i][0] = (3);
            objects[i][1] = (2);
            objects[i][2] = (4);
        }
        System.out.println("Вывожу массив вещей в рюкзаке");
        for(int i = 0; i < objects.length; i++){
            for(int j = 0; j<objects[i].length; j++){
                System.out.print(objects[i][j] + " ");
            }
            System.out.println();
        }
    }
}
