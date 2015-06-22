package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Azigar on 21.06.2015.
 */
public class Statement_help {
    ///��������� ��� ����������� � ��
    private static final String DB_NAME = "rpggame";
    private static final String URL = "jdbc:mysql://localhost:3306/"+DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "Anna20140129";

    private static java.sql.Statement statement = null;

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.jdbc.Driver"); //��������� �������
        } catch (ClassNotFoundException e) {
            System.err.println("�� ������� ��������� Driver, ������ � " + e);
        }

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS); //������ � ��
            statement = connection.createStatement();
            if (!connection.isClosed()) {
                System.out.println("���������� � �� \"" + DB_NAME + "\" ������� �����������");
            }
        } catch (SQLException e) {
            System.err.println("���������� ������������ � �� \"" + DB_NAME + "\", ������ � " + e);
        }

        //�������� SQL-�������
        try {
            //���������� ������ � �������
            statement.execute("insert into players (name, data_begin, str, lvl, dex, inst, def, hp, max_hp)" +
                   "values('������', CURRENT_DATE, 10, 1, 10, 10,10,40, 40);");
            System.out.println("SQL-������ ������� ���������");

            /*
            //��������� ������
            int res = statement.executeUpdate("UPDATE players set name = '�������' WHERE id_players = 4;");
            System.out.println(String.valueOf(res)); // ������� �-�� ����������� �������
            System.out.println("SQL-������ ������� ���������, ����������� ������� - "+res);
            */

            /*
            //������� ������
            String id = "2"; //������ ��� �������
            String query = "select * from user WHERE id = "+id; //��� SQL-�������
            ResultSet res = statement.executeQuery(query);  //�������� ������
            //� ����� ������� ��� ������� � ���� ������ � �������
            while (res.next()){
                Bots user = new Bots(); //��������� ����� Bots
                user.setId(res.getInt(1));  //���� � ������ Bots ��������� � ������� ���� - 1
                user.setUsername(res.getString(2)); //���� � ������ Bots ��������� �� ������� ���� - 2
                user.setPassword(res.getString("password")); //���� � ������ Bots ��������� �� �������� ���� - password (����� ��������� ����� ���� ��� ��� ��������)

                System.out.println(user);

                int a = user.getId();
                String b = user.getUsername();
                String c = user.getPassword();

                System.out.println(a + " " + b + " " + c);
            }
            */

            /*
            //����� ��������
            statement.addBatch("insert into bots (name, str, lvl, dex, inst, def, hp)" +
                         "values('����������� ���', 10, 1, 10, 10,10,40);");
            statement.addBatch("insert into bots (name, str, lvl, dex, inst, def, hp)" +
                    "values('�����', 10, 1, 10, 10,10,40);");
            statement.addBatch("insert into bots (name, str, lvl, dex, inst, def, hp)" +
                    "values('������', 15, 2, 15, 15, 15, 50);");

            statement.executeBatch(); //���������� ���� ��������
            System.out.println("����� SQL-������� ������� ���������");
            statement.clearBatch(); //������� ���� ����� ��������
            */



        } catch (SQLException e) {
            System.err.println("���������� ���������� SQL-������, ������ � " + e);
        }
    }
}
