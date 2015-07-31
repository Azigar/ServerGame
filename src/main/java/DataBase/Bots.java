package DataBase;

/**
 * Created by Azigar on 21.06.2015.
 */
public class Bots {

    private int id;
    private String name;
    private int lvl;
    private int str;
    private int dex;
    private int inst;
    private int def;
    private int hp;
    private int mana;
    private int isLive;
    private int exp;
    private int pvp_exp;
    private int money_min;
    private int money_max;
    private int sex;


    ///метод для коректного вывода на экран обьекта
    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "{"
            +  "id: " + id
            +  ", name: " + name
            +  ", lvl: " + lvl
            + "}";
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getLvl() {
        return lvl;
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

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMoney_min() {
        return money_min;
    }

    public void setMoney_min(int money_min) {
        this.money_min = money_min;
    }

    public int getMoney_max() {
        return money_max;
    }

    public void setMoney_max(int money_max) {
        this.money_max = money_max;
    }

    public int getIsLive() {
        return isLive;
    }

    public void setIsLive(int isLive) {
        this.isLive = isLive;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getPvp_exp() {
        return pvp_exp;
    }

    public void setPvp_exp(int pvp_exp) {
        this.pvp_exp = pvp_exp;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
