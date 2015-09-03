import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by Madalin.Colezea on 9/1/2015.
 */
public class TestJDBC {
    public static void main(String[] args) {
//        testBasic();
//        testStoredProcedureCall();
//        testTransaction();
//        testDatabaseMetadata();
//        testResultSetMetaData();
        testPropertiesFile();
    }

    static void testPropertiesFile() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("demo.properties"));

        } catch (Exception e){
            e.printStackTrace();
        }
        String url = properties.getProperty("dburl");
        String user = properties.getProperty("user");
        String pass = properties.getProperty("pass");

        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected");
        } catch (Exception e){
          e.printStackTrace();
        }

    }

    static void testResultSetMetaData(){
        Connection connection;
        Statement statement;
        ResultSet resultSet;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select id, last_name, first_name, salary from employees");
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            int count = resultSetMetaData.getColumnCount();
            System.out.println("Number " + count);
//        one based
            for (int i = 1; i <= count; i++){
                System.out.print(resultSetMetaData.getColumnName(i) + " ");
                System.out.print(resultSetMetaData.getColumnTypeName(i) + " ");
                System.out.print(resultSetMetaData.isNullable(i) + " ");
                System.out.println(resultSetMetaData.isAutoIncrement(i) +" ");
            }
        } catch (Exception e){

        }
    }

    static void testDatabaseMetadata(){
        Connection connection;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            System.out.println(databaseMetaData.getDatabaseProductName());
            System.out.println(databaseMetaData.getDatabaseProductVersion());
            System.out.println(databaseMetaData.getDriverName());
            System.out.println(databaseMetaData.getDriverVersion());

//            get tables
            resultSet = databaseMetaData.getTables(null, null, null, null);
            while (resultSet.next()){
                System.out.println(resultSet.getString("TABLE_NAME"));
            }

//            get columns
            resultSet = databaseMetaData.getColumns(null, null, "employees", null);
            while (resultSet.next()){
                System.out.println(resultSet.getString("COLUMN_NAME") + " " + resultSet.getString("TYPE_NAME"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    static void testTransaction(){
        Connection connection;
        Statement statement;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("delete from employees where department = 'HR'");
            if (true){
                connection.rollback();
            } else {
                connection.commit();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    static void testStoredProcedureCall(){
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
//            in parameter
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            callableStatement = connection.prepareCall("{call increase_salaries_for_department(?, ?)}");
            callableStatement.setString(1, "Engineering");
            callableStatement.setDouble(2, 100000);
            callableStatement.execute();

//            inout parameter
            callableStatement = connection.prepareCall("{call greet_the_department(?)}");
//            register inout parameter
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setString(1, "Engineering");
            callableStatement.execute();
//            get parameter
            String result = callableStatement.getString(1);
            System.out.println(result);

//            out parameter
            callableStatement = connection.prepareCall("{call get_count_for_department(?, ?)}");
            callableStatement.setString(1, "Engineering");
            callableStatement.registerOutParameter(2, Types.INTEGER);
            callableStatement.execute();
            Integer nr = callableStatement.getInt(2);
            System.out.println(nr);

//          result set
            callableStatement = connection.prepareCall("{call get_employees_for_department(?)}");
            callableStatement.setString(1, "Engineering");
            callableStatement.execute();
            ResultSet rs = callableStatement.getResultSet();
            while (rs.next()){
                System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                callableStatement.close();
                connection.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static void testBasic(){
        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {
//            for old driver version
//            Class.forName("org.gjt.mm.mysql.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            System.out.println("Connection successfully");
            statement = connection.createStatement();
//            executeUpdate can be user to execute update delete or insert
//            execute insert
            int rowsAffected;
            rowsAffected = statement.executeUpdate("insert into employees (last_name, first_name, email, department, salary) VALUES ('A', 'B', 'mail@yahoo', 'HR', 100)");
            System.out.println("Rows affected: " + rowsAffected);
//            execute update
            rowsAffected = statement.executeUpdate("update employees set email = 'mail.mail' where last_name = 'A'");
            System.out.println("Rows affected: " + rowsAffected);
//            execute delete
            rowsAffected = statement.executeUpdate("delete from employees where last_name = 'A'");
            System.out.println("Rows affected: " + rowsAffected);
//            execute select
            resultSet = statement.executeQuery("select * from employees");
            while (resultSet.next()){
                System.out.print(resultSet.getString("first_name") + " ");
                System.out.println(resultSet.getString("last_name"));
            }

//            prepared statement
//            faster and avoids SQL injection
            PreparedStatement preparedStatement = connection.prepareStatement("select * from employees where salary > ? and department = ?");
//            set parameters
            preparedStatement.setDouble(1, 80000);
            preparedStatement.setString(2, "Legal");
            resultSet = preparedStatement.executeQuery();
            System.out.println("Big salary from legal: ");
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
