class PriceVisitor implements Visitor{

    public PriceVisitor(){}

    public double visit(Items items){
        return items.getPrice();
    }

}
