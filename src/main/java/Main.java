import DataBase.*;
import WorkGame.Fight;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


class ServeOneThread extends Thread {

    //для работы сокета
    long n;
    String HostName;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String cmd; //переменная для распознавания команд от клиента
    String msg;

    //для работы с БД
    ConnectBD connectBD;
    PreparedStatement preparedStatement;
    Statement statement;
    ResultSet res;
    String query, opponent;

    //для работы с таблицами БД
    Bots bots;
    Players players;
    Bounty bounty;

    //массивы
    String [] nameDrop, name, pvp_name, avatar;
    int [] lvl, vip_lvl, str, dex, inst, def, hp, mana, pvp_exp, maxMyUron, maxEnemyUron, pve_v, pve_l, pvp_v, pvp_l, id_bots, sex;

    Fight fight; //для обработки боя
    final int PVP_EXP = 24;
    int setCount;

    //вытягиваю сегоднящнюю дату
    Calendar c = Calendar.getInstance();
    final int todayYear = c.get(Calendar.YEAR);
    final int todayMonth = c.get(Calendar.MONTH);
    final int todayDay = c.get(Calendar.DAY_OF_MONTH);

    //отдельный поток
    public ServeOneThread(Socket s, String Host_Name, long j, ConnectBD connBD) throws IOException, SQLException {
        this.n = j; //эта переменная получает по параметру № клиента, который подключился
        this.HostName = Host_Name;  //эта переменная получает по параметру имя клиента, который подключился
        this.socket = s; //эта переменная получает по параметру управление подключением через новый поток
        this.connectBD = connBD;  //эта переменная получает по параметру управление подключением к БД
        this.statement = (Statement) connectBD.getConnection().createStatement();  //подключаю Statement
        this.players = new Players();  //подключаю екземпляр класса Игроки
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); //в эту переменную будут поступать данные
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true); //эта переменная на вывод данных клиенту
        start();
    }

    public void run() {
        try {
            //ожидаю команду от клиента
            cmd = in.readLine();  //запись в переменную полученые данные
            /////////////Регистация/Авторизация Игрока
            if (cmd.equalsIgnoreCase("ENTER")) {
                while (true) {
                    //клиент покидает игру
                    if (cmd.equalsIgnoreCase("END") ||  cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {
                        out.println("END");
                        break; //выход с цикла и потока
                    }
                    out.println("ID_PLAEYR"); //запрашиваю ID игрока
                    cmd = in.readLine();  //запись в переменную полученые данные
                    if (cmd.equalsIgnoreCase("END") ||  cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {
                        break; //выход с цикла и потока
                    } else {
                        players.setId(cmd); //получаю ID игрока
                    }
                    //отправляю ответ на авторизацию или регистрацию
                    if (IsPlayer() == true) {
                        out.println("YES_HERO");
                        while (true){
                            if (cmd.equalsIgnoreCase("END")) {
                                break; //выход с цикла
                            }

                            cmd = in.readLine();  //слушаю команды

                            System.out.println("Получил "+cmd);

                            if (cmd.equalsIgnoreCase("END")) {
                                break; //выход с цикла
                            }

                            //клиент хочет начать игру сначала
                            if (cmd.equalsIgnoreCase("NEW_GAME")) {  //клиент покидает игру
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                while (true){
                                    out.println("YES_PASS");
                                    cmd = in.readLine();  //слушаю новую команду
                                    if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) { ////////////клиент выходит с игры
                                        break; // сервер закрывает сокет и поток
                                    } else {
                                        if (players.getPass().equals(cmd)){
                                            String del = DeleteHero(); //удаляю героя
                                            out.println(del);  //передаю вопросы клиенту
                                            break;
                                        } //получаю ОЗ героя
                                    }
                                }
                            }

                            //клиент хочет получить начальные хар-ки героя
                            if (cmd.equalsIgnoreCase("CHANGE_AVATAR")) {  //клиент покидает игру
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                out.println("NEW_AVATAR");  //передаю вопросы клиенту
                                cmd = in.readLine();  //запись в переменную нового аватара
                                if (ChangeAvatar(cmd) == true) out.println("NEW_AVATAR_OK");;  //меняю аватар

                            }

                            //клиент хочет получить начальные хар-ки героя
                            if (cmd.equalsIgnoreCase("GET_FULL")) {  //клиент покидает игру
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                out.println("OK");  //передаю вопросы клиенту
                                while (true) { //слушаю команды
                                    cmd = in.readLine();  //запись в переменную полученые данные
                                    //клиент покидает игру
                                    if (cmd.equalsIgnoreCase("END")) {
                                        break; //выход с цикла
                                    }
                                    if (cmd.equalsIgnoreCase("RATING")) { //клиент запрашивает рейтинг героя
                                        out.println(Rating(players.getName()));
                                    }
                                    if (cmd.equalsIgnoreCase("STR")) {  //клиент запрашивает силу героя
                                        out.println(players.getStr());
                                    }
                                    if (cmd.equalsIgnoreCase("DEX")) {  //клиент запрашивает силу героя
                                        out.println(players.getDex());
                                    }
                                    if (cmd.equalsIgnoreCase("INST")) {  //клиент запрашивает интуицию героя
                                        out.println(players.getInst());
                                    }
                                    if (cmd.equalsIgnoreCase("DEF")) {  //клиент запрашивает защиту героя
                                        out.println(players.getDef());
                                    }
                                    if (cmd.equalsIgnoreCase("MY_URON")) {  //клиент запрашивает макю нанесенный урон героя
                                        out.println(players.getMaxMyUron());
                                    }
                                    if (cmd.equalsIgnoreCase("ENEMY_URON")) {  //клиент запрашивает мак. полученый урон от врагов
                                        out.println(players.getMaxEnemyUron());
                                    }
                                    if (cmd.equalsIgnoreCase("PVP_V")) {  //клиент запрашивает к-во побед в ПвП
                                        out.println(players.getPvp_v());
                                    }
                                    if (cmd.equalsIgnoreCase("PVP_L")) {  //клиент запрашивает к-во поражений в ПвП
                                        out.println(players.getPvp_l());
                                    }
                                    if (cmd.equalsIgnoreCase("PVE_V")) {  //клиент запрашивает к-во побед в ПвЕ
                                        out.println(players.getPve_v());
                                    }
                                    if (cmd.equalsIgnoreCase("PVE_L")) {  //клиент запрашивает к-во поражений в ПвЕ
                                        out.println(players.getPve_l());
                                    }
                                }
                            }

                            //клиент хочет получить начальные хар-ки героя
                            if (cmd.equalsIgnoreCase("GET")) {  //клиент покидает игру
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                out.println("OK");  //передаю вопросы клиенту

                                while (true){ //слушаю команды
                                    cmd = in.readLine();  //запись в переменную полученые данные
                                    //клиент покидает игру
                                    if (cmd.equalsIgnoreCase("END")) {
                                        break; //выход с цикла
                                    }
                                    //клиент запрашивает Имя героя
                                    if (cmd.equalsIgnoreCase("NAME")) {
                                        //отправляю данные героя для активити-локиции
                                        out.println(players.getName()); //отсылаю имя героя
                                    }
                                    if (cmd.equalsIgnoreCase("LVL")) {
                                        out.println(players.getLvl());  //отсылаю уровень героя
                                    }
                                    if (cmd.equalsIgnoreCase("TITLE")) {
                                        out.println(players.getPvp_name());  //отсылаю звание героя
                                    }
                                    if (cmd.equalsIgnoreCase("MAX_EXP")) {
                                        out.println(players.getMax_exp());  //отсылаю мак. к-во опыта героя
                                    }
                                    if (cmd.equalsIgnoreCase("EXP")) { //отсылаю текущее к-во опыта героя
                                        out.println(players.getExp());
                                    }
                                    if (cmd.equalsIgnoreCase("MAX_PVP_EXP")) { //отсылаю мак. к-во пвп-опыта героя
                                        out.println(players.getMax_pvp_exp());
                                    }
                                    if (cmd.equalsIgnoreCase("PVP_EXP")) {  //отсылаю текущее к-во пвп-опыта героя
                                        out.println(players.getPvp_exp());
                                    }
                                    if (cmd.equalsIgnoreCase("MAX_HP")) { ////отсылаю мак. к-во ОЗ героя
                                        out.println(players.getMax_hp());
                                    }
                                    if (cmd.equalsIgnoreCase("HP")) { //отсылаю текущее к-во ОЗ героя
                                        out.println(players.getHp());
                                    }
                                    if (cmd.equalsIgnoreCase("MAX_MANA")) { //отсылаю мак. к-во маны героя
                                        out.println(players.getMax_mana());
                                    }
                                    if (cmd.equalsIgnoreCase("MANA")) { //отсылаю к-во маны героя
                                        out.println(players.getMana());
                                    }
                                    if (cmd.equalsIgnoreCase("MONEY")) { //отсылаю к-во монет у  героя
                                        out.println(players.getMoney());
                                    }
                                    if (cmd.equalsIgnoreCase("GOLD")) { //отсылаю к-во голда у героя
                                        float fGold = (float) players.getGold();
                                        String sGold = String.valueOf(fGold); //пишу значение голда в текст
                                        if (sGold.substring(sGold.indexOf(".") + 1).equalsIgnoreCase("0")) { //если символ после запятой - "0"
                                            sGold = sGold.substring(sGold.indexOf(""), sGold.indexOf(".")); //тогда пишу только символы до запятой
                                        }
                                        out.println(sGold);
                                    }
                                    if (cmd.equalsIgnoreCase("VIP")) { //отсылаю ВИП-уровень игрока
                                        out.println(players.getVip_lvl());
                                    }
                                    if (cmd.equalsIgnoreCase("SEX")) { //отсылаю пол героя
                                        out.println(players.getSex());
                                    }
                                    if (cmd.equalsIgnoreCase("AVATAR")) { //отсылаю аватар героя
                                        out.println(players.getAvatar());
                                    }
                                    if (cmd.equalsIgnoreCase("IS_PASS")) { //отсылаю аватар героя
                                        if (players.getIsPass() == 0) out.println("NO_PASS");
                                        else {
                                            while (true){
                                                out.println("YES_PASS");
                                                cmd = in.readLine();  //слушаю новую команду
                                                System.out.println("Пароль в БД - " + players.getPass());
                                                System.out.println("Пароль на проверку - " + cmd);
                                                if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) { ////////////клиент выходит с игры
                                                    break; // сервер закрывает сокет и поток
                                                } else {
                                                    if (players.getPass().equals(cmd)){
                                                        out.println("NO_PASS");
                                                        break;
                                                    } //получаю ОЗ героя
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //клиент хочет получить подарок за ежедневный вход
                            if (cmd.equalsIgnoreCase("BOUNTY")) {
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                String bounty = Bounty();
                                out.println(bounty); //отсылаю результат
                                if (bounty.equalsIgnoreCase("YES_BOUNTY")) {
                                    cmd = in.readLine();  //слушаю новую команду
                                    if (cmd.equalsIgnoreCase("WHAT_BOUNTY")) {
                                        out.println(msg);  //отправляю комент
                                    }
                                }
                            }

                            //клиент хочет получить подарок на праздник
                            if (cmd.equalsIgnoreCase("HOLIDAY")) { //отсылаю к-во голда у героя
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                String holiday = Holiday(); //определяю, буду ли я сегодня дарить что-то
                                out.println(holiday); //отсылаю результат
                                if (holiday.equalsIgnoreCase("YES_HOLIDAY")) {
                                    cmd = in.readLine();  //слушаю новую команду
                                    if (cmd.equalsIgnoreCase("WHAT_HOLIDAY")) {
                                        out.println(msg);  //отправляю комент
                                    }
                                }
                            }

                            //клиент хочет получить подарок на день рождения
                            if (cmd.equalsIgnoreCase("BIRTHDAY")) { //отсылаю к-во голда у героя
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                String birthday = Birthday(); //определяю, буду ли я сегодня дарить что-то
                                out.println(birthday); //отсылаю результат
                                if (birthday.equalsIgnoreCase("YES_BIRTHDAY")) {
                                    cmd = in.readLine();  //слушаю новую команду
                                    if (cmd.equalsIgnoreCase("WHAT_BIRTHDAY")) {
                                        out.println(msg);  //отправляю комент
                                    }
                                }
                            }

                            /////////////Клиент покидает игру
                            if (cmd.equalsIgnoreCase("SAVE_GAME")) {
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                //запрашиваю HP героя
                                out.println("HP");
                                cmd = in.readLine();
                                if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) { ////////////клиент выходит с игры
                                    break; // сервер закрывает сокет и поток
                                } else {
                                    players.setHp(Integer.parseInt(cmd)); //получаю ОЗ героя
                                }
                                //запрашиваю ману героя
                                out.println("MANA");
                                cmd = in.readLine();
                                if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) { ////////////клиент выходит с игры
                                    break; // сервер закрывает сокет и поток
                                } else {
                                    players.setMana(Integer.parseInt(cmd)); //получаю ману героя
                                }
                                ///////////проверяю к-во ОЗ и манны игрока
                                if (players.getHp() <= players.getMax_hp()) {
                                    UpdateHpHero(); //пишу актуальное значение в БД
                                }
                                if (players.getMana() <= players.getMax_mana()) {
                                    UpdateManaHero(); //пишу актуальное значение в БД
                                }
                                out.println("SAVE");
                            }

                            //Боги хотят исцелить героя
                            if (cmd.equalsIgnoreCase("REGEN")) {
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                String m_money = "", m_G = "";
                                msg = "NO_REGEN";
                                int hp_money = 0, mana_money = 0, s_money;
                                double ch;
                                double hp_G = 0;
                                double mana_G = 0;
                                double s_G = 0;
                                //проверяю ОЗ героя
                                if (players.getHp() < players.getMax_hp()) { //если хп меньше чем нужно
                                    msg = "REGEN";
                                    ch = ((double) players.getHp() / (double) players.getMax_hp()) * 100;
                                    //нада выставлять от старших уровней до меньших
                                    if (ch < 99 && players.getLvl() < 10) {
                                        hp_money = 50 * players.getLvl();
                                        hp_G = 0;
                                    }
                                    if (ch < 66 && players.getLvl() < 10) {
                                        hp_G = (double) (players.getLvl()) / 8;
                                        hp_money = 0;
                                    }
                                    if (ch < 33 && players.getLvl() < 10) {
                                        hp_G = (double) (players.getLvl()) / 4;
                                        hp_money = 0;
                                    }
                                    if (ch < 99 && players.getLvl() < 5) {
                                        hp_money = 12 * players.getLvl();
                                        hp_G = 0;
                                    }
                                    if (ch < 66 && players.getLvl() < 5) {
                                        hp_money = 25 * players.getLvl();
                                        hp_G = 0;
                                    }
                                    if (ch < 33 && players.getLvl() < 5) {
                                        hp_money = 37 * players.getLvl();
                                        hp_G = 0;
                                    }
                                }
                                //проверяю ману героя
                                if (players.getMana() < players.getMax_mana()) { //если хп меньше чем нужно
                                    msg = "REGEN";
                                    ch = ((double) players.getHp() / (double) players.getMax_hp()) * 100;
                                    //нада выставлять от старших уровней до меньших
                                    if (ch < 99 && players.getLvl() < 10) {
                                        mana_money = 50 * players.getLvl();
                                        mana_G = 0;
                                    }
                                    if (ch < 66 && players.getLvl() < 10) {
                                        mana_G = (double) (players.getLvl()) / 8;
                                        mana_money = 0;
                                    }
                                    if (ch < 33 && players.getLvl() < 10) {
                                        mana_G = (double) (players.getLvl()) / 4;
                                        mana_money = 0;
                                    }
                                    if (ch < 99 && players.getLvl() < 5) {
                                        mana_money = 12 * players.getLvl();
                                        mana_G = 0;
                                    }
                                    if (ch < 66 && players.getLvl() < 5) {
                                        mana_money = 25 * players.getLvl();
                                        mana_G = 0;
                                    }
                                    if (ch < 33 && players.getLvl() < 5) {
                                        mana_money = 37 * players.getLvl();
                                        mana_G = 0;
                                    }
                                }
                                if (msg.equalsIgnoreCase("REGEN")) {
                                    s_money = hp_money + mana_money;
                                    s_G = hp_G + mana_G;
                                    if (s_money != 0) m_money = s_money + " монет";
                                    if (s_G != 0) {
                                        m_G = String.valueOf(s_G); //пишу цену голдов в текст
                                        //если символ после запятой - "0"
                                        if (m_G.substring(m_G.indexOf(".") + 1).equalsIgnoreCase("0")) {
                                            m_G = m_G.substring(m_G.indexOf(""), m_G.indexOf(".")); //тогда пишу только символы до запятой
                                        }
                                        m_G = m_G + " G";
                                    }
                                    ///если хватит денег на реген
                                    if (players.getMoney() >= s_money && players.getGold() >= s_G) {
                                        out.println(msg);
                                        cmd = in.readLine();  //запись в переменную полученые данные
                                        if (cmd.equalsIgnoreCase("PRICE")) {
                                            out.println(m_money + m_G);
                                        }
                                        cmd = in.readLine();  //запись в переменную полученые данные
                                        if (cmd.equalsIgnoreCase("YES")) {
                                            Pay((hp_money + mana_money), (hp_G + mana_G)); //снять со счета игрока деньги за исцеление
                                            out.println("GOOD"); //просто нужно что-то отправить, что бы не сбится с очередности приём-передача
                                        } else {
                                            out.println("GOOD"); //просто нужно что-то отправить, что бы не сбится с очередности приём-передача
                                        }
                                    } else {
                                        msg = "NO_MONEY";
                                        out.println(msg);
                                    }
                                } else {
                                    out.println(msg);
                                }
                            }

                            /////////////клиент открыл ПОИСК СОПЕРНИКА ДЛЯ ПвП
                            if (cmd.equalsIgnoreCase("OPPONENT")) {
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                out.println("OK"); //передаю вопросы клиенту
                                while (true){
                                    //клиент покидает это активити
                                    if (cmd.equalsIgnoreCase("END")) {
                                        break; //выход с цикла
                                    }
                                    //клиент покидает это активити
                                    if (cmd.equalsIgnoreCase("SEARCH")) {
                                        out.println("OK_OK"); //передаю вопросы клиенту
                                    }
                                    //клиент покидает это активити
                                    if (cmd.equalsIgnoreCase("SELECT")) {
                                        out.println("OK"); //передаю вопросы клиенту
                                    }
                                    cmd = in.readLine();  //слушаю команду
                                    //клиент хочет что бы север нашел всех подходящих оппонентов
                                    if (cmd.equalsIgnoreCase("SELECT")) {
                                        SelectOpponent(); //ищу подходящих оппонентов
                                        out.println("FOUND"); //сервер нашел всех оппонентов
                                    }
                                    //клиент хочет что бы север нашел оппонента по имени
                                    if (cmd.equalsIgnoreCase("SEARCH")) {
                                        System.out.println("попал сюда 4");
                                        out.println("NAME"); //сервер нашел оппонента по имени
                                        cmd = in.readLine();  //слушаю команду
                                        System.out.println("Пришло имя - " + cmd);
                                        if(SearchOpponent(cmd) == true) { //если есть такой герой
                                            String a = "FOUND";
                                            out.println(a); //сервер нашел оппонента по имени
                                            System.out.println("Отправил - " + a);
                                        }else{
                                            String a = "NO_FOUND";
                                            System.out.println("Отправил - " + a);
                                            out.println(a); //сервер не нашел оппонента по имени
                                        }
                                    }
                                    //клиент хочет получить данные о оппонете
                                    if (cmd.equalsIgnoreCase("GET")) {
                                        out.println("YES"); //сервер готов к отправке данных
                                        System.out.println("к-во героев а массиве - " + setCount);
                                        for (int i = 0; i < setCount; i++) { //прохожу цикл по к-ву найденых подходящий оппонентов
                                            //клиент покидает это активити
                                            if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("SEARCH")){
                                                System.out.println("попал сюда 2");
                                                break; //выход с цикла
                                            }
                                            if (cmd.equalsIgnoreCase("NEXT")){
                                                out.println("YES_NEXT"); //сервер готов к отправке данных
                                            }
                                            while (true) {
                                                cmd = in.readLine();  //запись в переменную полученые данные

                                                System.out.println("Получи 30 - " + cmd);

                                                //клиент покидает это активити
                                                if (cmd.equalsIgnoreCase("END") ||
                                                        cmd.equalsIgnoreCase("SEARCH") ||
                                                            cmd.equalsIgnoreCase("NEXT")) {
                                                    System.out.println("попал сюда 1");
                                                    break; //выход с цикла
                                                }
                                                //клиент запрашивает Имя героя
                                                if (cmd.equalsIgnoreCase("NAME")) {
                                                    System.out.println("имя, которое оправляю - " + name[i]);
                                                    out.println(name[i]); //отсылаю имя героя
                                                }
                                                if (cmd.equalsIgnoreCase("LVL")) {
                                                    out.println(lvl[i]);  //отсылаю уровень героя
                                                }
                                                if (cmd.equalsIgnoreCase("TITLE")) {
                                                     out.println(pvp_name[i]);  //отсылаю звание героя
                                                }
                                                if (cmd.equalsIgnoreCase("PVP_EXP")) {  //отсылаю текущее к-во пвп-опыта героя
                                                         out.println(pvp_exp[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("HP")) { //отсылаю текущее к-во ОЗ героя
                                                    out.println(hp[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("MANA")) { //отсылаю к-во маны героя
                                                    out.println(mana[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("VIP")) { //отсылаю ВИП-уровень игрока
                                                    out.println(vip_lvl[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("AVATAR")) { //отсылаю аватар героя
                                                    out.println(avatar[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("RATING")) { //клиент запрашивает рейтинг героя
                                                    out.println(Rating(name[i]));
                                                }
                                                if (cmd.equalsIgnoreCase("STR")) {  //клиент запрашивает силу героя
                                                    out.println(str[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("DEX")) {  //клиент запрашивает силу героя
                                                    out.println(dex[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("INST")) {  //клиент запрашивает интуицию героя
                                                    out.println(inst[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("DEF")) {  //клиент запрашивает защиту героя
                                                    out.println(def[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("MY_URON")) {  //клиент запрашивает мак. нанесенный урон героя
                                                    out.println(maxMyUron[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("ENEMY_URON")) {  //клиент запрашивает мак. полученый урон от врагов
                                                    out.println(maxEnemyUron[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("PVP_V")) {  //клиент запрашивает к-во побед в ПвП
                                                    out.println(pvp_v[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("PVP_L")) {  //клиент запрашивает к-во поражений в ПвП
                                                    out.println(pvp_l[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("PVE_V")) {  //клиент запрашивает к-во побед в ПвЕ
                                                    out.println(pve_v[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("PVE_L")) {  //клиент запрашивает к-во поражений в ПвЕ
                                                    out.println(pve_l[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("SEX")) {  //клиент запрашивает к-во поражений в ПвЕ
                                                    out.println(sex[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("ID")) {  //клиент запрашивает ID бота-клона
                                                    out.println(id_bots[i]);
                                                }
                                                if (cmd.equalsIgnoreCase("LAST")) {  //клиент запрашивает ID бота-клона
                                                    if (i == (setCount - 1)) { //если это последний герой в списке
                                                        out.println("YES_LAST");
                                                        break;
                                                    } else out.println("NO_LAST");

                                                }
                                            }
                                        }
                                    }
                                }
                                SelectOpponent(); //ищу подходящих оппонентов
                                out.println("OK"); //передаю вопросы клиенту

                                if (cmd.equalsIgnoreCase("SEARCH")) { //если ищу по имени

                                }
                            }

                            /////////////клиент открыл АКТИВИТИ БОЯ, обработка запросов для боя
                            if (cmd.equalsIgnoreCase("FIGHT_ACTIVITY")) {
                                /////////////////загружаю клиенту данные для стартового окна
                                SelectAllFromPlayers(); //выбираю все данные по герою
                                this.fight = new Fight();  //подключаю екземпляр класса БОЙ
                                this.bots = new Bots();  //подключаю екземпляр класса Боти
                                //выбираю все данные о противнике
                                out.println("ID_ENEMY"); //запрашиваю ID противника
                                cmd = in.readLine();
                                if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {
                                    break; //выход с цикла и потока
                                } else {
                                    bots.setId(Integer.parseInt(cmd)); //получаю ID противника
                                }
                                SelectAllFrom_Bots(bots.getId());  //выбираю все данные о противнике с БД
                                //передаю управление вопросами клиенту, что бы не сбится с очередности получение-отправка
                                out.println("YOUR_QUESTIONS");
                                //отправка к-во мани игрока
                                cmd = in.readLine();  //запись в переменную полученые данные
                                if (cmd.equalsIgnoreCase("WHO_FIRST")) {
                                    boolean firstAtk = fight.Shans(players.getInst(), bots.getInst(), 50);
                                    if (firstAtk) {
                                        out.println("FIRST_PLAYER");
                                    } else {
                                        out.println("FIRST_ENEMY");
                                    }
                                }
                                ArrayList<Integer> heroUron = new ArrayList<Integer>(); //динамический массив для збора урона игрока
                                ArrayList<Integer> enemyUron = new ArrayList<Integer>(); //динамический массив для збора урона противника
                                  ////////////////////////////Система боя
                                while (true) {
                                    /////////Ожидаю команду 1 (ожидаю направление удара/защиты, отмены боя, смерти опонентов и т.д.)
                                    cmd = in.readLine();  //слушаю команды боя
                                    if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {//клиент уходит с битвы
                                        break; //выход с цикла
                                    }

                                    //клиент бьет в голову
                                    if (cmd.equalsIgnoreCase("ATK_1")) {
                                        bots.setHp(fight.Fight(true, players.getName(), players.getStr(), players.getDex(), players.getInst(),
                                                false, bots.getName(), bots.getDex(), bots.getInst(), bots.getDef(), bots.getHp(), 1, players.getSex(), bots.getSex()));
                                        if (bots.getHp() <= 0) bots.setHp(0);
                                        heroUron.add(fight.getUron()); //пишу урон в массив
                                        out.println(bots.getHp());
                                    }
                                    //клиент бьет в торс
                                    if (cmd.equalsIgnoreCase("ATK_2")) {
                                        bots.setHp(fight.Fight(true, players.getName(), players.getStr(), players.getDex(), players.getInst(),
                                                false, bots.getName(), bots.getDex(), bots.getInst(), bots.getDef(), bots.getHp(), 2, players.getSex(), bots.getSex()));
                                        if (bots.getHp() <= 0) bots.setHp(0);
                                        heroUron.add(fight.getUron()); //пишу урон в массив
                                        out.println(bots.getHp());
                                    }
                                    //клиент бьет в ноги
                                    if (cmd.equalsIgnoreCase("ATK_3")) {
                                        bots.setHp(fight.Fight(true, players.getName(), players.getStr(), players.getDex(), players.getInst(),
                                                false, bots.getName(), bots.getDex(), bots.getInst(), bots.getDef(), bots.getHp(), 3, players.getSex(), bots.getSex()));
                                        if (bots.getHp() <= 0) bots.setHp(0);
                                        heroUron.add(fight.getUron()); //пишу урон в массив
                                        out.println(bots.getHp());
                                    }
                                    //клиент защищает голову
                                    if (cmd.equalsIgnoreCase("DEF_1")) {
                                        //метод расчета ОЗ игрока. Параметры: хар-ки бота, хар-ки игрока, направление удара 1 - голова
                                        players.setHp(fight.Fight(false, bots.getName(), bots.getStr(), bots.getDex(), bots.getInst(),
                                                true, players.getName(), players.getDex(), players.getInst(), players.getDef(), players.getHp(), 1, bots.getSex(), players.getSex()));
                                        if (players.getHp() <= 0) players.setHp(0);
                                        enemyUron.add(fight.getUron()); //пишу урон в массив
                                        out.println(players.getHp());
                                    }
                                    //клиент защищает торс
                                    if (cmd.equalsIgnoreCase("DEF_2")) {
                                        //метод расчета ОЗ игрока. Параметры: хар-ки бота, хар-ки игрока, направление удара 1 - голова
                                        players.setHp(fight.Fight(false, bots.getName(), bots.getStr(), bots.getDex(), bots.getInst(),
                                                true, players.getName(), players.getDex(), players.getInst(), players.getDef(), players.getHp(), 2, bots.getSex(), players.getSex()));
                                        if (players.getHp() <= 0) players.setHp(0);
                                        enemyUron.add(fight.getUron()); //пишу урон в массив
                                        out.println(players.getHp());
                                    }
                                    //клиент защищает ноги
                                    if (cmd.equalsIgnoreCase("DEF_3")) {
                                        //метод расчета ОЗ игрока. Параметры: хар-ки бота, хар-ки игрока, направление удара 1 - голова
                                        players.setHp(fight.Fight(false, bots.getName(), bots.getStr(), bots.getDex(), bots.getInst(),
                                                true, players.getName(), players.getDex(), players.getInst(), players.getDef(), players.getHp(), 3, bots.getSex(), players.getSex()));
                                        if (players.getHp() <= 0) players.setHp(0);
                                        enemyUron.add(fight.getUron()); //пишу урон в массив
                                        out.println(players.getHp());
                                    }
                                    ///////////////////////////Завершение боя
                                    // клиент погиб в бою
                                    if (cmd.equalsIgnoreCase("LOSE") && players.getHp() <= 0) {
                                        String txtLoseMoney = "";
                                        int loseMoney = LoseMoney(); ////определяю к-во потеряных денег
                                        if (loseMoney > 0) {
                                            txtLoseMoney = bots.getName() + " обыскал Ваш труп и забрал " + loseMoney + " монет.";
                                        }
                                        String txtLoseCourage = "";
                                        int loseCourage = LoseCourage(); //определяю к-во потеряных очков Храбрости
                                        if (loseCourage > 0) {
                                            txtLoseCourage = " Храбрость снизилась на " + loseCourage + ".";
                                        }
                                        if (txtLoseMoney == "") {
                                            out.println("У Вас уже нечего забирать");
                                        } else {
                                            out.println(txtLoseMoney + txtLoseCourage);  //отправляю клиенту комент о поражении
                                        }
                                        UpdateHpHero();  //обновляю в БД ОЗ игрока
                                        Lose(); //заношу новое к-во поражений
                                        MaxUron(heroUron, enemyUron); //узнаю максимальный урон и по необходимости обновляю БД
                                        break; //выход с цикла
                                    }

                                    //клиент победил в бою
                                    if (cmd.equalsIgnoreCase("VICTORY") && bots.getHp() <= 0) {
                                        //определяю очки опыта
                                        String txtExp = "";
                                        int exp = Exp();
                                        if (exp > 0) {
                                            txtExp = "Вы заработали " + exp + " опыта";
                                        }
                                        //определяю полученые деньги
                                        String txtMoney = "";
                                        int money = Money();
                                        if (money > 0) {
                                            txtMoney = money + " монет";
                                        }
                                        //определяю дроп
                                        String txtDrop = "";
                                        if (Drop() == true) {
                                            for (int j = 0; j < nameDrop.length; j++) {  //собираю название дропа с масива в одну строку
                                                if (nameDrop[j] != null) { //если ячейка массива не null
                                                    txtDrop = txtDrop + nameDrop[j];
                                                }
                                            }
                                        }
                                        //определяю очки Храбрости
                                        String txtCourage = "";
                                        int courage = Courage();
                                        if (courage > 0) {
                                            txtCourage = " и " + courage + " Храбрости";
                                        }
                                        if (txtExp == "" && txtMoney == "") {
                                            out.println("Вы обыскали труп врага, но ничего не нашли.");  //отправляю клиенту комент о дропе
                                        } else {
                                            out.println(txtExp + txtCourage + ". Обыскав труп врага, получили " + txtMoney + txtDrop + ".");  //отправляю клиенту комент о победе
                                        }
                                        //Проверяю апп
                                        cmd = in.readLine();  //запись в переменную полученые данные
                                        if (cmd.equalsIgnoreCase("UP_LVL")) {
                                            if (UpLvl(exp) == true) { //если лвл-апп
                                                out.println("Вы достигли " + (players.getLvl() + 1) + "-го уровня, все хар-ки героя повышены. " +
                                                        "Вы получаете " + players.getExp_money() + " монет и 1 G.");
                                            } else {  //если игрок не апнулся
                                                out.println("NO_LVL_UP");
                                                UpdateHpHero();  //обновляю в БД hp игрока
                                            }
                                        }
                                        cmd = in.readLine();  //запись в переменную полученые данные
                                        if (cmd.equalsIgnoreCase("UP_PVP")) {
                                            String title = UpPvP(courage);  //определяю звание при апп
                                            if (title != "") {  //если получил новое звание
                                                out.println("Вы получили звание " + title + ", все хар-ки героя повышены. " +
                                                        "Вы получаете " + players.getPvp_money() + " монет и 2 G.");
                                            } else {
                                                out.println("NO_PVP_UP");
                                            }
                                        }
                                        Victory(); //заношу новое к-во побед
                                        MaxUron(heroUron, enemyUron); //узнаю максимальный урон и по необходимости обновляю БД
                                        break; //выход с цикла
                                    }
                                    /////////Ожидаю команду 2 (клиент хочет получить комент)
                                    cmd = in.readLine();  //запись в переменную полученые данные
                                    if (cmd.equalsIgnoreCase("COMMENT_ATK") || cmd.equalsIgnoreCase("COMMENT_DEF")) {
                                        out.println(fight.getTxtComent());
                                    }
                                }
                            }
                        }
                    } else {
                        out.println("NO_HERO");
                        this.bots = new Bots();  //подключаю екземпляр класса Боти
                        while (true) { //слушаю команды
                            cmd = in.readLine();  //сохраняю имя нового героя
                            if (cmd.equalsIgnoreCase("END") ||  cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {
                                break; //выход с цикла и потока
                            } else {
                                if (IsNameHero(cmd) == true) { //если новое имя свободно
                                    out.println("DATE_BIRTH"); //запрашиваю дату рождения
                                    players.setsDateBirth(in.readLine()); //сохраняю в сеттер дату рождения
                                    out.println("SEX"); //запрашиваю пол нового героя
                                    players.setSex(Integer.parseInt(in.readLine())); //сохраняю в сеттер пол нового героя
                                    out.println("AVATAR"); //запрашиваю имя файла выбраного аватара
                                    players.setAvatar(in.readLine()); //сохраняю в сеттер имя файла выбраного аватара
                                    out.println("PASS"); //запрашиваю имя файла выбраного аватара
                                    players.setPass(in.readLine()); //сохраняю в сеттер имя файла выбраного аватара
                                    if (CreateNewBot(cmd) == true) { //создаю бота-клона
                                        bots.setId(NewIdBot()); //пишу значение ID бота-клона
                                        if (CreateNewHero(cmd) == true) { //создаю нового героя
                                            out.println("REGISTERED");
                                            break; //выход с цикла и потока
                                        }
                                    }
                                } else {
                                    out.println("NAME_EXISTS");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Невозможно обработать SQL-запрос, ошибка № " + e);
        } catch (Throwable e) {
            System.err.println("Соединение № "+ n +". Ошибка при передачи клиенту: " +HostName);
        } finally {
            try {
                statement.close();
                socket.close();
                //закрываю экземпляры открытых обьектов
                if (socket != null) { socket = null; }
                if (bots != null) { bots = null; }
                if (nameDrop != null) { nameDrop = null; }
                if (bounty != null) bounty = null;
            } catch (IOException e) {
                System.err.println("Соединение № "+ n +". Не получается разорвать связь с клиентом: " +HostName);
            } catch (SQLException e) {
                System.err.println("Не получается закрыть Statement, ошибка: " + e);
            }
        }
        System.out.println("Завершено соединение № " + n + ", клиент: " +HostName + ", " + players.getName() + " отключен");
        if (players != null) { players = null; }  //закрываю экземпляры открытых классов
    }

    //удаляю героя
    private String DeleteHero() throws SQLException {
        String del = "NO_DELETE";
        //удаляю запись в таблице players
        query = "DELETE FROM players WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        //удаляю бота-клона в таблице bots
        query = "DELETE FROM bots WHERE id_bots = '" + players.getId_bots() + "';"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        //удаляю запись в таблице execution
        query = "DELETE FROM execution WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        //удаляю запись в таблице inventory
        query = "DELETE FROM inventory WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        //удаляю запись в таблице inventory
        query = "DELETE FROM inventory WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        statement.executeBatch(); //выполнение всех запросов
        statement.clearBatch(); //очищает весь пакет запросов
        del = "YES_DELETE";
        return del;
    }

    //меняю аватар героя
    private boolean ChangeAvatar(String name) throws SQLException {
        boolean isYes = false;
        query = "UPDATE players SET avatar = '" + name + "', gold = gold - 30 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //выполнение запроса
        isYes = true;
        return isYes;
    }

    //ищу оппонента по имени
    private boolean SearchOpponent(String opponent) throws SQLException {
        boolean search = false;
        //ищу есть ли такой герой в БД
        query = "SELECT COUNT(*) AS kol FROM players p WHERE p.name = '" + opponent + "';";
        res = statement.executeQuery(query); //запускаю запрос
        while (res.next()) {
            setCount = res.getInt("kol");
        }
        if (setCount == 1) { //если такое имя есть
            //выбираю данные этого героя
            query = "SELECT * FROM players p, pvp_exp pvp WHERE p.name = '" + opponent + "' AND p.pvp_lvl = pvp.pvp_lvl;";
            res = statement.executeQuery(query); //запускаю запрос
            int i = 0;
            //иницилизирую массивы
            name = new String[setCount];
            lvl = new int[setCount];
            vip_lvl = new int[setCount];
            str = new int[setCount];
            dex = new int[setCount];
            inst = new int[setCount];
            def = new int[setCount];
            hp = new int[setCount];
            mana = new int[setCount];
            pvp_exp = new int[setCount];
            pvp_name = new String[setCount];
            avatar = new String[setCount];
            maxMyUron = new int[setCount];
            maxEnemyUron = new int[setCount];
            pve_v = new int[setCount];
            pve_l = new int[setCount];
            pvp_v = new int[setCount];
            pvp_l = new int[setCount];
            id_bots = new int[setCount];
            sex = new int[setCount];
            //выбираю оппонентов по очереди и пишу данные в массивы
            while (res.next()) {
                name[i] = res.getString("name");
                lvl[i] = res.getInt("lvl");
                vip_lvl[i] = res.getInt("vip_lvl");
                str[i] = res.getInt("str");
                dex[i] = res.getInt("dex");
                inst[i] = res.getInt("inst");
                def[i] = res.getInt("def");
                hp[i] = res.getInt("max_hp");
                mana[i] = res.getInt("max_mana");
                pvp_exp[i] = res.getInt("pvp_exp");
                pvp_name[i] = res.getString("pvp_name");
                maxMyUron[i] = res.getInt("maxMyUron");
                maxEnemyUron[i] = res.getInt("maxEnemyUron");
                pve_v[i] = res.getInt("pve_v");
                pve_l[i] = res.getInt("pve_l");
                pvp_v[i] = res.getInt("pvp_v");
                pvp_l[i] = res.getInt("pvp_l");
                avatar[i] = res.getString("avatar");
                id_bots[i] = res.getInt("id_bots");
                sex[i] = res.getInt("sex");
                System.out.println("имя, которое нашел - " + name[i]);
            }
            search = true;
        }
        return search;
    }

    //ищу подходящих оппонентов
    private void SelectOpponent() throws SQLException {
        //к-во подходящих оппонентов
        query = "SELECT COUNT(*) AS kol " +
                "FROM players p, pvp_exp pvp " +
                "WHERE (p.lvl = " + (players.getLvl() + 2) + " OR p.lvl = " + (players.getLvl() + 1) + " OR p.lvl = " + players.getLvl() +
                " OR p.lvl = " + (players.getLvl() - 1) + " OR p.lvl = " + (players.getLvl() - 2) + " OR p.pvp_exp >= " + players.getPvp_exp() +
                ") AND p.name <> '" + players.getName() + "' AND p.pvp_lvl = pvp.pvp_lvl;";
            res = statement.executeQuery(query); //запускаю запрос
            while (res.next()) {
                setCount = res.getInt("kol");
            }
        //выбираю подходящиех оппонентов
        query = "SELECT * " +
                "FROM players p, pvp_exp pvp " +
                "WHERE (p.lvl = " + (players.getLvl() + 2) + " OR p.lvl = " + (players.getLvl() + 1) + " OR p.lvl = " + players.getLvl() +
                " OR p.lvl = " + (players.getLvl() - 1) + " OR p.lvl = " + (players.getLvl() - 2) + " OR p.pvp_exp >= " + players.getPvp_exp() +
                ") AND p.name <> '" + players.getName() + "' AND p.pvp_lvl = pvp.pvp_lvl ORDER BY p.lvl, p.pvp_exp, p.exp;";
            res = statement.executeQuery(query); //запускаю запрос
        int i = 0;
        //иницилизирую массивы
        name = new String[setCount];
        lvl = new int[setCount];
        vip_lvl = new int[setCount];
        str = new int[setCount];
        dex = new int[setCount];
        inst = new int[setCount];
        def = new int[setCount];
        hp = new int[setCount];
        mana = new int[setCount];
        pvp_exp = new int[setCount];
        pvp_name = new String[setCount];
        avatar = new String[setCount];
        maxMyUron = new int[setCount];
        maxEnemyUron = new int[setCount];
        pve_v = new int[setCount];
        pve_l = new int[setCount];
        pvp_v = new int[setCount];
        pvp_l = new int[setCount];
        id_bots = new int[setCount];
        sex = new int[setCount];
        //выбираю оппонентов по очереди и пишу данные в массивы
            while (res.next()) {
                name[i] = res.getString("name");
                lvl[i] = res.getInt("lvl");
                vip_lvl[i] = res.getInt("vip_lvl");
                str[i] = res.getInt("str");
                dex[i] = res.getInt("dex");
                inst[i] = res.getInt("inst");
                def[i] = res.getInt("def");
                hp[i] = res.getInt("max_hp");
                mana[i] = res.getInt("max_mana");
                pvp_exp[i] = res.getInt("pvp_exp");
                pvp_name[i] = res.getString("pvp_name");
                maxMyUron[i] = res.getInt("maxMyUron");
                maxEnemyUron[i] = res.getInt("maxEnemyUron");
                pve_v[i] = res.getInt("pve_v");
                pve_l[i] = res.getInt("pve_l");
                pvp_v[i] = res.getInt("pvp_v");
                pvp_l[i] = res.getInt("pvp_l");
                avatar[i] = res.getString("avatar");
                id_bots[i] = res.getInt("id_bots");
                sex[i] = res.getInt("sex");
                i++;
            }
    }


    //подарок на день рождения
    private String Birthday() throws SQLException {
        int p = 5;
        String m = "NO_BIRTHDAY";
        //вытягиваю месяц и день с даты рождения
        String birthday = String.valueOf(players.getdDateBirth()); //дату в текст
        String year = birthday.substring(birthday.indexOf(""), birthday.indexOf("-")); //вытягиваю год с даты (вытягиваю тест  от начала и до первого "-")
        String month = birthday.substring(5, birthday.length()); //убираю год с даты (отбрасываю первые 5 символов)
        month = month.substring(month.indexOf(""), month.indexOf("-")); //вытягиваю месяц с даты (вытягиваю тест от начала и до первого "-")
        String day = birthday.substring(8, birthday.length()); //вытягиваю день с даты (отбрасываю первые 8 символов)
        if (players.getBirthday() != 1) { //если еще нет отметки о праздновании и получении подарка
            if (Integer.parseInt(day) == todayDay && Integer.parseInt(month) == (todayMonth + 1)){ //если наступило день рождения
                int gold = 100;
                query = "UPDATE players SET gold = gold + " + gold + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди
                //пишу к-во подареных голдов
                query = "UPDATE players SET BountyGold = BountyGold + " + gold + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди
                //помечаю что игрок уже получил подарок
                query = "UPDATE players SET isBirthday = 1 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди
                //увеличиваю хар-ки героя
                query = "UPDATE players SET str = str + " + p + ", dex = dex + " + p + ", inst = inst + " + p + ", def = def + " + p + "," +
                        " max_hp = max_hp + " + (p * 10) + ", hp = max_hp, max_mana = max_mana + " + p + ", mana = max_mana " +
                        "WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
                //увеличиваю все статы бота-клона
                query = "UPDATE bots SET str = str + " + p + ", dex = dex + " + p + ", inst = inst + " + p + ", def = def + " + p + "," +
                        " hp = hp + " + (p * 10) + ", mana = mana + " + p + " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
                statement.addBatch(query); //запрос в очереди
                statement.executeBatch(); //выполнение всех запросов
                statement.clearBatch(); //очищает весь пакет запросов
                m = "YES_BIRTHDAY";
                msg = "Желает Вам здоровья, удачи, простого человеческого счастья, тепла и добра, а также исполнения желаний. " +
                        "Примите скромный подарок - " + gold + " G. Все хар-ки героя увеличины на " + p + "!";
            }
        }else{ //если есть отметка о праздновании и получении подарка
            if (Integer.parseInt(day) != todayDay && Integer.parseInt(month) != (todayMonth + 1) ||
                    Integer.parseInt(day) < todayDay && Integer.parseInt(month) == (todayMonth + 1)) { //но день не соответствует
                //снимаю отметку о праздновании
                query = "UPDATE players SET isBirthday = 0 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди
                //снимаю все бонусные хар-ки
                query = "UPDATE players SET str = str - " + p + ", dex = dex - " + p + ", inst = inst - " + p + ", def = def - " + p + "," +
                        " max_hp = max_hp - " + (p * 10) + ", hp = max_hp, max_mana = max_mana - " + p + ", mana = max_mana " +
                        "WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
                //увеличиваю все статы бота-клона
                query = "UPDATE bots SET str = str - " + p + ", dex = dex - " + p + ", inst = inst - " + p + ", def = def - " + p + "," +
                        " hp = hp - " + (p * 10) + ", mana = mana - " + p + " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
                statement.addBatch(query); //запрос в очереди
                statement.executeBatch(); //выполнение всех запросов
                statement.clearBatch(); //очищает весь пакет запросов
            }
        }
        return m;
    }

    //подарки на праздники
    private String Holiday() throws SQLException {
        String m = "NO_HOLIDAY";
        if (players.getHoliday() != 1){ //если еще нет отметки о праздновании и получении подарка
            int money = 1000;
            double gold = 10;
            //что-то дарю
            query = "UPDATE players SET gold = gold + " + gold + ", money = money + " + money + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди
            //пишу к-во подареных голдов ПО НЕОБХОДИМОСТИ
            query = "UPDATE players SET BountyGold = BountyGold + " + gold + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди
            //помечаю что игрок уже получил подарок
            query = "UPDATE players SET isHoliday = 1 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди
            statement.executeBatch(); //выполнение всех запросов
            statement.clearBatch(); //очищает весь пакет запросов
            m = "YES_HOLIDAY";
            msg = "Адмиистрация проекта решила подарить " + money + " монет и " + gold + " G.";
        }
        return m;
    }

    //узнаю рейтинг игрока
    private int Rating(String name) throws SQLException {
        String NAME;
        int rating = 0, i = 1; //имитация порядкового номера
        //выбираю ID игроков с сортировкой по убыванию ПвП-опыт
        query = "SELECT name FROM players ORDER BY pvp_exp DESC, exp DESC, pvp_v DESC, pve_v DESC;";
        res = statement.executeQuery(query); //запускаю запрос
        while (res.next()) {
            NAME = res.getString("name");
            if (NAME.equalsIgnoreCase(name)) {
                rating = i;
                break; //остановка цикла
            }
            i++;  //инкремент
        }
        return rating;
    }

    //узнаю максимальный урон и по необходимости пишу в БД
    private void MaxUron(ArrayList<Integer> hero, ArrayList<Integer> enemy) throws SQLException {
        int maxMyUron = getMaxValue(hero);
        if (maxMyUron > players.getMaxMyUron()){ //если мак. урон за бой больше того, что в БД
            //пишу новое значение мак. нанесенного урона в БД
            query = "UPDATE players SET maxMyUron = " + maxMyUron + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        }
        int maxEnemyUron = getMaxValue(enemy);
        if (maxEnemyUron > players.getMaxMyUron()){ //если мак. урон за бой больше того, что в БД
            //пишу новое значение мак. полученого урона в БД
            query = "UPDATE players SET maxEnemyUron = " + maxEnemyUron + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        }
    }

    //узнаю максимальное значение массива
    private int getMaxValue(ArrayList<Integer> array){
        int max = array.get(0); // берем первый элемент массива
        for (int i = 0; i < array.size(); i++) { // переберем весь массив
            if (max < array.get(i)) max = array.get(i); // если элемент больше, чем в переменной, то присваиваем его значение переменной
        }
        return max;
    }

    //заношу к-во поражений
    private void Lose() throws SQLException {
        if (bots.getIsLive() == 1){ //если это была ПвП битва
            query = "UPDATE players SET pvp_l = pvp_l + 1 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        } else { //если это была ПвЕ битва
            query = "UPDATE players SET pve_l = pve_l + 1 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        }
    }

    //заношу к-во побед
    private void Victory() throws SQLException {
        if (bots.getIsLive() == 1){ //если это была ПвП битва
            query = "UPDATE players SET pvp_v = pvp_v + 1 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        } else { //если это была ПвЕ битва
            query = "UPDATE players SET pve_v = pve_v + 1 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        }
    }

    //выбираю подарок за ежедневное посещение игры
    private String Bounty() throws SQLException {
        String m = "NO_BOUNTY";
        Date today = new Date(Calendar.getInstance().getTimeInMillis()); //сегодняшний день
        double days = (today.getTime() - players.getLastDate().getTime()) / 1000 / 60 / 60 / 24; //вычитаю разницу дней после последнего входа в игру
        if (days != 0) { //если клиент заходит в игру первый раз за сегодня
            m = "YES_BOUNTY";
            this.bounty = new Bounty(); //поключаю класс подарков
            if (days == 1) { //если игрок вчера посещал игру
                players.setDays(players.getDays() + 1);  //добавляю еще один день за ежедневное посещение игры
                if (players.getDays() > 7) players.setDays(1); //если всю неделю получал подарки, тогда заново с первого дня
                BountySelect(bounty); //выбираю подарки в сеттеры и пишу их игроку
            } else { //если игрок был в игре давно
                players.setDays(1); //пишу игроку первый день посещение после перерыва
                BountySelect(bounty); //выбираю подарки в сеттеры и пишу их игроку
            }
        }
        return m;
    }

    //метод выбора и записи подарков
    private void BountySelect(Bounty bounty) throws SQLException {
        if (players.getVip_lvl() == 0) players.setVip_lvl(1); //определяю VIP-умножитель
        //вибираю подарки
        query = "SELECT * FROM bounty WHERE days = " + players.getDays() + ";"; //код SQL-запроса
        res = statement.executeQuery(query); //запускаю запрос
        while (res.next()) {
            bounty.setMoney(res.getInt("money") * players.getVip_lvl());
            bounty.setGold(res.getDouble("gold") * players.getVip_lvl());
        }
        players.setMoney(players.getMoney() + bounty.getMoney());
        players.setGold(players.getGold() + bounty.getGold());
        //пишу новое значение монет, голда и новый день
        query = "UPDATE players SET money = " + players.getMoney() +
                ", gold = " + players.getGold() + ", days = " + players.getDays() + "" +
                ", LastDate = CURRENT_DATE() WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //выполнение запроса
        //пишу к-во подареных голдов
        query = "UPDATE players SET BountyGold = BountyGold + " +  bounty.getGold() + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.execute(query);  //выполняю запрос
        float fGold = (float) bounty.getGold(); //пишу значение голда у float, что бы обрезать лишнее после запятой
        msg = players.getDays() + "-й день: " + bounty.getMoney() + " монет и " + fGold + " G. Обязательно заходи завтра, подарки будут еще круче!";
    }

    //метод снятия денег
    private void Pay(int money, double G) throws SQLException {
        //снимаю деньги
        query = "UPDATE players SET money = money - " + money + ", gold = gold - " + G + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.execute(query);  //выполняю запрос
        //пишу статистику потраченых голдов
        query = "UPDATE players SET LastGold = LastGold + " + G + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.execute(query);  //выполняю запрос
        players.setHp(players.getMax_hp());
        players.setMana(players.getMax_mana());
        UpdateHpHero();
        UpdateManaHero();
    }

    //создаю нового героя
    private boolean CreateNewHero (String name) throws SQLException {
        boolean createNewHero = false;
        query = "INSERT INTO players (id_players, name, pass, isPass, maxEnemyUron, maxMyUron, pvp_v, pvp_l, pve_l, pve_v, DateBegin, LastDate, isHoliday, " +
                "isBirthday, BountyGold, LoseGold, days, lvl, exp, vip_lvl, vip_exp, pvp_lvl, pvp_exp, str, dex, inst, def, hp, max_hp, mana, " +
                "max_mana, money, gold, id_bots, DateBirth, sex, avatar) " +
                "VALUES ('" + players.getId() + "', '" + name + "', '" + players.getPass() + "', 0, 0, 0, 0, 0, 0, 0, CURRENT_DATE(), CURRENT_DATE()-1, 1, 0, 0, 0, 0," +
                "1, 0, 0, 0, 1, 0, 10, 10, 10, 10, 40, 40, 0, 0, 0, 0, " +
                bots.getId() + ", '" + players.getsDateBirth() + "', " + players.getSex() + ", '" + players.getAvatar() + "');"; //код SQL-запроса
        statement.execute(query);  //выполняю запрос
        //ложу в инвентарь нового героя две первые вещи
        query = "INSERT INTO inventory (id_players, id_Objects, kol) " +
                "VALUES ('" + players.getId() + "', 1, 1);"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        query = "INSERT INTO inventory (id_players, id_Objects, kol) " +
                "VALUES ('" + players.getId() + "', 2, 1);"; //код SQL-запроса
        statement.addBatch(query); //запрос в очереди
        statement.executeBatch(); //выполнение всех запросов
        statement.clearBatch(); //очищает весь пакет запросов
        createNewHero = true;
        return createNewHero;
    }

    //создаю бота-клона нового героя
    private boolean CreateNewBot (String name) throws SQLException {
        boolean createNewBot = false;
        query = "INSERT INTO bots (name, lvl, str, dex, inst, def, hp, mana," +
                "isLive, exp, money_min, money_max, pvp_lvl, pvp_exp, sex) " +
                "VALUES ('" + name + "', 1, 10, 10, 10, 10, 40, 0, 1, 10, 25, 100, 1, 0, " + players.getSex() + ");"; //код SQL-запроса
        statement.execute(query);  //выполняю запрос
        createNewBot = true;
        return createNewBot;
    }

    //узнаю ID бота-клона нового героя
    private int NewIdBot () throws SQLException {
        int newIdBot = 0;
        query = "SELECT last_insert_id() AS ID;"; //код SQL-запроса
        res = statement.executeQuery(query); //запускаю запрос
        while (res.next()) {
            newIdBot = (res.getInt("ID"));
        }
        return newIdBot;
    }

    //проверка имени героя
    private boolean IsNameHero (String name) throws SQLException {
        boolean isNameHero = false;
        query = "SELECT COUNT(*) AS kol FROM players p WHERE p.name = '" + name + "';";
        res = statement.executeQuery(query); //запускаю запрос
        setCount = 1;
        while (res.next()) {
            setCount = res.getInt("kol");
        }
        if (setCount == 0) {  //если героев с таким именем не существует, проверяю список ботов
            query = "SELECT COUNT(*) AS kol FROM bots b WHERE b.name = '" + name + "';";
            res = statement.executeQuery(query); //запускаю запрос
            setCount = 1;
            while (res.next()) {
                setCount = res.getInt("kol");
            }
            if (setCount == 0) { //если такое имя не применялось в БД
                isNameHero = true;
            }
        }
        return isNameHero;
    }

    //проверяю наличия героя для этого ID
    private boolean IsPlayer () throws SQLException {
        boolean isPlayers = false;
        query = "SELECT COUNT(*) AS kol FROM players p WHERE p.id_players = '" + players.getId() + "';";
        res = statement.executeQuery(query); //запускаю запрос
        setCount = 0;
        while (res.next()) {
            setCount = res.getInt("kol");
        }
        if (setCount == 1) {
            isPlayers = true;
        }
        return isPlayers;
    }

    //проверка на ПвП-апп
    private String UpPvP (int exp) throws SQLException {
        String title = "";
        if ((players.getPvp_exp() + exp) >= players.getMax_pvp_exp()) { //если набраный опыт больше мак. опыта по званию,
            //тогда увеличиваем все статы перса
            query = "UPDATE players SET pvp_lvl = pvp_lvl + 1, str = str + 2, dex = dex + 2, inst = inst + 2, def = def + 2," +
                    " max_hp = max_hp + 5, hp = max_hp, max_mana = max_mana + 2, mana = max_mana, gold = gold + 2," +
                    " money = money + " + players.getPvp_money() +
                    " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
            //тогда увеличиваем все статы бота-клона
            query = "UPDATE bots SET pvp_lvl = " + (players.getPvp_lvl() + 1) + ", str = " + (players.getStr() + 2) +
                    ", dex = " + (players.getDex() + 2) + ", inst = " + (players.getInst() + 2) +
                    ", def = " + (players.getDef() + 2) + ", hp = " + (players.getMax_hp() + 5) +
                    ", mana = " + (players.getMana() + 2) + " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
            statement.addBatch(query); //запрос в очереди
            query = "UPDATE players SET BountyGold = BountyGold + 2 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса (обновляю данные бота-клона персонажа)
            statement.addBatch(query); //запрос в очереди
            statement.executeBatch(); //выполнение всех запросов
            statement.clearBatch(); //очищает весь пакет запросов
            //узнаю звание
            query = "SELECT pvp.pvp_name FROM pvp_exp pvp LEFT JOIN players p ON p.pvp_lvl = pvp.pvp_lvl" +
                    " WHERE id_players = '" + players.getId() + "';";
            res = statement.executeQuery(query); //запускаю запрос
            while (res.next()) {
                title = res.getString("pvp_name");
            }
        }
        return title;
    }

    //проверка на лвл-апп
    private boolean UpLvl (int exp) throws SQLException {
        boolean upLvl = false;
        if ((players.getExp() + exp) >= players.getMax_exp()) { //если набраный опыт больше мак. опыта по уровню,
            //тогда увеличиваем все статы перса на upSt
            query = "UPDATE players SET lvl = lvl + 1, str = str + 2, dex = dex + 2, inst = inst + 2, def = def + 2," +
                    " max_hp = max_hp + 5, hp = max_hp, max_mana = max_mana + 2, mana = max_mana, gold = gold + 1," +
                    " money = money + " + players.getExp_money() + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
            query = "UPDATE bots SET lvl = " + (players.getLvl() + 1) + ", str = " + (players.getStr() + 2) +
                    ", dex = " + (players.getDex() + 2) + ", inst = " + (players.getInst() + 2) +
                    ", def = " + (players.getDef() + 2) + ", hp = " + (players.getMax_hp() + 5) +
                    ", mana = " + (players.getMana() + 2) + " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
            statement.addBatch(query); //запрос в очереди
            query = "UPDATE players SET BountyGold = BountyGold + 2 WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса (обновляю данные бота-клона персонажа)
            statement.addBatch(query); //запрос в очереди
            statement.executeBatch(); //выполнение всех запросов
            statement.clearBatch(); //очищает весь пакет запросов
            upLvl = true;
        }
        return upLvl;
    }

    //выборка всех данных о игроке
    private void SelectAllFromPlayers() throws SQLException {
        query = "SELECT * FROM players p, vip_exp v, exp, pvp_exp pvp" +
                " WHERE id_players = '" + players.getId() +
                "' AND p.vip_lvl = v.vip_lvl" +
                " AND p.lvl = exp.lvl" +
                " AND p.pvp_lvl = pvp.pvp_lvl;"; //код SQL-запроса
        res = statement.executeQuery(query); //запускаю запрос
        //в цикле прохожу всю таблицу и пишу данные в сеттеры
        while (res.next()) {
            players.setName(res.getString("name"));
            players.setLvl(res.getInt("lvl"));
            players.setPvp_lvl(res.getInt("pvp_lvl"));
            players.setVip_lvl(res.getInt("vip_lvl"));
            players.setStr(res.getInt("str"));
            players.setDex(res.getInt("dex"));
            players.setInst(res.getInt("inst"));
            players.setDef(res.getInt("def"));
            players.setHp(res.getInt("hp"));
            players.setMax_hp(res.getInt("max_hp"));
            players.setMana(res.getInt("mana"));
            players.setMax_mana(res.getInt("max_mana"));
            players.setSize_inventory(res.getInt("size_inventory"));
            players.setMoney(res.getInt("money"));
            players.setGold(res.getDouble("gold"));
            players.setExp(res.getInt("exp"));
            players.setPvp_exp(res.getInt("pvp_exp"));
            players.setMin_pvp_exp(res.getInt("min_pvp_exp"));
            players.setMax_pvp_exp(res.getInt("max_pvp_exp"));
            players.setMax_exp(res.getInt("max_exp"));
            players.setExp_money(res.getInt("exp_money"));
            players.setVip_money(res.getInt("vip_money"));
            players.setPvp_money(res.getInt("pvp_money"));
            players.setId_bots(res.getInt("id_bots"));
            players.setPvp_name(res.getString("pvp_name"));
            players.setLastDate(res.getDate("LastDate"));
            players.setdDateBirth(res.getDate("DateBirth"));
            players.setDays(res.getInt("days"));
            players.setMaxMyUron(res.getInt("maxMyUron"));
            players.setMaxEnemyUron(res.getInt("maxEnemyUron"));
            players.setPve_v(res.getInt("pve_v"));
            players.setPve_l(res.getInt("pve_l"));
            players.setPvp_v(res.getInt("pvp_v"));
            players.setPvp_l(res.getInt("pvp_l"));
            players.setSex(res.getInt("sex"));
            players.setAvatar(res.getString("avatar"));
            players.setHoliday(res.getInt("isHoliday"));
            players.setBirthday(res.getInt("isBirthday"));
            players.setPass(res.getString("pass"));
            players.setIsPass(res.getInt("isPass"));
        }
    }

    //выборка всех данных о противнике
    private void SelectAllFrom_Bots(int id) throws SQLException {
        query = "SELECT * FROM bots WHERE id_bots = "+id; //код SQL-запроса
        res = statement.executeQuery(query); //запускаю запрос
        //в цикле прохожу всю таблицу и пишу данные в сеттеры
        while (res.next()) {
            bots.setName(res.getString("name"));
            bots.setLvl(res.getInt("lvl"));
            bots.setStr(res.getInt("str"));
            bots.setDex(res.getInt("dex"));
            bots.setInst(res.getInt("inst"));
            bots.setDef(res.getInt("def"));
            bots.setHp(res.getInt("hp"));
            bots.setMana(res.getInt("mana"));
            bots.setIsLive(res.getInt("isLive"));
            bots.setExp(res.getInt("exp"));
            bots.setPvp_exp(res.getInt("pvp_exp"));
            bots.setMoney_min(res.getInt("money_min"));
            bots.setMoney_max(res.getInt("money_max"));
            bots.setSex(res.getInt("sex"));
        }
    }

    //запись нового значения ОЗ героя
    private void UpdateHpHero() throws SQLException {
        /*if (players.getHp() == 0) {
            players.setHp((int) (players.getMax_hp() * players.getVip_hp()) / 100);
        }*/
        query = "UPDATE players SET hp = " + players.getHp() + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //запускаю запрос
    }

    //запись нового значения маны героя
    private void UpdateManaHero() throws SQLException {
        query = "UPDATE players SET mana = " + players.getMana() + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //запускаю запрос
    }

    //опыт при победе
    private int Exp() throws SQLException {
        int exp = bots.getExp();
        if (bots.getIsLive() == 1) {  //если опонет - игрок
            exp = (int) (exp * (AverageTwo() + players.getVip_lvl()));
        } else {  //если опонет - бот
            if (players.getVip_lvl() > 0) {
                exp = exp * players.getVip_lvl();
            }
        }
        if ((players.getLvl() + 3) <= bots.getLvl()) { exp = exp * 2; }  //если уровень бота больше на 3 уровня и выше чем уровень перса
        if ((players.getLvl() - 2) == bots.getLvl()) { exp = (int) (exp / 2); }  //если уровень бота на 2 уровня меньше чем уровень перса
        if ((players.getLvl() - 3) == bots.getLvl()) { exp = (int) (exp / 4); }  //если уровень бота на 3 уровня меньше чем уровень перса
        if ((players.getLvl() - 4) >= bots.getLvl()) {  //если уровень бота на 4 уровня или еще меньше чем уровень перса
            if (bots.getPvp_exp() >= players.getPvp_exp()) { //но его храбрость больше
                exp = (int) (exp / 6);
            } else {  //иначе
                exp = 0;  //перс не получает опыта
            }
        }
        query = "UPDATE players SET exp = exp + " + exp + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //выполнение запроса
        return exp;
    }

    //деньги при победе
    private int Money() throws SQLException {
        int money = (int) (RandomTwo(bots.getMoney_min(), bots.getMoney_max()));
        if (bots.getIsLive() == 1) {  //если опонет - игрок
              money = (int) (money * (AverageTwo() + players.getVip_lvl()));
        } else {  //если опонет - бот
            if (players.getVip_lvl() > 0) {
                money = money * players.getVip_lvl();
            }
        }
        if ((players.getLvl() + 3) <= bots.getLvl()) { money = money * 2; }  //если уровень бота больше на 3 уровня и выше чем уровень перса
        if ((players.getLvl() - 2) == bots.getLvl()) { money = (int) (money / 2); } //если уровень бота на 2 уровня меньше чем уровень перса
        if ((players.getLvl() - 3) == bots.getLvl()) { money = (int) (money / 4); }  //если уровень бота на 3 уровня меньше чем уровень перса
        if ((players.getLvl() - 4) >= bots.getLvl()) money = 0;  //если уровень бота на 4 уровня или еще меньше чем уровень перса
        query = "UPDATE players SET money = money + " + money + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //выполнение запроса
        return money;
    }

    //голды при победе
    /*
    private int Money() throws SQLException {
        int money = (int) (RandomTwo(bots.getMoney_min(), bots.getMoney_max()));
        if (bots.getIsLive() == 1) {  //если опонет - игрок
            money = (int) (money * (AverageTwo() + players.getVip_lvl()));
        } else {  //если опонет - бот
            if (players.getVip_lvl() > 0) {
                money = money * players.getVip_lvl();
            }
        }
        if ((players.getLvl() + 3) <= bots.getLvl()) { money = money * 2; }  //если уровень бота больше на 3 уровня и выше чем уровень перса
        if ((players.getLvl() - 2) == bots.getLvl()) { money = (int) (money / 2); } //если уровень бота на 2 уровня меньше чем уровень перса
        if ((players.getLvl() - 3) == bots.getLvl()) { money = (int) (money / 4); }  //если уровень бота на 3 уровня меньше чем уровень перса
        if ((players.getLvl() - 4) >= bots.getLvl()) {  //если уровень бота на 4 уровня или еще меньше чем уровень перса
            if (bots.getPvp_exp() > players.getPvp_exp()) { //но его храбрость больше
                money = (int) (money / 6);
            } else {  //иначе
                money = 0;  //перс не получает денег
            }
        }
        query = "UPDATE players SET money = money + " + money + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.executeUpdate(query); //выполнение запроса
        return money;
    }
    */

    //Храбрость при победе
    private int Courage() throws SQLException {
        int exp = 0;
        if (bots.getIsLive() == 1) {
            exp = (int) (PVP_EXP * (AverageTwo() + players.getVip_lvl()));
            //если уровень бота больше на 3 уровня и выше чем уровень перса
            if ((players.getLvl() + 3) <= bots.getLvl()) {
                exp = exp * 2;
            }
            //если уровень бота на 2 уровня меньше чем уровень перса
            if ((players.getLvl() - 2) == bots.getLvl()) {
                exp = (int) (exp / 2);
            }
            //если уровень бота на 3 уровня меньше чем уровень перса
            if ((players.getLvl() - 3) == bots.getLvl()) {
                exp = (int) (exp / 3);
            }
            //если уровень бота на 4 уровня меньше и еще меньше чем уровень перса
            if ((players.getLvl() - 4) >= bots.getLvl()) {
                if (bots.getPvp_exp() >= players.getPvp_exp()) { //но его храбрость больше
                    exp = exp / 4;
                } else {  //иначе
                    exp = 0;  //перс не получает Храбрость
                }
            }
            query = "UPDATE players SET pvp_exp = pvp_exp + " + exp + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
            query = "UPDATE bots SET pvp_exp = " + (players.getPvp_exp() + exp) +
                    " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
            statement.addBatch(query); //запрос в очереди
            statement.executeBatch(); //выполнение всех запросов
            statement.clearBatch(); //очищает весь пакет запросов
        }
        return exp;
    }

    //Храбрость при проиграше в ПвП
    private int LoseCourage() throws SQLException {
        int exp = 0;
        if (bots.getIsLive() == 1) {
            exp = (int) (PVP_EXP * AverageTwo());
            if ((players.getLvl() + 4) <= bots.getLvl()) {
                exp = 0;
            } //если уровень бота больше на 4 уровня и выше чем уровень перса
            if ((players.getLvl() + 3) == bots.getLvl()) {
                exp = (int) (exp / 5);
            }  //если уровень бота больше на 3 уровня чем уровень перса
            if ((players.getLvl() + 2) == bots.getLvl()) {
                exp = (int) (exp / 4);
            }  //если уровень бота больше на 2 уровня чем уровень перса
            if ((players.getLvl() + 1) == bots.getLvl()) {
                exp = (int) (exp / 3);
            }  //если уровень бота больше на 1 уровня чем уровень перса
            if (players.getLvl() == bots.getLvl()) {
                exp = (int) (exp / 2);
            } //если уровень бота равен уровеню перса
            if ((players.getLvl() - 1) == bots.getLvl()) {
                exp = (int) (exp / 1);
            }  //если уровень бота на единицу меньше уровня перса
            if ((players.getLvl() - 2) == bots.getLvl()) {
                exp = exp * 2;
            }  //если уровень бота на 2 уровня меньше чем уровень перса
            if ((players.getLvl() - 3) == bots.getLvl()) {
                exp = exp * 4;
            }  //если уровень бота на 3 уровня меньше чем уровень перса
            if ((players.getLvl() - 4) >= bots.getLvl()) {
                exp = exp * 6;
            }  //если уровень бота на 4 уровня или еще меньше чем уровень перса
            if ((players.getPvp_exp() - exp) < players.getMin_pvp_exp()) {  //если Храбрость после снятие будет меньше минимальной Храбрости
                exp = players.getPvp_exp() - players.getMin_pvp_exp();
                if (exp == 0) {
                    exp = -1;  //флаг, что не надо снимать Храбрость, потому что уже минимальный уровень ПвП-опыта
                }
            }
            if (exp != -1) {
                query = "UPDATE players SET pvp_exp = pvp_exp - " + exp + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
                statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
                query = "UPDATE bots SET pvp_exp = " + (players.getPvp_exp() + exp) +
                        " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
                statement.addBatch(query); //запрос в очереди
                statement.executeBatch(); //выполнение всех запросов
                statement.clearBatch(); //очищает весь пакет запросов
            }
        }
        return exp;
    }

    //деньги при проиграше
    private int LoseMoney() throws SQLException {
        int money = (int) (RandomTwo(bots.getMoney_min(), bots.getMoney_max()));
        if (bots.getIsLive() == 1) {  //если опонет - игрок
            money = (int) (money * (AverageTwo()));
        }
        if ((players.getLvl() + 4) <= bots.getLvl()) { money = 0; } //если уровень бота больше на 4 уровня и выше чем уровень перса
        if ((players.getLvl() + 3) == bots.getLvl()) { money = (int) (money / 5); }  //если уровень бота больше на 3 уровня и выше чем уровень перса
        if ((players.getLvl() + 2) == bots.getLvl()) { money = (int) (money / 4); }  //если уровень бота больше на 2 уровня чем уровень перса
        if ((players.getLvl() + 1) == bots.getLvl()) { money = (int) (money / 3); }  //если уровень бота больше на 1 уровня чем уровень перса
        if (players.getLvl() == bots.getLvl()) { money = (int) (money / 2); }  //если уровень бота равен уровеню перса
        if ((players.getLvl() - 1) == bots.getLvl()) { money = money * 1; } //если уровень бота на единицу меньше уровня перса
        if ((players.getLvl() - 2) == bots.getLvl()) { money = money * 2; }  //если уровень бота на 2 уровня меньше чем уровень перса
        if ((players.getLvl() - 3) == bots.getLvl()) { money = money * 4; }  //если уровень бота на 3 уровня меньше чем уровень перса
        if ((players.getLvl() - 4) >= bots.getLvl()) { money = money * 6; }  //если уровень бота на 4 уровня или еще меньше чем уровень перса
        if ((players.getMoney() - money) < 0) {  //если денег после снятие будет меньше 0
            money = players.getMoney();
            if (money == 0) {
                money = -1;  //флаг, что не надо снимать Храбрость, потому что уже минимальный уровень ПвП-опыта
            }
        }
        if (money != -1) {
            //снимаю денежные средства перса
            query = "UPDATE players SET money = money - " + money + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.executeUpdate(query); //выполнение запроса
        }
        return money;
    }

    //рандом между любых двух чисел
    private static int RandomTwo(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    //среднее арифметическое
    private double AverageTwo() {
        return (players.getLvl() + players.getPvp_lvl());
    }

    //выборка дропа
    private boolean Drop() throws SQLException {
        boolean drop = false;
        if (bots.getLvl() >= players.getLvl() - 1) { //если уровень бота больше чем уровень перса минус один уровень
            int countDrop = 0;
            int i = 0;
            boolean l = false;
            boolean k = false;
            //узнаю к-во предметов, которые вообще могут выпасть дропом
            query = "SELECT COUNT(*) AS kol FROM drops d WHERE d.id_bots =" + bots.getId() + ";";
            res = statement.executeQuery(query); //запускаю запрос
            while (res.next()) {
                countDrop = res.getInt("kol");
            }
            //иницилизирую масивы
            this.nameDrop = new String[countDrop];
            double [] chance = new  double[countDrop];
            String [] name = new String[countDrop];
            int [] id_Objects = new int[countDrop];
            int [] isStakan = new int[countDrop];
            int [] isQuest = new int[countDrop];
            int [] id_quests = new int[countDrop];
            int [] countForQuest = new int[countDrop];
            String [] questName = new String[countDrop];
            //вытягиваю весь возможный дроп и пишу значения в массивы
            query = "SELECT d.chance, o.name, o.id_Objects, o.isStakan, o.isQuest, o.id_quests, o.CountForQuest, q.name as questName" +
                    " FROM drops d" +
                    " LEFT JOIN bots b ON d.id_bots=b.id_bots" +
                    " LEFT JOIN objects o ON o.id_Objects=d.id_Objects" +
                    " LEFT JOIN quests q on o.id_quests=q.id_quests" +
                    " WHERE b.id_bots =" + bots.getId() + ";"; //код SQL-запроса
            res = statement.executeQuery(query); //запускаю запрос
            while (res.next()) {  //в цикле прохожу всю таблицу запроса и пишу данные в массивы
                chance[i] = res.getDouble("chance");
                name[i] = res.getString("name");
                id_Objects[i] = res.getInt("id_Objects");
                isStakan[i] = res.getInt("isStakan");
                isQuest[i] = res.getInt("isQuest");
                id_quests[i] = res.getInt("id_quests");
                countForQuest[i] = res.getInt("CountForQuest");
                questName[i] = res.getString("questName");
                i++;  //инкремент
            }
            //ищу реальный дроп и пишу его в массив
            for(i = 0; i < countDrop; i++){
                if (Math.random() <= chance[i]) {  //если вещь выпала
                    drop = true;
                    if (isStakan[i] == 1) { //и она стаканется
                        //ищу такие же самые предметы в рюкзаке перса
                        query = "SELECT COUNT(*) AS kol FROM inventory i WHERE i.id_players = '" + players.getId() +
                                "' AND i.id_Objects = " + id_Objects[i] + ";"; //код SQL-запроса
                        res = statement.executeQuery(query); //запускаю запрос
                        while (res.next()) {
                            setCount = res.getInt("kol");
                        }
                        if(setCount == 1) { //если такие предметы есть в рюкзаке этого перса
                            //узнаю количество этих предметов
                            query = "SELECT kol FROM inventory i WHERE i.id_players = '" + players.getId() +
                                    "' AND i.id_Objects = " + id_Objects[i] + ";"; //код SQL-запроса
                            res = statement.executeQuery(query); //запускаю запрос
                            while (res.next()) {
                                setCount = res.getInt("kol");
                            }
                            if (setCount < countForQuest[i]) {  //если к-во таких предметов не превышает мак. к-ва
                                //добавляю еще одно кол-во
                                query = "UPDATE inventory SET kol = kol + 1 WHERE id_players = '" + players.getId() +
                                        "' AND id_Objects = " + id_Objects[i] + ";"; //код SQL-запроса
                                statement.executeUpdate(query); //выполнение запроса
                                nameDrop[i] = ", " + name[i];  //добавляю в масив название дропа
                            } else {  //если к-во таких предметов равно максимальному
                                nameDrop[i] = ", завершите квест \"" + questName[i] + "\"";  //добавляю в масив название дропа
                            }
                        } else { //если таких вещей нет в рюкзаке этого перса
                            l = true;  //добавляю новую запись в таблицу inventory
                        }
                    } else { //если предмет не стаканется
                        l = true;  //добавляю новую запись в таблицу inventory
                    }
                }
                if (l == true) {
                    if (isQuest[i] == 1) { //если эта вещь квестовая
                        //проверяю взят ли такой квест на выполнение
                        query = "SELECT COUNT(*) AS kol FROM execution WHERE id_players = '" + players.getId() +
                                "' AND id_quests = " + id_quests[i] + ";"; //код SQL-запроса
                        res = statement.executeQuery(query); //запускаю запрос
                        while (res.next()) {
                            setCount = res.getInt("kol");
                        }
                        if (setCount  == 1) { //такой квест выполняется
                            k = true; //добавляю новый предмет в рюкзак этого перса
                        }
                    } else {
                        //проверяю к-во не квестовых предметов в рюкзаке
                        query = "SELECT COUNT(*) AS kol FROM inventory i " +
                                "LEFT JOIN objects o ON i.id_Objects = o.id_Objects " +
                                "WHERE i.id_players = '" + players.getId() +
                                "' AND (o.isQuest IS NULL OR o.isQuest = '' OR o.isQuest = 0);"; //код SQL-запроса
                        res = statement.executeQuery(query); //запускаю запрос
                        while (res.next()) {
                            setCount = res.getInt("kol");
                        }
                        if (setCount < players.getSize_inventory()) { //если есть место в рюкзаке
                            k = true; //добавляю новый предмет в инвентарь этого перса
                        } else {  //если нет места в инвентаре
                            nameDrop[i] = ", рюкзак переполнен";  //добавляю в масив название дропа
                        }
                    }
                }
                if (k == true) {
                    query = "INSERT INTO inventory (id_players, id_Objects, kol) " +
                            "VALUES ('" + players.getId() + "', " + id_Objects[i] + ", 1);"; //код SQL-запроса
                    statement.execute(query);  //выполняю запрос
                    nameDrop[i] = ", " + name[i];  //добавляю в масив название дропа
                }
                l = false;
                k = false;
            }
        }
        return drop;
    }
}


public class Main {

    //переменные для сокет-подключения
    static final int PORT = 8888;
    static String HostName = null;
    static long n = 0;
    static ServerSocket s;

    public static void main(String[] args) throws IOException {

        ConnectBD connectBD = new ConnectBD(); //конект с БД

        //попытка соеденится с портом
        try{
            s = new ServerSocket(PORT);
        }catch (IOException e) {
            System.out.println("Порт: "+PORT+" занят");
            return;
        }

        System.out.println("Сервер успешно запущен");
        System.out.println("Ожидаю клиентов....");

        try {
            //мониторю порт - бесконечный цикл
            while (true) {
                Socket socket = s.accept();  //подключаю клиента
                HostName=socket.getInetAddress().getHostName(); //получаю в переменную имя клиента
                n++;  //считаю к-во подключений
                System.out.println("Соединение № "+ n +". Подключился клиент: "+HostName);
                try {
                    new ServeOneThread(socket, HostName, n, connectBD);  //посылаю соединение в новый поток
                } catch (IOException e) {
                    System.out.println("Соединение № "+ n +" с клиентом: "+HostName+" невозможно, ошибка "+e);
                    socket.close();
                } catch (SQLException e) {
                    System.out.println("Соединение № "+ n +" с клиентом: "+HostName+" невозможно, ошибка "+e);
                    socket.close();
                }
            }
        } finally {
            s.close();
            System.out.println("Соединение с портом разорвано");
            System.out.println("Выход с сервер-программы");
        }
    }
}