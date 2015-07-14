package DataBase;

import java.sql.Date;

/**
 * Created by Azigar on 22.06.2015.
 */
public class Players {

    private String id;
    private String name;
    private int lvl;
    private int vip_lvl;
    private int str;
    private int dex;
    private int inst;
    private int def;
    private int hp;
    private int max_hp;
    private int mana;
    private int max_mana;
    private int size_inventory;
    private int money;
    private int gold;
    private int pvp_exp;
    private int pvp_lvl;
    private int min_pvp_exp;
    private int max_pvp_exp;
    private int exp;
    private int max_exp;
    private int exp_money;
    private int vip_money;
    private int pvp_money;
    private int vip_hp;
    private int id_bots;
    private String pvp_name;



    //геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getSize_inventory() {
        return size_inventory;
    }

    public void setSize_inventory(int size_inventory) {
        this.size_inventory = size_inventory;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPvp_exp() {
        return pvp_exp;
    }

    public void setPvp_exp(int pvp_exp) {
        this.pvp_exp = pvp_exp;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getPvp_lvl() {
        return pvp_lvl;
    }

    public void setPvp_lvl(int pvp_lvl) {
        this.pvp_lvl = pvp_lvl;
    }

    public int getMin_pvp_exp() {
        return min_pvp_exp;
    }

    public void setMin_pvp_exp(int min_pvp_exp) {
        this.min_pvp_exp = min_pvp_exp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMax_exp() {
        return max_exp;
    }

    public void setMax_exp(int max_exp) {
        this.max_exp = max_exp;
    }

    public int getExp_money() {
        return exp_money;
    }

    public void setExp_money(int exp_money) {
        this.exp_money = exp_money;
    }

    public int getVip_money() {
        return vip_money;
    }

    public void setVip_money(int vip_money) {
        this.vip_money = vip_money;
    }

    public int getPvp_money() {
        return pvp_money;
    }

    public void setPvp_money(int pvp_money) {
        this.pvp_money = pvp_money;
    }

    public int getMax_pvp_exp() {
        return max_pvp_exp;
    }

    public void setMax_pvp_exp(int max_pvp_exp) {
        this.max_pvp_exp = max_pvp_exp;
    }

    public int getVip_hp() {
        return vip_hp;
    }

    public void setVip_hp(int vip_hp) {
        this.vip_hp = vip_hp;
    }

    public int getVip_lvl() {
        return vip_lvl;
    }

    public void setVip_lvl(int vip_lvl) {
        this.vip_lvl = vip_lvl;
    }

    public int getId_bots() {
        return id_bots;
    }

    public void setId_bots(int id_bots) {
        this.id_bots = id_bots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPvp_name() {
        return pvp_name;
    }

    public void setPvp_name(String pvp_name) {
        this.pvp_name = pvp_name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getMax_mana() {
        return max_mana;
    }

    public void setMax_mana(int max_mana) {
        this.max_mana = max_mana;
    }
}
