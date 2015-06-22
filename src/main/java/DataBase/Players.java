package DataBase;

import java.sql.Date;

/**
 * Created by Azigar on 22.06.2015.
 */
public class Players {

    private int id_players;
    private String name;
    private Date data_begin;
    private int lvl;
    private int str;
    private int dex;
    private int inst;
    private int def;
    private int hp;
    private int max_hp;


    public int getId_players() {
        return id_players;
    }

    public void setId_players(int id_players) {
        this.id_players = id_players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getData_begin() {
        return data_begin;
    }

    public void setData_begin(Date data_begin) {
        this.data_begin = data_begin;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getInst() {
        return inst;
    }

    public void setInst(int inst) {
        this.inst = inst;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public void setMax_hp(int max_hp) {
        this.max_hp = max_hp;
    }
}
