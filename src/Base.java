import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by dusky on 4/29/17.
 */
public abstract class Base {

    protected static Connection con = null;
    private static final String url = "jdbc:mysql://virtuallab.kar.elf.stuba.sk:3306/";
    private static final String db = "ain172";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "ain172";
    private static final String pass = "ain172";

    private Random rnd;
    private Set<Integer> selected;


    private static final int maxId = 10000;
    private static final int maxLastName = 1000;
    private static final int maxFirstName = maxLastName;
    private static final int maximumIdToChoose = 3000;

    private static final int numberOfRows = 100;

    protected static class Persons {
        protected static final String table = "TAB_matejka";

        protected static final String id = "id";
        protected static final String lastName = "LastName";
        protected static final String firstName = "FirstName";
    }

    protected Base(){
        rnd = new Random();
        selected = new HashSet<>();
    }

    protected void createConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName(driver).newInstance();
        con = DriverManager.getConnection(url+db, user, pass);
    }

    protected void closeConnection() throws SQLException {
        con.close();
    }

    protected void runQuery() {
        try{
            createConnection();

            body();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception close){
                close.printStackTrace();
            }
        }
    }

    abstract void body() throws SQLException;

    protected String createTableString() {
        return "CREATE TABLE IF NOT EXISTS " + Databaza.Persons.table + " (" +
                Databaza.Persons.id + " int," +
                Databaza.Persons.lastName + " varchar(255)," +
                Databaza.Persons.firstName + " varchar(255)" +
                ");";
    }

    protected void createTable() throws SQLException {
        String sql = createTableString();
        Statement st = con.createStatement();
        st.executeUpdate(sql);
    }

    protected void insertRandomRows() throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + Databaza.Persons.table + " VALUES ");

        for (int i = 0; i < numberOfRows; i++){
            int id = rnd.nextInt(maxId);
            String lastName = "last" + rnd.nextInt(maxLastName);
            String firstName = "first" + rnd.nextInt(maxFirstName);

            String row = String.format("(%s, '%s', '%s'),", id, lastName, firstName);
            sql.append(row);
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(";");
        System.out.println(sql);
        Statement st = con.createStatement();
        st.executeUpdate(sql.toString());
    }

    protected void chooseRows() throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s < %s;", Databaza.Persons.table, Databaza.Persons.id, maximumIdToChoose);
        Statement st = con.createStatement();
        ResultSet res = st.executeQuery(sql);

        while(res.next()){
            int id  = res.getInt(Databaza.Persons.id);
            String lastName = res.getString(Databaza.Persons.lastName);
            String firstName = res.getString(Databaza.Persons.firstName);

            System.out.println(String.format("ROW: Id=%s, FirstName=%s, LastName=%s", id, firstName, lastName));
            selected.add(id);
        }
    }

    protected void updateRows() throws SQLException {
        for (int id: selected){
            Statement stUpdate = con.createStatement();
            stUpdate.executeUpdate(String.format("UPDATE %s SET %s='%s' WHERE %s=%s ;", Databaza.Persons.table, Databaza.Persons.firstName, "NOVA HODNOTA", Databaza.Persons.id, id));
            System.out.println("UPDATED ID " + id);
        }
    }

    protected void deleteRows() throws SQLException {
        StringBuilder rows = new StringBuilder();
        for (int id : selected) {
            rows.append(id + ",");
        }
        rows.deleteCharAt(rows.length() - 1);

        String deleting = String.format("DELETE FROM %s WHERE %s NOT IN (%s);", Databaza.Persons.table, Databaza.Persons.id, rows);
        Statement st = con.createStatement();
        int pocetZvysnych = st.executeUpdate(deleting);
        System.out.println("Remaining: " + (numberOfRows - pocetZvysnych));
    }



    protected void deleteTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + Databaza.Persons.table;
        Statement st = con.createStatement();
        st.executeUpdate(sql);
    }
}
