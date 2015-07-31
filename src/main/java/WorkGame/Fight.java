package WorkGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by Azigar on 02.06.2015.
 */
public class Fight {

    private String txtUdar;
    private String txtComent;
    private int koef_dex = 15; //коефициент для шанса уворота
    private int koef_inst = 35; //коефициент для шанса крита
    private int uron;
    private static String s1Atk, s2Atk, s3Atk;
    private static String s1Def, s2Def, s3Def;

    //метод боя и возврат значение ОЗ противника
    public int Fight(boolean liveAtk, String nameAtk, int strAtk, double dexAtk, double instAtk, boolean liveDef, String nameDef,
                     double dexDef, double instDef, int defDef, int hpDef, int nap, int sexAtk, int sexDef){
        SEX(sexAtk, sexDef); //мальчик-девочка
        setUron(0); //обнуляю урон
        int minUronAtk = strAtk / 3, maxUronAtk = strAtk + (strAtk / 3);
        if (Shans(dexAtk, dexDef, koef_dex) == false) {  //если попал в противника
            if (ShansBlok(liveAtk, liveDef, nameAtk, nameDef, nap) == true) {  //и не попал в блок
                if (Shans(instAtk, instDef, koef_inst) == true) {  //и нанес крит урон
                    setUron(SrtKritUron(minUronAtk, maxUronAtk, defDef, nameAtk)); //расчет силы КРИТИЧЕСОГО Урона
                } else { //нанес обычный удар
                    setUron(SrtUron(minUronAtk, maxUronAtk, defDef, nameAtk));  //расчет силы урона
                }
            } else { //попал в блок
                if (Shans(instAtk, instDef, koef_inst) == true) {  //но нанес крит урон - пробил блок
                    setUron(SrtUronBlok(minUronAtk, maxUronAtk, defDef, nameAtk));  //расчет силы урона
                } else {  //обычным ударом
                    int txt = randTxt();
                    switch (txt) {
                        case 1:
                            if (sexDef == 1)
                            setTxtComent(nameDef + " удачно блокировал" + s1Def + " удар");
                            break;
                        case 2:
                            setTxtComent(nameDef + " умело парировал" + s1Def + " удар");
                            break;
                        case 3:
                            setTxtComent(nameAtk + " попал" + s1Atk + " в блок");
                            break;
                    }
                }
            }
        } else {  //промазал
            int txt = randTxt();
            switch (txt) {
                case 1:
                    setTxtComent(nameAtk + " промазал" + s1Atk);
                    break;
                case 2:
                    setTxtComent(nameDef + " уклонил" + s1Def + "с" + s2Def + " от удара");
                    break;
                case 3:
                    setTxtComent(nameDef + " ловко увернул" + s1Def + "с" + s2Def);
                    break;
            }
        }
        hpDef = hpDef - getUron();  //уменшаем к-во ОЗ противника
        return hpDef;
    }

    //мальчик-девочка
    private static void SEX(int sex1, int sex2) {
        if (sex1 == 1){
            s1Atk = "";
            s2Atk = "я";
            s3Atk = "";
        }
        else {
            s1Atk = "а";
            s2Atk = "ь";
            s3Atk = "ла";
        }
        if (sex2 == 1){
            s1Def = "";
            s2Def = "я";
            s3Def = "";
        }
        else {
            s1Def = "а";
            s2Def = "ь";
            s3Def = "ла";
        }
    }

    //расчет
    public static boolean Shans(double statAtk, double statDef, int koef) {
        boolean chance = false;  //первоначально шанс-лож
        double shans = (koef + (statAtk - statDef)) / 100;  //расчет шанса (коеф+(стат+лвл)-(стат+лвл)) /100
        if (shans >= 0.87) shans = 0.87;  //всегда есть антишанс
        if (shans <= 0.13) shans = 0.13;  //всегда есть шанс
        double random = Math.random();  //генератор случаных чисел от 0,1 до 1
        if (random <= shans) { chance = true; } //если раном удолетворительный, тогда шасн-правда
        return chance;  //возвращаю значение
    }

    //расчет силы урона
    private static int Str(int strmin, int strmax) {
        Random random = new Random();
        return random.nextInt(strmin + 1) + (strmax - strmin);
    }

    //рандом направление атаки/защиты
    private static int randUdar() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    //Шанс блока
    private boolean ShansBlok(boolean liveAtk, boolean liveDef, String nameAtk, String nameDef, int nap){
        boolean shansBlok = false; // блок - лож
        int udarAtk, udarDef;
        //вибор направление удара АТАКующего игрока
        udarAtk = Udar(liveAtk, nameAtk, nap);
        //вибор направление защиты защищающего игрока
        udarDef = Udar(liveDef, nameDef, nap);
        //текстовка для удара
        switch (udarAtk) {
            case 1:
                txtUdar = " в голову";
                break;
            case 2:
                txtUdar = " в торс";
                break;
            case 3:
                txtUdar = " в ноги";
        }
        //если выбор направлене совпадает, то блок - правда
        if (udarAtk==udarDef) shansBlok = true;
        return shansBlok;
    }

    //вибор направление удара/защиты
    private static int Udar(boolean live, String name, int nap){
        int udar;
        if (live == true) udar=nap;
        else udar = randUdar();
        return udar;
    }

    //расчет силы КРИТИЧЕСОГО Урона
    private int SrtKritUron(int srtMin, int srtMax, int def, String name) {
        int srtKritUron = (Str(srtMin, srtMax) * 2) - (def / 10);
        setTxtComent(name + " нанес" + s3Atk + " КРИТИЧЕСКИЙ удар" + txtUdar + " (урон " + srtKritUron + ")");
        return srtKritUron;
    }

    //расчет силы Обычного Урона
    private int SrtUron(int srtMin, int srtMax, int def, String name) {
        int srtUron = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUron <= ave)
            setTxtComent(name + " нанес" + s3Atk + " незначительный удар" + txtUdar + " (урон " + srtUron + ")");
        else
            setTxtComent(name + " нанес" + s3Atk + " серьезный удар" + txtUdar + " (урон " + srtUron + ")");
        return srtUron;
    }

    //расчет силы Урона через блок
    private int SrtUronBlok(int srtMin, int srtMax, int def, String name) {
        int srtUronBlok = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUronBlok <= ave)
            setTxtComent(name + " пробил" + s1Atk + " блок и нанес" + s3Atk + " незначительный удар" + txtUdar + " (урон " + srtUronBlok + ")");
        else
            setTxtComent(name + " пробив блок, нанес" + s3Atk + " серьезный удар" + txtUdar + " (урон " + srtUronBlok + ")");
        return srtUronBlok;
    }

    //рандом текста
    public static int randTxt() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    public String getTxtComent() {
        return txtComent;
    }

    public void setTxtComent(String txtComent) {
        this.txtComent = txtComent;
    }

    public int getUron() {
        return uron;
    }

    public void setUron(int uron) {
        this.uron = uron;
    }
}