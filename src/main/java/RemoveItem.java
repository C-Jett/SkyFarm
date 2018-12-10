import javafx.scene.control.TreeItem;

import java.util.LinkedList;
import java.util.Map;

public class RemoveItem implements Command {
    Items theItem;
    LinkedList<Map.Entry<String, Items>> theChildren;
    Items theParent;
    TreeItem<String> theMom;
    TreeItem<String> theRemoved;
    public RemoveItem(Items item, TreeItem<String> selected, Items parent, TreeItem<String> mommy){
        theParent = parent;
        theItem = item;
        theRemoved = selected;
        theMom = mommy;
        theChildren = new LinkedList<>();
    }
    @Override
    public void execute() {
        if(theItem.isItemContainer()) {
            for (Map.Entry<String, Items> entry : theItem.getChildren().entrySet()) {
                theChildren.push(entry);
                theItem.removeChild(entry.getKey());
                Main.itemMap.remove(entry.getKey());
            }
        }
        Main.itemMap.remove(theItem.getName());
        Main.itemMap.get(theRemoved.getParent().getValue()).removeChild(theRemoved.getValue());
        theRemoved.getParent().getChildren().remove(theRemoved);
        System.out.println(theItem.getName() + " have been removed");
    }

    @Override
    public void undo() {
        theParent.getChildren().put(theItem.getName(), theItem);
        Main.itemMap.put(theItem.getName(), theItem);
        theMom.getChildren().add(theRemoved);
        if (theItem.isItemContainer()){
            String[] Children = new String[theChildren.size()];
            for (int num=0; num < theChildren.size(); num++){
                Children[num] = theChildren.get(num).getKey();
            }
            while (theChildren.size() > 0){
                Map.Entry<String, Items> child = theChildren.pop();
                theItem.setChildren(Children);
                Main.itemMap.put(child.getKey(), child.getValue());
            }
        }
        System.out.println(theItem.getName() + " have been re-added");
    }
}
