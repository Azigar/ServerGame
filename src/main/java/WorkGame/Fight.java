package WorkGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by Azigar on 25.05.2015.
 */
public class Fight {

    //����� � ��������
    static String txtG = " � ������";
    static String txtT = " � ����";
    static String txtN = " � ����";

    static String txtUdar = null;

    //����� ��� � ������� �������� �� ����������
    public static int Fight(boolean liveAtk, String nameAtk, int minUronAtk, int maxUronAtk, int dexAtk, int instAtk,
                            boolean liveDef, String nameDef, int dexDef, int instDef, int defDef, int hpDef) throws IOException {
        int uron=0;
        txtHod(nameAtk); //����� ������ � ��� ��� ���
        //���������� ��� �����
        boolean shansBlokAtk = ShansBlok(liveAtk, liveDef, nameAtk, nameDef);
        //������� �� ��������� �����
        boolean shansDexAtk = Shans(dexAtk, dexDef);
        //������� �� �� ����������� ����
        boolean shansKritAtk = Shans(instAtk, instDef);
        //���� ����� � ����������
        if (shansDexAtk == true) {
            //� �� ����� � ����
            if (shansBlokAtk == false) {
                //� ����� ���� ����
                if (shansKritAtk == true) {
                    //������ ���� ����������� �����
                    uron = SrtKritUron(minUronAtk, maxUronAtk, defDef, nameAtk);
                } else { //����� ������� ����
                    //������ ���� �����
                    uron = SrtUron(minUronAtk, maxUronAtk, defDef, nameAtk);
                }
                //����� � ����
            } else {
                //� ����� ���� ����  (������ ����)
                if (shansKritAtk == true) {
                    //������ ���� �����
                    uron = SrtUronBlok(minUronAtk, maxUronAtk, defDef, nameAtk);
                } else {
                    int txt = randTxt();
                    switch (txt) {
                        case 1:
                            System.out.println(nameDef + " ������ ���������� ����");
                            break;
                        case 2:
                            System.out.println(nameDef + " ����� ��������� ����");
                            break;
                    }
                }
            }
            //��������
        } else {
            int txt = randTxt();
            switch (txt) {
                case 1:
                    System.out.println(nameAtk + " ��������");
                    break;
                case 2:
                    System.out.println(nameDef + " ��������� �� �����");
                    break;
            }
        }
        //�������� �-�� �� ����������
        hpDef = hpDef - uron;
        return hpDef;
    }

    //������ �����
    public static boolean Shans(int statAtk, int statDef) {
        //������������� ����-���
        boolean i = false;
        //������ ����� 20+(����+���)-(����+���)
        double shans = (50 + (statAtk - statDef)) / 100;
        //������ ���� ��������
        if (shans >= 0.87) shans = 0.87;
        if (shans <= 0.13) shans = 0.13;
        String str = Double.toString(shans); //������� double � String
        //��������� �������� ����� �� 0,1 �� 1
        double random = Math.random();
        //���� ����� �����������������, ����� ����-������
        if (random <= shans) i = true;
        //��������� ��������
        return i;
    }

    //������ ���� �����
    private static int Str(int strmin, int strmax) {
        Random random = new Random();
        return random.nextInt(strmin + 1) + (strmax - strmin);
    }

    //������ ������
    private static int randTxt() {
        Random random = new Random();
        return random.nextInt(2) + 1;
    }

    //������ ����������� �����/������
    private static int randUdar() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    //��������� ������ ������ ����
    private static void txtHod(String name) {
        System.out.println();
        System.out.println("��� " + name + "�");
    }

    //���� �����
    private static boolean ShansBlok(boolean liveAtk, boolean liveDef, String nameAtk, String nameDef) throws IOException {
        boolean shansBlok = false; // ���� - ���
        int udarAtk, udarDef;
        //����� ����������� ����� ���������� ������
        udarAtk = Udar(liveAtk, nameAtk);
        //����� ����������� ������ ����������� ������
        udarAtk = Udar(liveDef, nameDef);
        //��������� ��� �����
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
        //���� ����� ���������� ���������, �� ��� - ������
        if (udarAtk==udarAtk) shansBlok = true;
        return shansBlok;
    }

    //����� ����������� �����/������
    private static int Udar(boolean live, String name) throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        int udar;
        if (live == true) { //���� ����� �� ���
            do {
                System.out.println(name + ", �������� �����������: 1 -" + txtG + ", 2 -" + txtT + ", 3 -" + txtN);
                udar = Integer.parseInt(read.readLine());
                if ((udar == 1) || (udar == 2) || (udar == 3)) break;
            } while (true);
        } else udar = randUdar();
        return udar;
    }

    //������ ���� ����������� �����
    private static int SrtKritUron(int srtMin, int srtMax, int def, String name) {
        int srtKritUron = (Str(srtMin, srtMax) * 2) - (def / 10);
        System.out.println(name + " ����� ����������� ����" + txtUdar + " (���� " + srtKritUron + "). ");
        return srtKritUron;
    }

    //������ ���� �������� �����
    private static int SrtUron(int srtMin, int srtMax, int def, String name) {
        int srtUron = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUron <= ave)
            System.out.println(name + " ����� �������������� ����" + txtUdar + " (���� " + srtUron + "). ");
        else
            System.out.println(name + " ����� ��������� ����" + txtUdar + " (���� " + srtUron + "). ");
        return srtUron;
    }

    //������ ���� ����� ����� ����
    private static int SrtUronBlok(int srtMin, int srtMax, int def, String name) {
        int srtUronBlok = Str(srtMin, srtMax) - (def / 10);
        int ave = (srtMin + srtMax) / 2;
        if (srtUronBlok <= ave)
            System.out.println(name + " ������ ���� � ����� �������������� ����" + txtUdar + " (���� " + srtUronBlok + "). ");
        else
            System.out.println(name + " ������ ���� ����������, ����� ��������� ����" + txtUdar + " (���� " + srtUronBlok + "). ");
        return srtUronBlok;
    }

    //��������� � ���, ��� ������ �����
    static void TxtFist(String nameAtk, String nameDef) {
        int txt = randTxt();
        switch (txt) {
            case 1:
                System.out.println(nameAtk + " ������ �������� �� " + nameDef + "�");
                break;
            case 2:
                System.out.println(nameAtk + " ������ ������ " + nameDef + "�");
                break;
        }
    }

    static boolean Death(int hp, String nameAtk, String nameDef){
        boolean death = false;
        //���� ��������� �����
        if (hp <= 0) {
            System.out.println();
            int txt = randTxt();
            switch (txt) {
                case 1:
                    System.out.println(nameAtk + " ���� " + nameDef);
                    break;
                case 2:
                    System.out.println(nameDef + " �����");
                    break;
            }
            death = true;
        }
        return death;
    }

    static void TxtHP(String nameAtk, int hpAtk, String nameDef, int hpDef){
        System.out.println(nameAtk + " - " +hpAtk+"  HP"); //����� ���������� �-�� ��
        System.out.println(nameDef + " - " +hpDef+"  HP"); //����� ���������� �-�� ��;
    }
}


