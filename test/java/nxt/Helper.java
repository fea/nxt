package nxt;


import nxt.util.Listener;

import org.h2.tools.Shell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static String executeQuery(String line) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        out.println(line);
        try {
            Shell shell = new Shell();
            shell.setErr(out);
            shell.setOut(out);
            shell.runTool(Db.db.getConnection(), "-sql", line);
        } catch (SQLException e) {
            out.println(e.toString());
        }
        return new String(baos.toByteArray());
    }

    public static int getCount(String table) {
        try (Connection con = Db.db.getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("select count(*) as c from " + table)) {
            rs.next();
            return rs.getInt("c");
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public static class BlockListener implements Listener<Block> {
        @Override
        public void notify(Block block) {
            System.out.printf("Block Generated at height %d with %d transactions\n", block.getHeight(), block.getTransactions().size());
        }
    }
    
    public static class EasyMap {
        public static <K, V> Map<K, V> of(K k1, V v1) {
            HashMap<K, V> result = new HashMap<K, V>();
            result.put(k1, v1);
            return result;
        }
        
        public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
            HashMap<K, V> result = new HashMap<K, V>();
            result.put(k1, v1);
            result.put(k2, v2);
            return result;
        }
    }
}
