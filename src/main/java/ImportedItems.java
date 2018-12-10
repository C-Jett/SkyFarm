import javafx.scene.paint.Color;

public class ImportedItems {

        public static class Builder {
            private String type;
            private String name;
            private Double price;
            private Double marketPrice;
            private Integer itemCount;
            private Integer xCoord;
            private Integer yCoord;
            private Integer lengthSpan;
            private Integer widthSpan;
            private Color color;
            private String[] children;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Double getPrice() {
                return price;
            }

            public void setPrice(Double price) {
                this.price = price;
            }

            public Double getMarketPrice() {
                return marketPrice;
            }

            public void setMarketPrice(Double marketPrice) {
                this.marketPrice = marketPrice;
            }

            public Integer getItemCount() {
                return itemCount;
            }

            public void setItemCount(Integer itemCount) {
                this.itemCount = itemCount;
            }

            public Integer getxCoord() {
                return xCoord;
            }

            public void setxCoord(Integer xCoord) {
                this.xCoord = xCoord;
            }

            public Integer getyCoord() {
                return yCoord;
            }

            public void setyCoord(Integer yCoord) {
                this.yCoord = yCoord;
            }

            public Integer getLengthSpan() {
                return lengthSpan;
            }

            public void setLengthSpan(Integer lengthSpan) {
                this.lengthSpan = lengthSpan;
            }

            public Integer getWidthSpan() {
                return widthSpan;
            }

            public void setWidthSpan(Integer widthSpan) {
                this.widthSpan = widthSpan;
            }

            public Color getColor() {
                return color;
            }

            public void setColor(Color color) {
                this.color = color;
            }

            public String[] getChildren(){return children;}

            public void setChildren(String[] children) {this.children = children;}

            public Builder(String name){
                this.name = name;
            }

            public Builder withType(String Type){
                this.type = Type;
                return this;
            }


            public Builder withPrice(Double price){
                this.price = price;
                return this;
            }

            public Builder withMarketPrice(Double marketPrice){
                this.marketPrice = marketPrice;
                return this;
            }

            public Builder hasItemCount(Integer itemCount){
                this.itemCount = itemCount;
                return this;
            }

            public Builder withxCoord(Integer xCoord){
                this.xCoord = xCoord;
                return this;
            }

            public Builder withyCoord(Integer yCoord) {
                this.yCoord = yCoord;
                return this;
            }


            public Builder withlengthSpan(Integer lengthSpan){
                this.lengthSpan = lengthSpan;
                return this;
            }

            public Builder withWidthSpan(Integer WidthSpan){
                this.widthSpan = WidthSpan;
                return this;
            }

            public Builder hasColor(Color Color){
                this.color = Color;
                return this;
            }

            public Builder withChildren(String[] children){
                this.children = children;
                return this;
            }
        }
        public ImportedItems() {
        }
    }

