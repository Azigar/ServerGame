package DataBase;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Azigar on 21.06.2015.
 */
public class ConnectBD {

    ///Константы для подключение к БД
    private final String DB_NAME = "rpggame";
    private final String URL = "jdbc:mysql://localhost:3306/"+DB_NAME;
    private final String USER = "root";
    private final String PASS = "Anna20140129";

    Connection connection;

    public ConnectBD() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); //подключаю драйвер
        } catch (ClassNotFoundException e) {
            System.err.println("Не удалось загрузить Driver, ошибка № " + e);
        }

        try {
            connection = DriverManager.getConnection(URL, USER, PASS); //конект с БД
            if (!connection.isClosed()) {
                System.out.println("Соединение с БД \"" + DB_NAME + "\" успешно установлено");
            }
        } catch (SQLException e) {
            System.err.println("Невозможно соединенится с БД \"" + DB_NAME + "\", ошибка № " + e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
