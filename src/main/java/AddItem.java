import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AddItem implements Command{
    Items theItem;
    Items theParent;
    TreeItem<String> theSelected;
    String image;
    TreeItem<String> theAdded;
    public AddItem(Items parent, Items item, TreeItem<String> selected){
        theParent = parent;
        theItem = item;
        theSelected = selected;
        if (theItem.isItemContainer()){
            image = "folder.png";
        }else{
            image = "item.png";
        }
        theAdded = new TreeItem<String>(theItem.getName(), new ImageView(new Image(getClass().getResourceAsStream(image))));
    }
    @Override
    public void execute() {
        theParent.getChildren().put(theItem.getName(), theItem);
        Main.itemMap.put(theItem.getName(), theItem);

        theSelected.getChildren().add(theAdded);
        theSelected.setExpanded(true);
        System.out.println(theItem.getName() + " have been added");
    }

    @Override
    public void undo() {
        Main.itemMap.remove(theItem.getName());
        Main.itemMap.get(theAdded.getParent().getValue()).removeChild(theAdded.getValue());
        theAdded.getParent().getChildren().remove(theAdded);
        System.out.println(theItem.getName() + " have been removed");
    }

}
