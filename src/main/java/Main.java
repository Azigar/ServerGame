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
import java.util.Random;


class ServeOneThread extends Thread {

    //для работы сокета
    long n;
    String HostName;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String cmd; //переменная для распознавания команд от клиента

    //для работы с БД
    ConnectBD connectBD;
    PreparedStatement preparedStatement;
    Statement statement;
    ResultSet res;
    String query;

    //для работы с таблицами БД
    Bots bots;
    Players players;
    String [] nameDrop;

    Fight fight; //для обработки боя
    int pvp_exp = 24;

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
            System.out.println("Получил 1 "+cmd);
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
                    System.out.println("Получил 2 "+cmd);

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
                                out.println("END");  //отправляю клиенту, что надо закрыть потоки на конект
                                break; //выход с цикла
                            }

                            cmd = in.readLine();  //слушаю команды

                            System.out.println("Получил 3 "+cmd);

                            if (cmd.equalsIgnoreCase("END")) {
                                out.println("END");  //отправляю клиенту, что надо закрыть потоки на конект
                                break; //выход с цикла
                            }

                            //клиент хочет получить начальные хар-ки героя
                            if (cmd.equalsIgnoreCase("GET")) {  //клиент покидает игру

                                out.println("OK");  //передаю вопросы клиенту

                                while (true){ //слушаю команды
                                    cmd = in.readLine();  //запись в переменную полученые данные
                                    //клиент покидает игру
                                    if (cmd.equalsIgnoreCase("END")) {
                                        out.println("END");  //отправляю клиенту, что надо закрыть потоки на конект
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
                                        out.println(players.getGold());
                                    }
                                    if (cmd.equalsIgnoreCase("VIP")) { //отсылаю к-во голда у героя
                                        out.println(players.getVip_lvl());
                                    }
                                }
                            }

                            /////////////Клиент покидает игру
                            if (cmd.equalsIgnoreCase("SAVE_GAME")) {
                                //запрашиваю HP героя
                                out.println("HP");
                                cmd = in.readLine();
                                if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) { ////////////клиент выходит с игры
                                    out.println("END"); //отправляю клиенту команду на закрытие сокета и потоков кли
                                    break; // сервер закрывает сокет и поток
                                } else {
                                    players.setHp(Integer.parseInt(cmd)); //получаю ОЗ героя
                                }
                                //запрашиваю ману героя
                                out.println("MANA");
                                cmd = in.readLine();
                                if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) { ////////////клиент выходит с игры
                                    out.println("END"); //отправляю клиенту команду на закрытие сокета и потоков кли
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
                                if (players.getHp() < players.getMax_hp()) { //если хп меньше чем нужно
                                    int i_money = 0, i_G = 0;
                                    String s_money = "", s_G = "";
                                    double ch = ((double) players.getHp() / (double) players.getMax_hp()) * 100;
                                    //нада выставлять от старших уровней до меньших
                                    if (ch < 99 && players.getLvl() < 10) {
                                        i_money = 100 * players.getLvl(); i_G = 0;
                                    }
                                    if (ch < 66 && players.getLvl() < 10) {
                                        i_G = ((int) players.getLvl() / 4); i_money = 0;
                                    }
                                    if (ch < 33 && players.getLvl() < 10) {
                                        i_G = ((int) players.getLvl() / 2); i_money = 0;
                                    }
                                    if (ch < 99 && players.getLvl() < 5) {
                                        i_money = 25 * players.getLvl(); i_G = 0;
                                    }
                                    if (ch < 66 && players.getLvl() < 5) {
                                        i_money = 50 * players.getLvl(); i_G = 0;
                                    }
                                    if (ch < 33 && players.getLvl() < 5) {
                                        i_money = 75 * players.getLvl(); i_G = 0;
                                    }
                                    if (i_money != 0) {
                                        s_money = i_money + " монет.";
                                    }
                                    if (i_G != 0) {
                                        s_G = i_G + " G.";
                                    }
                                    if (players.getMoney() >= i_money && players.getGold() >= i_G) { ///если хватит денег на реген
                                        out.println("REGEN");
                                        cmd = in.readLine();  //запись в переменную полученые данные
                                        if (cmd.equalsIgnoreCase("PRICE")) {
                                            out.println(s_money + s_G);
                                        }
                                        cmd = in.readLine();  //запись в переменную полученые данные
                                        if (cmd.equalsIgnoreCase("YES")) {
                                            Pay(i_money, i_G); //снять со счета игрока деньги за исцеление
                                            out.println("GOOD");
                                        } else {
                                            out.println("GOOD");
                                        }
                                    } else {
                                        out.println("NO_REGEN");
                                    }
                                } else {
                                    out.println("NO_REGEN");
                                }
                            }

                            /////////////клиент открыл АКТИВИТИ БОЯ, обработка запросов для боя
                            if (cmd.equalsIgnoreCase("FIGHT_ACTIVITY")) {
                                /////////////////загружаю клиенту данные для стартового окна
                                this.fight = new Fight();  //подключаю екземпляр класса БОЙ
                                this.bots = new Bots();  //подключаю екземпляр класса Боти

                                //выбираю все данные о противнике
                                out.println("ID_ENEMY"); //запрашиваю ID противника
                                cmd = in.readLine();

                                System.out.println("Получил 7 "+cmd);

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

                                System.out.println("Получил 8 "+cmd);

                                if (cmd.equalsIgnoreCase("WHO_FIRST")) {
                                    boolean firstAtk = fight.Shans(players.getInst(), bots.getInst(), 50);
                                    //System.out.println(firstAtk);
                                    if (firstAtk) {
                                        out.println("FIRST_PLAYER");
                                    } else {
                                        out.println("FIRST_ENEMY");
                                    }
                                }
                                ////////////////////////////Система боя
                                while (true) {
                                    /////////Ожидаю команду 1 (ожидаю направление удара/защиты, отмены боя, смерти опонентов и т.д.)
                                    cmd = in.readLine();  //слушаю команды боя

                                    System.out.println("Получил 9 "+cmd + " " + bots.getHp());

                                    if (cmd.equalsIgnoreCase("END") || cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {//клиент уходит с битвы
                                        out.println("END");  //отправляю клиенту, что надо закрыть потоки на конект
                                        break; //выход с цикла
                                    }

                                    //клиент бьет в голову
                                    if (cmd.equalsIgnoreCase("ATK_1")) {
                                        bots.setHp(fight.Fight(true, players.getName(), players.getStr() / 2, players.getStr(), players.getDex(), players.getInst(),
                                                false, bots.getName(), bots.getDex(), bots.getInst(), bots.getDef(), bots.getHp(), 1));
                                        //System.out.println("ОЗ бота "+bots.getHp());
                                        if (bots.getHp() <= 0) bots.setHp(0);
                                        out.println(bots.getHp());
                                    }
                                    //клиент бьет в торс
                                    if (cmd.equalsIgnoreCase("ATK_2")) {
                                        bots.setHp(fight.Fight(true, players.getName(), players.getStr() / 2, players.getStr(), players.getDex(), players.getInst(),
                                                false, bots.getName(), bots.getDex(), bots.getInst(), bots.getDef(), bots.getHp(), 2));
                                        if (bots.getHp() <= 0) bots.setHp(0);
                                        out.println(bots.getHp());
                                    }
                                    //клиент бьет в ноги
                                    if (cmd.equalsIgnoreCase("ATK_3")) {
                                        bots.setHp(fight.Fight(true, players.getName(), players.getStr() / 2, players.getStr(), players.getDex(), players.getInst(),
                                                false, bots.getName(), bots.getDex(), bots.getInst(), bots.getDef(), bots.getHp(), 3));
                                        if (bots.getHp() <= 0) bots.setHp(0);
                                        out.println(bots.getHp());
                                    }
                                    //клиент защищает голову
                                    if (cmd.equalsIgnoreCase("DEF_1")) {
                                        //метод расчета ОЗ игрока. Параметры: хар-ки бота, хар-ки игрока, направление удара 1 - голова
                                        players.setHp(fight.Fight(false, bots.getName(), bots.getStr() / 2, bots.getStr(), bots.getDex(), bots.getInst(),
                                                true, players.getName(), players.getDex(), players.getInst(), players.getDef(), players.getHp(), 1));
                                        if (players.getHp() <= 0) players.setHp(0);
                                        out.println(players.getHp());
                                    }
                                    //клиент защищает торс
                                    if (cmd.equalsIgnoreCase("DEF_2")) {
                                        //метод расчета ОЗ игрока. Параметры: хар-ки бота, хар-ки игрока, направление удара 1 - голова
                                        players.setHp(fight.Fight(false, bots.getName(), bots.getStr() / 2, bots.getStr(), bots.getDex(), bots.getInst(),
                                                true, players.getName(), players.getDex(), players.getInst(), players.getDef(), players.getHp(), 2));
                                        if (players.getHp() <= 0) players.setHp(0);
                                        out.println(players.getHp());
                                    }
                                    //клиент защищает ноги
                                    if (cmd.equalsIgnoreCase("DEF_3")) {
                                        //метод расчета ОЗ игрока. Параметры: хар-ки бота, хар-ки игрока, направление удара 1 - голова
                                        players.setHp(fight.Fight(false, bots.getName(), bots.getStr() / 2, bots.getStr(), bots.getDex(), bots.getInst(),
                                                true, players.getName(), players.getDex(), players.getInst(), players.getDef(), players.getHp(), 3));
                                        if (players.getHp() <= 0) players.setHp(0);
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
                                        String m;
                                        if (txtLoseMoney == "") {
                                            out.println("У Вас уже нечего забирать");
                                            m = "У Вас уже нечего забирать";
                                        } else {
                                            out.println(txtLoseMoney + txtLoseCourage);  //отправляю клиенту комент о поражении
                                            m = txtLoseMoney + txtLoseCourage;
                                        }
                                        System.out.println(m);
                                        UpdateHpHero();  //обновляю в БД ОЗ игрока
                                        break; //выход с цикла
                                    }

                                    //клиент победил в бою
                                    if (cmd.equalsIgnoreCase("VICTORY") && bots.getHp() <= 0) {

                                        System.out.println("попал сюда");

                                        //определяю очки опыта
                                        String txtExp = "";
                                        int exp = Exp();
                                        if (exp > 0) {
                                            txtExp = "Вы заработали " + exp + " опыта.";
                                        }
                                        //определяю полученые деньги
                                        String txtMoney = "";
                                        int money = Money();
                                        if (money > 0) {
                                            txtMoney = money + " монет";
                                        }
                                        //определяю дроп
                                        String txtDrop = "";
                                        Drop();
                                        for (int i = 0; i < nameDrop.length; i++) {  //собираю название дропа с масива в одну строку
                                            if (nameDrop[i] != null) { //если ячейка массива не null
                                                txtDrop = txtDrop + nameDrop[i];
                                            }
                                        }
                                        //определяю очки Храбрости
                                        String txtCourage = "";
                                        int courage = Courage();
                                        if (courage > 0) {
                                            txtCourage = " и " + courage + " Храбрости";
                                        }
                                        String m;
                                        if (txtExp == "" && txtMoney == "") {
                                            out.println("Вы обыскали труп врага, но ничего не нашли.");  //отправляю клиенту комент о дропе
                                            m = "Вы обыскали труп врага, но ничего не нашли.";
                                        } else {
                                            out.println(txtExp + txtCourage + " Обыскав труп врага, получили " + txtMoney + txtDrop + ".");  //отправляю клиенту комент о победе
                                            m = txtExp + txtCourage + ", обыскав труп врага, получили " + txtMoney + txtDrop + ".";
                                        }
                                        System.out.println(m);
                                        //Проверяю апп
                                        cmd = in.readLine();  //запись в переменную полученые данные

                                        System.out.println("Получил 15 "+cmd);

                                        if (cmd.equalsIgnoreCase("UP_LVL")) {
                                            if (UpLvl(exp) == true) { //если лвл-апп
                                                out.println("Вы достигли " + (players.getLvl() + 1) + "-го уровня, все хар-ки героя повышены. " +
                                                        "Вы получаете " + players.getExp_money() + " монет.");
                                            } else {  //если игрок не апнулся
                                                out.println("NO_LVL_UP");
                                                UpdateHpHero();  //обновляю в БД hp игрока
                                            }
                                        }
                                        cmd = in.readLine();  //запись в переменную полученые данные

                                        System.out.println("Получил 16 "+cmd);

                                        if (cmd.equalsIgnoreCase("UP_PVP")) {
                                            String title = UpPvP(courage);  //определяю звание при апп
                                            if (title != "") {  //если получил новое звание
                                                out.println("Вы получили звание " + title + ", все хар-ки героя повышены. " +
                                                        "Вы получаете " + players.getPvp_money() + " монет.");
                                            } else {
                                                out.println("NO_PVP_UP");
                                            }
                                        }
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
                        cmd = in.readLine();  //сохраняю имя нового героя

                        System.out.println("Получил 11 "+cmd);

                        if (cmd.equalsIgnoreCase("END") ||  cmd.equalsIgnoreCase("null") || cmd.equalsIgnoreCase(null)) {
                            break; //выход с цикла и потока
                        } else {
                            if (IsNameHero(cmd) == true) { //проверяю имя нового героя
                                if (CreateNewBot(cmd) == true) { //создаю бота-клона
                                    bots.setId(NewIdBot()); //пишу значение ID бота-клона
                                    if (CreateNewHero(cmd) == true) { //создаю нового героя
                                        out.println("REGISTERED");
                                    }
                                }
                            } else {
                                out.println("NAME_EXISTS");
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
            } catch (IOException e) {
                System.err.println("Соединение № "+ n +". Не получается разорвать связь с клиентом: " +HostName);
            } catch (SQLException e) {
                System.err.println("Не получается закрыть Statement, ошибка: " + e);
            }
        }
        System.out.println("Завершено соединение № " + n + ", клиент: " +HostName + ", " + players.getName() + " отключен");
        if (players != null) { players = null; }  //закрываю экземпляры открытых классов
    }

    //метод определение нужно ли исцеление богов
    private void Regen() throws IOException, SQLException {
        cmd = in.readLine();  //запись в переменную полученые данные

        System.out.println("Получил 17 "+cmd);

        if (cmd.equalsIgnoreCase("REGEN")) {
            SelectAllFromPlayers(); //выбираю все данные по герою
            if (players.getHp() < players.getMax_hp()){ //если хп меньше чем нужно
                int i_money = 0, i_G = 0;
                String s_money = "", s_G = "";
                double ch = ((double) players.getHp() / (double) players.getMax_hp()) * 100;

                System.out.println("Тяжесть ранения " + ch);

                if (ch < 33 && players.getLvl() < 5) { i_money = 75 * players.getLvl();}
                if (ch < 66 && players.getLvl() < 5) {i_money = 50 * players.getLvl();}
                if (ch < 99 && players.getLvl() < 5) { i_money = 25 * players.getLvl();}
                if (ch < 33 && players.getLvl() < 10) { i_G = ((int) players.getLvl() / 2);}
                if (ch < 66 && players.getLvl() < 10) { i_G = ((int) players.getLvl() / 4);}
                if (ch < 99 && players.getLvl() < 10) { i_money = 100 * players.getLvl();}
                if (i_money != 0) { s_money = i_money + " монет.";}
                if (i_G != 0) { s_G = i_G + " G.";}

                System.out.println("плата за исцеление " + i_money + i_G);

                if (players.getMoney() >= i_money && players.getGold() >= i_G){ ///если хватит денег на реген
                    out.println("REGEN");
                    cmd = in.readLine();  //запись в переменную полученые данные

                    System.out.println("Получил 18 "+cmd);

                    if (cmd.equalsIgnoreCase("PRICE")) {
                        out.println(s_money+s_G);
                    }
                    cmd = in.readLine();  //запись в переменную полученые данные

                    System.out.println("Получил 19 "+cmd);

                    if (cmd.equalsIgnoreCase("YES")) {
                        Pay(i_money, i_G);
                        out.println("GOOD");
                    }else{
                        out.println("GOOD");
                    }
                } else {
                    out.println("NO_REGEN");
                }
            }
        }
    }

    //метод снятия денег за исцеление и само исцеление
    private void Pay(int money, int G) throws SQLException {
        query = "UPDATE players SET money = money - " + money + ", gold = gold - " + G + " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
        statement.execute(query);  //выполняю запрос
        players.setHp(players.getMax_hp());
        players.setMana(players.getMax_mana());
        UpdateHpHero();
        UpdateManaHero();
    }

    //создаю нового героя
    private boolean CreateNewHero (String name) throws SQLException {
        boolean createNewHero = false;
        query = "INSERT INTO players (id_players, name, data_begin, lvl, exp, vip_lvl, vip_exp, " +
                "pvp_lvl, pvp_exp, str, dex, inst, def, hp, max_hp, mana, max_mana, money, gold, id_bots) " +
                "VALUES ('" + players.getId() + "', '" + name + "', CURRENT_DATE(), " +
                "1, 0, 0, 0, 1, 0, 10, 10, 10, 10, 40, 40, 0, 0, 0, 0, " + bots.getId() + ");"; //код SQL-запроса
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
                "isLive, exp, money_min, money_max, pvp_lvl) " +
                "VALUES ('" + name + "', 1, 10, 10, 10, 10, 40, 0, 1, 10, 25, 100, 1);"; //код SQL-запроса
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
        int kol = 1;
        while (res.next()) {
            kol = res.getInt("kol");
        }
        if (kol == 0) {  //если героев с таким именем не существует, проверяю список ботов
            query = "SELECT COUNT(*) AS kol FROM bots b WHERE b.name = '" + name + "';";
            res = statement.executeQuery(query); //запускаю запрос
            kol = 1;
            while (res.next()) {
                kol = res.getInt("kol");
            }
            if (kol == 0) { //если такое имя не применялось в БД
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
        int kol = 0;
        while (res.next()) {
            kol = res.getInt("kol");
        }
        if (kol == 1) {
            isPlayers = true;
            SelectAllFromPlayers(); //выбираю все данные по герою
        }
        return isPlayers;
    }

    //проверка на ПвП-апп
    private String UpPvP (int exp) throws SQLException {
        String title = "";
        if ((players.getPvp_exp() + exp) >= players.getMax_pvp_exp()) { //если набраный опыт больше мак. опыта по званию,
            //тогда увеличиваем все статы перса на upSt
            query = "UPDATE players SET pvp_lvl = pvp_lvl + 1, str = str + 2, dex = dex + 2, inst = inst + 2, def = def + 2," +
                    " max_hp = max_hp + 5, hp = max_hp, mana = mana + 2, money = money + " + players.getPvp_money() +
                    " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
            query = "UPDATE bots SET pvp_lvl = " + (players.getPvp_lvl() + 1) + ", str = " + (players.getStr() + 2) +
                    ", dex = " + (players.getDex() + 2) + ", inst = " + (players.getInst() + 2) +
                    ", def = " + (players.getDef() + 2) + ", hp = " + (players.getMax_hp() + 5) +
                    ", mana = " + (players.getMana() + 2) + " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
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
                    " max_hp = max_hp + 5, hp = max_hp, mana = mana + 2, money = money + " + players.getExp_money() +
                    " WHERE id_players = '" + players.getId() + "';"; //код SQL-запроса
            statement.addBatch(query); //запрос в очереди (обновляю данные персонажа)
            query = "UPDATE bots SET lvl = " + (players.getLvl() + 1) + ", str = " + (players.getStr() + 2) +
                    ", dex = " + (players.getDex() + 2) + ", inst = " + (players.getInst() + 2) +
                    ", def = " + (players.getDef() + 2) + ", hp = " + (players.getMax_hp() + 5) +
                    ", mana = " + (players.getMana() + 2) + " WHERE id_bots = " + players.getId_bots() + ";"; //код SQL-запроса (обновляю данные бота-клона персонажа)
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
            players.setGold(res.getInt("gold"));
            players.setExp(res.getInt("exp"));
            players.setPvp_exp(res.getInt("pvp_exp"));
            players.setMin_pvp_exp(res.getInt("min_pvp_exp"));
            players.setMax_pvp_exp(res.getInt("max_pvp_exp"));
            players.setMax_exp(res.getInt("max_exp"));
            players.setExp_money(res.getInt("exp_money"));
            players.setVip_money(res.getInt("vip_money"));
            players.setPvp_money(res.getInt("pvp_money"));
            players.setVip_hp(res.getInt("vip_hp"));
            players.setId_bots(res.getInt("id_bots"));
            players.setPvp_name(res.getString("pvp_name"));
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
            if (bots.getPvp_exp() > players.getPvp_exp()) { //но его храбрость больше
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

    //Храбрость при победе
    private int Courage() throws SQLException {
        int exp = 0;
        if (bots.getIsLive() == 1) {
            exp = (int) (pvp_exp * (AverageTwo() + players.getVip_lvl()));
            if ((players.getLvl() + 3) <= bots.getLvl()) {
                exp = exp * 2;
            }  //если уровень бота больше на 3 уровня и выше чем уровень перса
            if ((players.getLvl() - 2) == bots.getLvl()) {
                exp = (int) (exp / 2);
            }  //если уровень бота на 2 уровня меньше чем уровень перса
            if ((players.getLvl() - 3) == bots.getLvl()) {
                exp = (int) (exp / 3);
            } //если уровень бота на 3 уровня меньше чем уровень перса
            if ((players.getLvl() - 4) >= bots.getLvl()) {
                if (bots.getPvp_exp() > players.getPvp_exp()) { //но его храбрость больше
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
            exp = (int) (pvp_exp * AverageTwo());
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
    private void Drop() throws SQLException {
        if (bots.getLvl() >= players.getLvl() - 1) { //если уровень бота больше чем уровень перса минус один уровень
            int countDrop = 0;
            int setCount = 0;
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