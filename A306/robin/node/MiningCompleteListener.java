package robin.node;

import robin.Block;
import robin.DatabaseConnection;

import java.sql.SQLException;

public interface MiningCompleteListener {
    void onMiningComplete(final Block block);
}
