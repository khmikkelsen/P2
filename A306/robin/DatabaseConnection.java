package robin;

import RSA.InvalidRSAKeyException;
import RSA.RSAKey;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    // SQLite connection string
    private static final String url = "jdbc:sqlite:C://sqlite/db/p2_blockchain.db";

    private Connection connect() throws SQLException {
        // SQLite connection string
        Connection conn = null;

        conn = DriverManager.getConnection(url);

        return conn;
    }

    public void setup() throws SQLException {
        // SQL statement for creating a new table
        String sql = "PRAGMA foreign_keys = ON;";

        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        }
    }

    public Block getBlockById(int blockId) throws SQLException {
        String messageQuery = "SELECT recipient, sender, signature, message from messages where block_id = ?";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = this.connect(); PreparedStatement statement = conn.prepareStatement(messageQuery)) {
            statement.setInt(1, blockId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                try {
                    RSAKey recipient = new RSAKey(rs.getString("recipient"));
                    RSAKey sender = new RSAKey(rs.getString("sender"));
                    String signature = rs.getString("signature");
                    String message = rs.getString("message");

                    Message m = new Message(message, sender, recipient, signature);

                    messages.add(m);
                } catch (IOException | InvalidRSAKeyException e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }

        String blockQuery = "SELECT block_id, hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp from blocks where block_id = ?";

        try (Connection conn = this.connect(); PreparedStatement statement = conn.prepareStatement(blockQuery)) {
            statement.setInt(1, blockId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int blockIndex = rs.getInt("block_id");
                String hash = rs.getString("hash");
                String previousHash = rs.getString("previous_hash");
                String merkleRootHash = rs.getString("merkle_root_hash");
                String compactTarget = rs.getString("compact_difficulty");
                int nonce = rs.getInt("nonce");
                long minedTimestamp = rs.getLong("mined_timestamp");

                Block block = new Block(hash, previousHash, compactTarget, nonce, merkleRootHash, minedTimestamp, blockIndex, messages);

                return block;
            }
        }

        return null;
    }

    /**
     * Adds a block to the database and returns it's id.
     *
     * @param block The block to add
     * @return Id of the added block
     * @throws SQLException
     */
    public int addBlock(Block block) throws SQLException {

        String query = "INSERT INTO blocks (hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp) VALUES(?,?,?,?,?,?)";

        try (Connection conn = this.connect(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, block.calculateHash());
            statement.setString(2, block.getPrevHeadHash());
            statement.setString(3, block.getMerkleRootHash());
            statement.setString(4, block.getCompactTarget());
            statement.setInt(5, block.getNonce());
            statement.setLong(6, block.getTimestamp());
            statement.executeUpdate();
        }

        // Block has now been inserted. Get it's id.
        query = "SELECT block_id FROM blocks ORDER BY block_id DESC LIMIT 1;";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            rs.next();
            return rs.getInt("block_id");
        }
    }

    public void addMessagesToBlockId(List<Message> messages, long blockId) throws SQLException {
        String query = "INSERT INTO messages (recipient, sender, signature, message, block_id) VALUES(?,?,?,?,?)";

        for (Message m : messages) {
            try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, m.getRecipient().toString());
                pstmt.setString(2, m.getSender().toString());
                pstmt.setString(3, m.getSignature());
                pstmt.setString(4, m.getMessage());
                pstmt.setLong(5, blockId);
                pstmt.executeUpdate();
            }
        }
    }

    public void createBlockTable() throws SQLException {
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
        }
    }

    public void createMessageTable() throws SQLException {
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
        }
    }
}
