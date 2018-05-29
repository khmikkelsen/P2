package node.database;

import rsa.InvalidRSAKeyException;
import rsa.RSAKey;
import rsa.Signature;
import blockchain.utils.HexUtil;
import blockchain.block.Block;
import blockchain.message.Message;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    // SQLite connection string

    private String url = "jdbc:sqlite:";

    public void setup() throws SQLException {
        // SQL statement for creating a new table
        String sql = "PRAGMA foreign_keys = ON;";

        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        }

        createBlockTable();
        createMessageTable();
    }


    public DatabaseConnection(String url) throws SQLException {
        this.url += url;
        setup();
    }

    private Connection connect() throws SQLException {
        // SQLite connection string
        Connection conn = DriverManager.getConnection(url);

        return conn;
    }

    public List<Block> getBlockTable() throws SQLException {

        String blockQuery = "SELECT block_id FROM blocks where block_id > 0";

        List<Long> blockIndices = new ArrayList<>();

        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(blockQuery);

            while (rs.next()) {
                blockIndices.add(rs.getLong("block_id") - 1);
            }
        }

        List<Block> blocks = new ArrayList<>();

        for (Long index : blockIndices) {
            blocks.add(getBlockByIndex(index));
        }

        return blocks;
    }

    public Block getBlockByIndex(long blockIndex) throws SQLException {
        List<Message> messages = getMessagesInBlock(blockIndex);

        String blockQuery = "SELECT block_id, hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp FROM blocks where block_id = ?";

        try (Connection conn = connect(); PreparedStatement statement = conn.prepareStatement(blockQuery)) {
            statement.setLong(1, blockIndex + 1);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
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

    public List<Message> getMessagesInBlock(long blockIndex) throws SQLException {
        String messageQuery = "SELECT recipient, sender, signature, message FROM messages where block_id = ?";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement statement = conn.prepareStatement(messageQuery)) {
            statement.setLong(1, blockIndex + 1);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                try {
                    RSAKey recipient = new RSAKey(rs.getString("recipient"));
                    RSAKey sender = new RSAKey(rs.getString("sender"));
                    Signature signature = new Signature(rs.getString("signature"));
                    String message = rs.getString("message");

                    Message m = new Message(message, sender, recipient, signature);

                    messages.add(m);
                } catch (IOException | InvalidRSAKeyException e) {
                    System.out.println(e.getMessage());
                }
            }

            return messages;
        }
    }

    /**
     * Adds a blockchain.block to the database and returns it's id.
     *
     * @param block The blockchain.block to add
     * @return Index of the added blockchain.block
     * @throws SQLException
     */
    public Long addBlock(Block block) throws SQLException {
        String query = "INSERT INTO blocks (hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp) VALUES(?,?,?,?,?,?)";

        try (Connection conn = connect(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, HexUtil.prependZeros(block.calculateHash(), 64));
            statement.setString(2, block.getPrevHeadHash());
            statement.setString(3, block.getMerkleRootHash());
            statement.setString(4, block.getCompactTarget());
            statement.setInt(5, block.getNonce());
            statement.setLong(6, block.getTimestamp());
            statement.executeUpdate();
        }

        // Block has now been inserted. Get it's id.
        query = "SELECT block_id FROM blocks ORDER BY block_id DESC LIMIT 1;";

        long blockIndex;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Get index = id - 1.
            if (rs.next()) {
                blockIndex = rs.getLong("block_id") - 1;
            } else {
                return null;
            }
        }

        addMessages(block.getMessages(), blockIndex);

        return blockIndex;
    }


    public long getBlockCount() throws SQLException {
        // Block has now been inserted. Get it's id.
        String query = "SELECT block_id FROM blocks ORDER BY block_id DESC LIMIT 1;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {

                long blockCount = rs.getLong("block_id");

                // Block count is equal to block_id since it is 1-based.
                return blockCount;
            }

            return 0;
        }
    }

    public Block getLatestBlock() throws SQLException {
        String blockQuery = "SELECT block_id, hash, previous_hash, merkle_root_hash, compact_difficulty, nonce, mined_timestamp FROM blocks ORDER BY block_id DESC LIMIT 1";

        String hash;
        String previousHash;
        String merkleRootHash;
        String compactTarget;
        int nonce;
        long minedTimestamp;
        long blockIndex;

        try (Connection conn = connect()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(blockQuery);

            if (rs.next()) {
                hash = rs.getString("hash");
                previousHash = rs.getString("previous_hash");
                merkleRootHash = rs.getString("merkle_root_hash");
                compactTarget = rs.getString("compact_difficulty");
                nonce = rs.getInt("nonce");
                minedTimestamp = rs.getLong("mined_timestamp");
                blockIndex = rs.getLong("block_id") - 1;
            } else {
                return null;
            }
        }

        List<Message> messages = getMessagesInBlock(blockIndex);

        return new Block(hash, previousHash, compactTarget, nonce, merkleRootHash, minedTimestamp, blockIndex, messages);
    }

    private void addMessages(List<Message> messages, long blockIndex) throws SQLException {
        String query = "INSERT INTO messages (recipient, sender, signature, message, block_id) VALUES(?,?,?,?,?)";

        for (Message m : messages) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, m.getRecipientPublicKey().getBase64String());
                pstmt.setString(2, m.getSenderPublicKey().getBase64String());
                pstmt.setString(3, m.getSignature().getBase64String());
                pstmt.setString(4, m.getMessage());
                pstmt.setLong(5, blockIndex + 1);
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
                + " FOREIGN KEY(block_id) REFERENCES blockchain.block(block_id)\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(query);
        }
    }
}