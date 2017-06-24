import java.sql.SQLException;

/**
 * Created by dusky on 4/29/17.
 */
public class Databaza extends Base {

    public static void run(){
        new Databaza().runQuery();
    }

    @Override
    void body() throws SQLException {
        deleteTable();
        createTable();
        insertRandomRows();
        chooseRows();
        updateRows();
        deleteRows();
        deleteTable();
    }
}
