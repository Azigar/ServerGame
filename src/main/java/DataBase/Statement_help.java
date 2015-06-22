package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Azigar on 21.06.2015.
 */
public class Statement_help {
    ///Константы для подключение к БД
    private static final String DB_NAME = "rpggame";
    private static final String URL = "jdbc:mysql://localhost:3306/"+DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "Anna20140129";

    private static java.sql.Statement statement = null;

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.jdbc.Driver"); //подключаю драйвер
        } catch (ClassNotFoundException e) {
            System.err.println("Не удалось загрузить Driver, ошибка № " + e);
        }

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS); //конект с БД
            statement = connection.createStatement();
            if (!connection.isClosed()) {
                System.out.println("Соединение с БД \"" + DB_NAME + "\" успешно установлено");
            }
        } catch (SQLException e) {
            System.err.println("Невозможно соединенится с БД \"" + DB_NAME + "\", ошибка № " + e);
        }

        //создание SQL-запроса
        try {
            //добавление записи в таблицу
            statement.execute("insert into players (name, data_begin, str, lvl, dex, inst, def, hp, max_hp)" +
                   "values('Азигар', CURRENT_DATE, 10, 1, 10, 10,10,40, 40);");
            System.out.println("SQL-запрос успешно обработан");

            /*
            //изменение записи
            int res = statement.executeUpdate("UPDATE players set name = 'Пингвин' WHERE id_players = 4;");
            System.out.println(String.valueOf(res)); // выводит к-во обработаных записей
            System.out.println("SQL-запрос успешно обработан, обработаных записей - "+res);
            */

            /*
            //выборка данных
            String id = "2"; //фильтр для запроса
            String query = "select * from user WHERE id = "+id; //код SQL-запроса
            ResultSet res = statement.executeQuery(query);  //запускаю запрос
            //в цикле прохожу всю таблицу и пишу данные в сеттеры
            while (res.next()){
                Bots user = new Bots(); //подключаю класс Bots
                user.setId(res.getInt(1));  //пишу в сеттер Bots результат с первого поля - 1
                user.setUsername(res.getString(2)); //пишу в сеттер Bots результат со второго поля - 2
                user.setPassword(res.getString("password")); //пишу в сеттер Bots результат со третьего поля - password (можно указывать номер поля или его название)

                System.out.println(user);

                int a = user.getId();
                String b = user.getUsername();
                String c = user.getPassword();

                System.out.println(a + " " + b + " " + c);
            }
            */

            /*
            //Пакет запросов
            statement.addBatch("insert into bots (name, str, lvl, dex, inst, def, hp)" +
                         "values('Колорадский Жук', 10, 1, 10, 10,10,40);");
            statement.addBatch("insert into bots (name, str, lvl, dex, inst, def, hp)" +
                    "values('Мышун', 10, 1, 10, 10,10,40);");
            statement.addBatch("insert into bots (name, str, lvl, dex, inst, def, hp)" +
                    "values('Крысак', 15, 2, 15, 15, 15, 50);");

            statement.executeBatch(); //выполнение всех запросов
            System.out.println("Пакет SQL-запросо успешно обработан");
            statement.clearBatch(); //очищает весь пакет запросов
            */



        } catch (SQLException e) {
            System.err.println("Невозможно обработать SQL-запрос, ошибка № " + e);
        }
    }
}
