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

    //метод боя и возврат значение ОЗ противника
    public int Fight(boolean liveAtk, String nameAtk, int minUronAtk, int maxUronAtk, double dexAtk, double instAtk,
                            boolean liveDef, String nameDef, double dexDef, double instDef, int defDef, int hpDef, int nap){
        int uron=0;
        if (Shans(dexAtk, dexDef, koef_dex) == false) {  //если попал в противника
            if (ShansBlok(liveAtk, liveDef, nameAtk, nameDef, nap) == true) {  //и не попал в блок
                if (Shans(instAtk, instDef, koef_inst) == true) {  //и нанес крит урон
                    uron = SrtKritUron(minUronAtk, maxUronAtk, defDef, nameAtk);  //расчет силы КРИТИЧЕСОГО Урона
                } else { //нанес обычный удар
                    uron = SrtUron(minUronAtk, maxUronAtk, defDef, nameAtk);  //расчет силы урона
                }
            } else { //попал в блок
                if (Shans(instAtk, instDef, koef_inst) == true) {  //но нанес крит урон - пробил блок
                    uron = SrtUronBlok(minUronAtk, maxUronAtk, defDef, nameAtk);  //расчет силы урона
                } else {  //обычным ударом
                    int txt = randTxt();
                    switch (txt) {
                        case 1:
                            setTxtComent(nameDef + " удачно блокировал удар");
                            break;
                        case 2:
                            setTxtComent(nameDef + " умело парировал удар");
                            break;
                    }
                }
            }
        } else {  //промазал
            int txt = randTxt();
            switch (txt) {
                case 1:
                    setTxtComent(nameAtk + " промазал");
                    break;
                case 2:
                    setTxtComent(nameDef + " уклонился от удара");
                    break;
            }
        }
        hpDef = hpDef - uron;  //уменшаем к-во ОЗ противника
        return hpDef;
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
        setTxtComent(name + " нанес КРИТИЧЕСКИЙ удар" + txtUdar + " (урон " + srtKritUron + ")");
        return srtKritUron;
    }

    //расчет силы Обычного Урона
    private int SrtUron(int srtMin, int srtMax, int def, String name) {
        int srtUron = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUron <= ave)
            setTxtComent(name + " нанес незначительный удар" + txtUdar + " (урон " + srtUron + ")");
        else
            setTxtComent(name + " нанес серьезный удар" + txtUdar + " (урон " + srtUron + ")");
        return srtUron;
    }

    //расчет силы Урона через блок
    private int SrtUronBlok(int srtMin, int srtMax, int def, String name) {
        int srtUronBlok = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUronBlok <= ave)
            setTxtComent(name + " пробил блок и нанес незначительный удар" + txtUdar + " (урон " + srtUronBlok + ")");
        else
            setTxtComent(name + " пробив блок противника, нанес серьезный удар" + txtUdar + " (урон " + srtUronBlok + ")");
        return srtUronBlok;
    }

    private boolean Death(int hp, String nameAtk, String nameDef){
        boolean death = false;
        //если защитника убили
        if (hp <= 0) {
            int txt = randTxt();
            switch (txt) {
                case 1:
                    setTxtComent(nameAtk + " убил " + nameDef);
                    break;
                case 2:
                    setTxtComent(nameDef + " погиб");
                    break;
            }
            death = true;
        }
        return death;
    }

    //рандом текста
    public static int randTxt() {
        Random random = new Random();
        return random.nextInt(2) + 1;
    }

    //текстовка о том, кто первый напал
    private void TxtFist(String nameAtk, String nameDef) {
        int txt = randTxt();
        switch (txt) {
            case 1:
                setTxtComent(nameAtk + " яросно бросился на " + nameDef);
                break;
            case 2:
                setTxtComent(nameAtk + " первым настиг " + nameDef);
                break;
        }
    }

    public String getTxtComent() {
        return txtComent;
    }

    public void setTxtComent(String txtComent) {
        this.txtComent = txtComent;
    }
}