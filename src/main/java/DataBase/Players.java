package DataBase;

import java.util.Date;

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
    private double gold;
    private int pvp_exp;
    private int pvp_lvl;
    private int min_pvp_exp;
    private int max_pvp_exp;
    private int exp;
    private int max_exp;
    private int exp_money;
    private int vip_money;
    private int pvp_money;
    private int id_bots;
    private String pvp_name;
    private Date lastDate;
    private Date dDateBirth;
    private String sDateBirth;
    private int sex;
    private String avatar;
    private int days;
    private int maxMyUron;
    private int maxEnemyUron;
    private int pve_v;
    private int pve_l;
    private int pvp_v;
    private int pvp_l;
    private int holiday;
    private int birthday;
    private String pass;
    private int isPass;



    //геттеры и сеттеры
    public String getName() {
        return name;
    }

    public int getIsPass() {
        return isPass;
    }

    public void setIsPass(int isPass) {
        this.isPass = isPass;
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

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public int getMax_mana() {
        return max_mana;
    }

    public void setMax_mana(int max_mana) {
        this.max_mana = max_mana;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getMaxMyUron() {
        return maxMyUron;
    }

    public void setMaxMyUron(int maxMyUron) {
        this.maxMyUron = maxMyUron;
    }

    public int getMaxEnemyUron() {
        return maxEnemyUron;
    }

    public void setMaxEnemyUron(int maxEnemyUron) {
        this.maxEnemyUron = maxEnemyUron;
    }

    public int getPve_v() {
        return pve_v;
    }

    public void setPve_v(int pve_v) {
        this.pve_v = pve_v;
    }

    public int getPve_l() {
        return pve_l;
    }

    public void setPve_l(int pve_l) {
        this.pve_l = pve_l;
    }

    public int getPvp_v() {
        return pvp_v;
    }

    public void setPvp_v(int pvp_v) {
        this.pvp_v = pvp_v;
    }

    public int getPvp_l() {
        return pvp_l;
    }

    public void setPvp_l(int pvp_l) {
        this.pvp_l = pvp_l;
    }

    public String getsDateBirth() {
        return sDateBirth;
    }

    public void setsDateBirth(String sDateBirth) {
        this.sDateBirth = sDateBirth;
    }

    public Date getdDateBirth() {
        return dDateBirth;
    }

    public void setdDateBirth(Date dDateBirth) {
        this.dDateBirth = dDateBirth;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getHoliday() {
        return holiday;
    }

    public void setHoliday(int holiday) {
        this.holiday = holiday;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
