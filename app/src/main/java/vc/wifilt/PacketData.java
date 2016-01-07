package vc.wifilt;

import java.io.Serializable;

/**
 * Created by hansamuE on 2016/1/7.
 */
public class PacketData implements Serializable {
    private String type;
    private Serializable data;

    public PacketData(String type, Serializable data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Serializable getData() {
        return data;
    }
}