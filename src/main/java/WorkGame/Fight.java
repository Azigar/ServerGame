package WorkGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by Azigar on 25.05.2015.
 */
public class Fight {

    //текст с ресурсов
    static String txtG = " в голову";
    static String txtT = " в торс";
    static String txtN = " в ноги";

    static String txtUdar = null;

    //метод боя и возврат значение ОЗ противника
    public static int Fight(boolean liveAtk, String nameAtk, int minUronAtk, int maxUronAtk, int dexAtk, int instAtk,
                            boolean liveDef, String nameDef, int dexDef, int instDef, int defDef, int hpDef) throws IOException {
        int uron=0;
        txtHod(nameAtk); //Вывод текста о том чей ход
        //Определяет шас блока
        boolean shansBlokAtk = ShansBlok(liveAtk, liveDef, nameAtk, nameDef);
        //попадет ли атакующий игрок
        boolean shansDexAtk = Shans(dexAtk, dexDef);
        //нанесет ли он критический урон
        boolean shansKritAtk = Shans(instAtk, instDef);
        //если попал в противника
        if (shansDexAtk == true) {
            //и не попал в блок
            if (shansBlokAtk == false) {
                //и нанес крит урон
                if (shansKritAtk == true) {
                    //расчет силы КРИТИЧЕСОГО Урона
                    uron = SrtKritUron(minUronAtk, maxUronAtk, defDef, nameAtk);
                } else { //нанес обычный удар
                    //расчет силы урона
                    uron = SrtUron(minUronAtk, maxUronAtk, defDef, nameAtk);
                }
                //попал в блок
            } else {
                //и нанес крит урон  (пробил блок)
                if (shansKritAtk == true) {
                    //расчет силы урона
                    uron = SrtUronBlok(minUronAtk, maxUronAtk, defDef, nameAtk);
                } else {
                    int txt = randTxt();
                    switch (txt) {
                        case 1:
                            System.out.println(nameDef + " удачно блокировал удар");
                            break;
                        case 2:
                            System.out.println(nameDef + " умело парировал удар");
                            break;
                    }
                }
            }
            //промазал
        } else {
            int txt = randTxt();
            switch (txt) {
                case 1:
                    System.out.println(nameAtk + " промазал");
                    break;
                case 2:
                    System.out.println(nameDef + " уклонился от удара");
                    break;
            }
        }
        //уменшаем к-во ОЗ противника
        hpDef = hpDef - uron;
        return hpDef;
    }

    //расчет шанса
    public static boolean Shans(int statAtk, int statDef) {
        //первоначально шанс-лож
        boolean i = false;
        //расчет шанса 20+(стат+лвл)-(стат+лвл)
        double shans = (50 + (statAtk - statDef)) / 100;
        //всегда есть антишанс
        if (shans >= 0.87) shans = 0.87;
        if (shans <= 0.13) shans = 0.13;
        String str = Double.toString(shans); //перевод double в String
        //генератор случаных чисел от 0,1 до 1
        double random = Math.random();
        //если раном удолетворительный, тогда шасн-правда
        if (random <= shans) i = true;
        //возвращаю значение
        return i;
    }

    //расчет силы урона
    private static int Str(int strmin, int strmax) {
        Random random = new Random();
        return random.nextInt(strmin + 1) + (strmax - strmin);
    }

    //рандом текста
    private static int randTxt() {
        Random random = new Random();
        return random.nextInt(2) + 1;
    }

    //рандом направление атаки/защиты
    private static int randUdar() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    //текстовка Начало нового хода
    private static void txtHod(String name) {
        System.out.println();
        System.out.println("Ход " + name + "а");
    }

    //Шанс блока
    private static boolean ShansBlok(boolean liveAtk, boolean liveDef, String nameAtk, String nameDef) throws IOException {
        boolean shansBlok = false; // блок - лож
        int udarAtk, udarDef;
        //вибор направление удара АТАКующего игрока
        udarAtk = Udar(liveAtk, nameAtk);
        //вибор направление защиты защищающего игрока
        udarAtk = Udar(liveDef, nameDef);
        //текстовка для удара
        switch (udarAtk) {
            case 1:
                txtUdar = txtG;
                break;
            case 2:
                txtUdar = txtT;
                break;
            case 3:
                txtUdar = txtN;
        }
        //если выбор направлене совпадает, то бок - правда
        if (udarAtk==udarAtk) shansBlok = true;
        return shansBlok;
    }

    //вибор направление удара/защиты
    private static int Udar(boolean live, String name) throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        int udar;
        if (live == true) { //если игрок не БОТ
            do {
                System.out.println(name + ", виберите направление: 1 -" + txtG + ", 2 -" + txtT + ", 3 -" + txtN);
                udar = Integer.parseInt(read.readLine());
                if ((udar == 1) || (udar == 2) || (udar == 3)) break;
            } while (true);
        } else udar = randUdar();
        return udar;
    }

    //расчет силы КРИТИЧЕСОГО Урона
    private static int SrtKritUron(int srtMin, int srtMax, int def, String name) {
        int srtKritUron = (Str(srtMin, srtMax) * 2) - (def / 10);
        System.out.println(name + " нанес КРИТИЧЕСКИЙ удар" + txtUdar + " (урон " + srtKritUron + "). ");
        return srtKritUron;
    }

    //расчет силы Обычного Урона
    private static int SrtUron(int srtMin, int srtMax, int def, String name) {
        int srtUron = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUron <= ave)
            System.out.println(name + " нанес незначительный удар" + txtUdar + " (урон " + srtUron + "). ");
        else
            System.out.println(name + " нанес серьезный удар" + txtUdar + " (урон " + srtUron + "). ");
        return srtUron;
    }

    //расчет силы Урона через блок
    private static int SrtUronBlok(int srtMin, int srtMax, int def, String name) {
        int srtUronBlok = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUronBlok <= ave)
            System.out.println(name + " пробил блок и нанес незначительный удар" + txtUdar + " (урон " + srtUronBlok + "). ");
        else
            System.out.println(name + " пробив блок противника, нанес серьезный удар" + txtUdar + " (урон " + srtUronBlok + "). ");
        return srtUronBlok;
    }

    //текстовка о том, кто первый напал
    static void TxtFist(String nameAtk, String nameDef) {
        int txt = randTxt();
        switch (txt) {
            case 1:
                System.out.println(nameAtk + " яросно бросился на " + nameDef + "а");
                break;
            case 2:
                System.out.println(nameAtk + " первым настиг " + nameDef + "а");
                break;
        }
    }

    static boolean Death(int hp, String nameAtk, String nameDef){
        boolean death = false;
        //если защитника убили
        if (hp <= 0) {
            System.out.println();
            int txt = randTxt();
            switch (txt) {
                case 1:
                    System.out.println(nameAtk + " убил " + nameDef);
                    break;
                case 2:
                    System.out.println(nameDef + " погиб");
                    break;
            }
            death = true;
        }
        return death;
    }

    static void TxtHP(String nameAtk, int hpAtk, String nameDef, int hpDef){
        System.out.println(nameAtk + " - " +hpAtk+"  HP"); //вивод начального к-ва ОЗ
        System.out.println(nameDef + " - " +hpDef+"  HP"); //вивод начального к-ва ОЗ;
    }
}


