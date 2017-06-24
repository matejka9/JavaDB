import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dusky on 4/29/17.
 */
public class PokrocilaDatabaza extends Databaza {

    private static class Procedure {
        private static String name = "DUSAN_PROCEDURE";
    }

    public static void run(){
        new PokrocilaDatabaza().runQuery();
    }

    @Override
    void body() throws SQLException {
        dropProcedure();
        createProcedure();
        callProcedure();
        dropProcedure();
        deleteTable();


        doTransaction();
    }

    private void createProcedure() throws SQLException {
        String sql =
                "CREATE PROCEDURE " + Procedure.name + "() " +
                        "BEGIN " +
                            createTableString() +
                        "END";

        Statement st = con.createStatement();
        st.execute(sql);
    }

    private void dropProcedure() throws SQLException {
        String sql = "DROP PROCEDURE IF EXISTS " + Procedure.name + ";";
        Statement st = con.createStatement();
        st.execute(sql);
    }

    private void callProcedure() throws SQLException {
        String sql = "CALL " + Procedure.name + "()";
        Statement st = con.createStatement();
        st.execute(sql);
    }

    private void doTransaction() throws SQLException {
        startTransaction();
        super.body();
        endTransaction();
    }

    private void startTransaction() throws SQLException {
        con.setAutoCommit(false);
    }

    private void endTransaction() throws SQLException {
        con.commit();
        con.setAutoCommit(true);
    }


}
