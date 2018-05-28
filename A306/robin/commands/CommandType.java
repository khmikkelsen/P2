package robin.commands;

import com.google.gson.annotations.SerializedName;

public enum CommandType {
    @SerializedName("sendmessage") SEND_MESSAGE,
    @SerializedName("getblockcount") GET_BLOCK_COUNT,
    @SerializedName("getblock") GET_BLOCK,
    UNKNOWN
}
