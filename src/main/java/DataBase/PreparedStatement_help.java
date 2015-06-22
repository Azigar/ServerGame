package DataBase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Calendar;

/**
 * Created by Azigar on 20.06.2015.
 */
public class PreparedStatement_help {

    private static final String INSERT_NEW = "INSERT INTO dsh VALUES (?,?,?,?,?,?,?)";
    private static final String GET_ALL = "SELECT * FROM dsh";
    private static final String DELETE = "DELETE FROM dsh WHERE id = ?";

    public static void main(String[] args) {

        PreparedStatement preparedStatement = null;

        ConnectBD conn = new ConnectBD();

        /*
        //ввод данных в таблицу
        try {
            preparedStatement = conn.getConnection().prepareStatement(INSERT_NEW);

            //параметры ввода
            preparedStatement.setInt(1, 2);
            preparedStatement.setString(2, "Титле");
            preparedStatement.setString(3, "Дескриптион");
            preparedStatement.setFloat(4, 0.2f);
            preparedStatement.setBoolean(5, true);
            preparedStatement.setDate(6, new Date(Calendar.getInstance().getTimeInMillis()));
            preparedStatement.setBlob(7, new FileInputStream("smiley.png"));

            preparedStatement.execute(); //запуск запроса

            System.out.println("Вставка данных успешно завершена");
        } catch (SQLException e) {
            System.out.println("Ошибка при вводе данных в таблицу, "+e);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при загрузки иконки, " + e);
        }
        */

        /*
        //вывод данных
        try {
            preparedStatement = conn.getConnection().prepareStatement(GET_ALL);

            ResultSet res = preparedStatement.executeQuery();

            while (res.next()){
                int id = res.getInt(1);  //пишу в сеттер Bots результат с первого поля - 1
                String title = res.getString(2); //пишу в сеттер Bots результат со второго поля - 2
                String description = res.getString(3);
                float rating = res.getFloat("rating");
                boolean published = res.getBoolean("published");
                Date created = res.getDate("created");
                byte [] icon = res.getBytes("icon");

                System.out.println(id + " " + title + " " + description + " " + rating + " " + published + " " + created + " " + icon);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при обробке запроса, "+e);
        }
        */

        //удаление данных
        try {
            preparedStatement = conn.getConnection().prepareStatement(DELETE);

            preparedStatement.setInt(1, 2); //передаем параметер
            preparedStatement.executeUpdate();

            System.out.println("Данные успешно удалены");
        } catch (SQLException e) {
            System.out.println("Ошибка при обробке запроса, "+e);
        }
    }
}
