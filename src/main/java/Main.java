import DataBase.Bots;
import DataBase.ConnectBD;
import DataBase.Players;
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

    Fight fight;

    //отдельный поток
    public ServeOneThread(Socket s, String Host_Name, long j, ConnectBD connBD) throws IOException, SQLException {

        this.n = j; //эта переменная получает по параметру № клиента, который подключился
        this.HostName = Host_Name;  //эта переменная получает по параметру имя клиента, который подключился
        this.socket = s; //эта переменная получает по параметру управление подключением через новый поток

        this.connectBD = connBD;
        this.statement = (Statement) connectBD.getConnection().createStatement();  //подключаю Statement
        this.bots = new Bots();
        this.players = new Players();

        this.fight = new Fight();

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //в эту переменную будут поступать данные
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); //эта переменная на вывод данных клиенту
        start();
    }

    public void run() {
        try {
            while (true) {
                //ожидаю команду от клиента
                cmd = in.readLine();  //запись в переменную полученые данные

                //закрываю поток
                if (cmd.equalsIgnoreCase("END")) {
                    out.println("END");
                    break; //выход с потока
                }

                //клиент открыл активити боя
                if (cmd.equalsIgnoreCase("FIGHT_ACTIVITY")){

                    //выбираю все данные о игроке
                    out.println("ID_PLAEYR"); //запрашиваю ID игрока
                    players.setId(Integer.parseInt(in.readLine())); //получаю ID игрока
                    query = "select * from players WHERE id_players = "+players.getId(); //код SQL-запроса
                    try {
                        res = statement.executeQuery(query); //запускаю запрос
                        //в цикле прохожу всю таблицу и пишу данные в сеттеры
                        while (res.next()) {
                            players.setName(res.getString("name"));
                            players.setData_begin(res.getDate("data_begin"));
                            players.setLvl(res.getInt("lvl"));
                            players.setStr(res.getInt("str"));
                            players.setDex(res.getInt("dex"));
                            players.setInst(res.getInt("inst"));
                            players.setDef(res.getInt("def"));
                            players.setHp(res.getInt("hp"));
                            players.setMax_hp(res.getInt("max_hp"));
                        }
                    } catch (SQLException e) {
                        System.err.println("Невозможно обработать SQL-запрос, ошибка № " + e);
                        break;   //выход с потока
                    }

                    //выбираю все данные о противнике
                    out.println("ID_ENEMY"); //запрашиваю ID противника
                    bots.setId(Integer.parseInt(in.readLine())); //получаю ID противника
                    query = "select * from bots WHERE id_bots = "+bots.getId(); //код SQL-запроса
                    try {
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
                            bots.setLive(res.getInt("live"));
                        }
                    } catch (SQLException e) {
                        System.err.println("Невозможно обработать SQL-запрос, ошибка № " + e);
                        break;   //выход с потока
                    }

                    //передаю управление вопросами клиенту
                    out.println("HP_HERO");
                    cmd = in.readLine();  //запись в переменную полученые данные

                    //отправка к-во ОЗ игрока
                    if (cmd.equalsIgnoreCase("HP_HERO")) {
                        out.println(players.getHp());
                    }

                    //отправка к-во ОЗ противника
                    cmd = in.readLine();  //запись в переменную полученые данные
                    if (cmd.equalsIgnoreCase("HP_ENEMY")){
                        out.println(bots.getHp());
                    }

                    //определяю кто первый ходит, сравнивая интуицию опонентов
                    cmd = in.readLine();  //запись в переменную полученые данные
                    if (cmd.equalsIgnoreCase("WHO_FIRST")){
                        boolean firstAtk = fight.Shans(players.getInst(), bots.getInst());
                        if (firstAtk){
                            out.println("FIRST_PLAYER"); //запрашиваю ID противника
                        }else{
                            out.println("FIRST_ENEMY"); //запрашиваю ID противника
                        }
                    }





                    //out.println("ID_ENEMY"); //отправляю ОЗ игрока
                    //_idEnemy = in.readLine();  //запись в переменную полученые данные о ID моба
                    //System.out.println(_idEnemy);
                    //ищу хар-ки игрока и моба
                    //out.println("HP_HERO"); //отправляю ОЗ игрока
                }

                if (cmd.equalsIgnoreCase("HP_HERO")){
                    //out.println(player.hp); //отправляю ОЗ игрока
                }

                if (cmd.equalsIgnoreCase("HP_ENEMY")){
                   // out.println(bot.hp); //отправляю ОЗ моба
                }
            }
        } catch (IOException e) {
            System.err.println("Соединение № "+ n +". Ошибка при передачи клиенту: " +HostName);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Соединение № "+ n +". Не получается разорвать связь с клиентом: " +HostName);
            }
        }
        System.out.println("Завершено соединение № "+ n +", клиент: " +HostName+" отключен");

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