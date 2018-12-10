import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Items implements Serializable,Visitable,Observer {
    private String name;
    private double price;
    private double marketPrice;
    private int itemCount;
    private int xCoord;
    private int yCoord;
    private int lengthSpan;
    private int widthSpan;
    //For color
    private double red;
    private double green;
    private double blue;

    private HashMap<String, Items> children = new HashMap<>();

    public Items(String name,
                 Double price,
                 Double marketPrice,
                 Integer itemCount,
                 Integer xCoord,
                 Integer yCoord,
                 Integer lengthSpan,
                 Integer widthSpan,
                 Color color)
    {
        this.name = name;
        this.price = price;
        this.marketPrice = marketPrice;
        this.itemCount = itemCount;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.lengthSpan = lengthSpan;
        this.widthSpan = widthSpan;
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
    }

    protected abstract boolean isItemContainer();

    public HashMap<String,Items> getChildren(){
        if(!isItemContainer()) return null;
        return children;
    }

    public void getChildrenAsString(){
        if(isItemContainer()) {
            for (Map.Entry<String, Items> entry : children.entrySet()) {
                System.out.println("Child: " + entry.getKey());
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public int getItemCount() { return itemCount; }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public int getLengthSpan() {
        return lengthSpan;
    }

    public void setLengthSpan(int lengthSpan) {
        this.lengthSpan = lengthSpan;
    }

    public int getWidthSpan() {
        return widthSpan;
    }

    public void setWidthSpan(int widthSpan) {
        this.widthSpan = widthSpan;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public Color getColor(){
        return Color.color(red,green,blue);
    }

    public String getColorAsString(){
        return "[red=" + red + ", green=" + green + ", blue=" + blue + "]";
    }

    public void setChildren(String[] names) {
        if(isItemContainer()){
            for(String name : names){
                children.put(name, Main.itemMap.get(name));
            }
        }
    }

    public void removeChild(String name){
        children.remove(name);
    }

    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public void update(ConcreteSubject s) { itemCount = s.getCount(); }

}

