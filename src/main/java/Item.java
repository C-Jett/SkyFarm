public class Item extends Items {

    public Item(String name,
                 Double price,
                 Double marketPrice,
                 Integer itemCount,
                 Integer xCoord,
                 Integer yCoord,
                 Integer lengthSpan,
                 Integer widthSpan,
                 javafx.scene.paint.Color color){
        super(name,price,marketPrice,itemCount,xCoord,yCoord,lengthSpan,widthSpan,color);
    }

    @Override
    protected boolean isItemContainer() {
        return false;
    }

    public double accept(Visitor visitor) { return visitor.visit(this); }

}
