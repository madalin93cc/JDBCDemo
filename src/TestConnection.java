import java.sql.*;

/**
 * Created by Madalin.Colezea on 9/1/2015.
 */
public class TestConnection {
    public static void main(String[] args) {
        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {
//            for old driver version
//            Class.forName("org.gjt.mm.mysql.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            System.out.println("Connection successfully");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from employees");
            while (resultSet.next()){
                System.out.print(resultSet.getString("first_name") + " ");
                System.out.println(resultSet.getString("last_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        } catch (ClassNotFoundException e){
//            e.printStackTrace();
//        }
    }
}
