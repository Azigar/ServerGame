import java.util.Random;

/**
 * Created by Azigar on 15.07.2015.
 */
public class Rand {
    public static void main(String[] args){
        Random random = new Random();
        for (int i = 0; i < 1; i++){
            System.out.println(random.nextInt(3) + 1);
        }
    }
}
