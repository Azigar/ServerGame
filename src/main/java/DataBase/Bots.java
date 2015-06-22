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
    private int live;

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

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }
}
