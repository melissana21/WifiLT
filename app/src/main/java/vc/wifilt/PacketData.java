package vc.wifilt;

import java.io.Serializable;

/**
 * Created by hansamuE on 2016/1/7.
 */
public class PacketData implements Serializable {
    private String ori;
    private String des;
    private String type;
    private Serializable data;
    private int position;

    public PacketData(String type, Serializable data) {
        this.type = type;
        this.data = data;
    }

    public void setOri(String ori) {
        this.ori = ori;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public Serializable getData() {
        return data;
    }

    public String getOri() {
        return ori;
    }

    public String getDes() {
        return des;
    }

    public int getPosition() {
        return position;
    }
}