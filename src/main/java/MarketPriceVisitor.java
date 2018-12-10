class MarketPriceVisitor implements Visitor{

    public MarketPriceVisitor(){}

    public double visit(Items items){
        return items.getMarketPrice();
    }
}
