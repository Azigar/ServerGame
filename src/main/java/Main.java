import DataBase.Bots;
import DataBase.ConnectBD;
import DataBase.Players;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


class ServeOneThread extends Thread {

    long n;
    String HostName;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String cmd; //переменная для распознавания команд от клиента
    String _idEnemy;

    //классы для таблиц и работы с БД
    ConnectBD connectBD;
    Bots bots;
    Players players;

    //отдельный поток
    public ServeOneThread(Socket s, String Host_Name, long j, ConnectBD connBD) throws IOException {

        this.n = j; //эта переменная получает по параметру № клиента, который подключился
        this.HostName = Host_Name;  //эта переменная получает по параметру имя клиента, который подключился
        this.socket = s; //эта переменная получает по параметру управление подключением через новый поток

        this.connectBD = connBD;
        this.bots = new Bots();
        this.players = new Players();

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //в эту переменную будут поступать данные
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); //эта переменная на вывод данных клиенту
        start();
    }

    public void run() {
        try {
            while (true) {
                //ожидаю данные
                cmd = in.readLine();  //запись в переменную полученые данные

                //закрываю поток
                if (cmd.equalsIgnoreCase("END")) {
                    out.println("END");
                    break;
                }

                //если хотим дратся, тогда обробляю и вот что вывожу
                if (cmd.equalsIgnoreCase("FIGHT")){
                    out.println("ID_ENEMY"); //отправляю ОЗ игрока
                    _idEnemy = in.readLine();  //запись в переменную полученые данные о ID моба
                    System.out.println(_idEnemy);
                    //ищу хар-ки игрока и моба
                    out.println("HP_HERO"); //отправляю ОЗ игрока
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
                    System.out.println("Соединение № "+ n +" с клиентом: "+HostName+" разорвано");
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