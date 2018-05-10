package robin;

import java.sql.*;

public class DatabaseConnection {

    // SQLite connection string
    private static final String url = "jdbc:sqlite:C://sqlite/db/p2_blockchain.db";

    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public void setup() {
        // SQL statement for creating a new table
        String sql = "PRAGMA foreign_keys = ON;";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Block getBlockByIndex(int index) {
        String query = "SELECT block_id, hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp from blocks where block_id = ?";

        try (Connection conn = this.connect(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, index);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int blockIndex = rs.getInt("block_id");
                String hash = rs.getString("hash");
                String previousHash = rs.getString("previous_hash");
                String merkleRootHash = rs.getString("merkle_root_hash");
                String compactDifficulty = rs.getString("compact_difficulty");
                int nonce = rs.getInt("nonce");
                Long minedTimestamp = rs.getLong("mined_timestamp");

                Block block = new Block(hash, previousHash, compactDifficulty, blockIndex, minedTimestamp, merkleRootHash, nonce);

                return block;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void addBlock(Block b) {

        String query = "INSERT INTO blocks (hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp) VALUES(?,?,?,?,?,?)";

        try (Connection conn = this.connect(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, b.calculateHash());
            statement.setString(2, b.getPrevHeadHash());
            statement.setString(3, b.getMerkleRootHash());
            statement.setString(4, b.getCompactDifficulty());
            statement.setInt(5, b.getNonce());
            statement.setLong(6, b.getTimestamp());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        query = "SELECT block_id FROM blocks ORDER BY block_id DESC LIMIT 1;";

        long newBlockId = -1;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            rs.next();
            newBlockId = rs.getInt("block_id");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//        query = "SELECT * FROM blocks ORDER BY id DESC LIMIT 1;";
//
//        long newBlockId = -1;
//
//        try (Connection conn = this.connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            rs.next();
//
//            System.out.println(rs.getLong("id") + "\t" +
//                    rs.getString("previous_hash") + "\t" +
//                    rs.getString("merkle_root_hash") + "\t" +
//                    rs.getString("compact_difficulty") + "\t" +
//                    rs.getInt("nonce") + "\t" +
//                    rs.getLong("mined_timestamp"));
//
//            newBlockId = rs.getInt("block_id");
//
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }

        if (newBlockId == -1) {
            return;
        }

        for (Message m : b.getMessages()) {
            query = "INSERT INTO messages (recipient, sender, signature, message, block_id) VALUES(?,?,?,?,?)";

            try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, m.getRecipient());
                pstmt.setString(2, m.getSender());
                pstmt.setString(3, m.getSignature());
                pstmt.setString(4, m.getMessage());
                pstmt.setLong(5, newBlockId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        query = "SELECT * from messages;";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // loop through the result set
            System.out.println("id\t\t\t\trecipient\t\t\t\t\t\tsender\t\t\t\t\t\t\tsignature\t\t\t\t\tmessage\t\t\t\t\tblock_id");
            while (rs.next()) {
                System.out.println(rs.getInt("message_id") + "\t\t\t\t" +
                        rs.getString("recipient") + "\t\t\t\t" +
                        rs.getString("sender") + "\t\t\t\t" +
                        rs.getString("signature") + "\t\t\t\t" +
                        rs.getString("message") + "\t\t\t\t" +
                        rs.getInt("block_id"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createBlockTable() {
        // SQL statement for creating a new table
        String query = "CREATE TABLE IF NOT EXISTS blocks (\n"
                + "	block_id integer PRIMARY KEY AUTOINCREMENT,\n"
                + " hash text NOT NULL,\n"
                + "	previous_hash text NOT NULL,\n"
                + " merkle_root_hash text NOT NULL,\n"
                + "	compact_difficulty text NOT NULL,\n"
                + "	nonce integer NOT NULL,\n"
                + " mined_timestamp integer NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createMessageTable() {
        // SQL statement for creating a new table
        String query = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	message_id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	recipient text NOT NULL,\n"
                + " sender text NOT NULL,\n"
                + " signature text NOT NULL,\n"
                + "	message text NOT NULL,\n"
                + "	block_id int NOT NULL,\n"
                + " FOREIGN KEY(block_id) REFERENCES block(block_id)\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
