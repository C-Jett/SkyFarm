import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private CommandManager commandManager = new CommandManager();

    @FXML
    private TreeView<String> tree;

    @FXML
    private TextField priceBox;

    @FXML
    private TextField nameBox;

    @FXML
    private TextField xCoord;

    @FXML
    private TextField lenBox;

    @FXML
    private TextField yCoord;

    @FXML
    private TextField widthBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button newButton;

    @FXML
    private CheckBox containerCheck;

    @FXML
    private GridPane farmGrid;

    @FXML
    private TextField marketPriceBox;

    @FXML
    private TextArea currentPrice;

    @FXML
    private TextArea CurrentMarketValue;

    @FXML
    private Button updateCountButton;

    @FXML
    private TextArea itemCountBox;

    @FXML
    private TextArea commandLog;

    @FXML
    private Button ImportFarm;

    private static final String DEFAULT_NAME = "SkyFarm";
    private static final String FILENAME = "skyfarm.list";
//    private static final String RESOURCES = "resources/";
    private static final String FOLDER_IMAGE = "folder.png";
    private static final String ITEM_IMAGE = "item.png";
    private static final String ITEM_CONTAINER_CODE = "ic";
    private static final String SEPARATOR = "|";
    private static final String SEPARATOR_REGEX = "\\|";
    private static final String COMMA = ",";
    private static final String EMPTY_STRING = "";
    private boolean fileExists = false;
    private static final Map<String, Integer> INDEX_MAP;
    static
    {
        INDEX_MAP = new HashMap<String, Integer>();
        INDEX_MAP.put("isContainer", 0);
        INDEX_MAP.put("name", 1);
        INDEX_MAP.put("price", 2);
        INDEX_MAP.put("marketPrice", 3);
        INDEX_MAP.put("itemCount", 4);
        INDEX_MAP.put("xcoord", 5);
        INDEX_MAP.put("ycoord", 6);
        INDEX_MAP.put("lengthSpan", 7);
        INDEX_MAP.put("widthSpan", 8);
        INDEX_MAP.put("red", 9);
        INDEX_MAP.put("green", 10);
        INDEX_MAP.put("blue", 11);
        INDEX_MAP.put("children", 12);
    }

    public void initialize(URL url, ResourceBundle rb){
        loadTreeItems(DEFAULT_NAME);
    }

    //Reads skyfarm.json file if it exists and returns the root object from the file.
    //If it doesn't exist the it will return a new root item with the name DEFAULT_NAME
    public Items readFile(){
        //Setting default root item
        Items rootItem = new ItemContainer(DEFAULT_NAME,0.0,0.0, 0, 0,0,0,0, Color.BLACK);
        try{
            //Setting the file
            File inFile = new File(FILENAME);
            //Checking if the file exists and also making sure it is not empty
            if(inFile.exists() && inFile.length() > 0){
                //Use the stream to retrieve the map object from the file and set the main item map equal to it
                FileReader file = new FileReader(inFile);
                BufferedReader reader = new BufferedReader(file);
                Map<String, String[]> hasChildMap = new HashMap<String, String[]>();

                String line;

                while((line = reader.readLine()) != null){
                    Items lineItem = parseLine(line, hasChildMap);
                    Main.itemMap.put(lineItem.getName(), lineItem);

                    if(lineItem.getName().equalsIgnoreCase(DEFAULT_NAME)){
                        rootItem = lineItem;
                        fileExists = true;
                    }
                }

                for(Map.Entry<String, String[]> entry : hasChildMap.entrySet()){
                    Main.itemMap.get(entry.getKey()).setChildren(entry.getValue());
                }
                file.close();
                reader.close();
            }else{
                //If the file doesn't exist then we create it here
                inFile.createNewFile();
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return rootItem;
    }

    private Items parseLine(String line, Map<String, String[]> childMap){
        Items item = null;

        String[] atts = line.split(SEPARATOR_REGEX);
        Color color = Color.color(Double.parseDouble(atts[INDEX_MAP.get("red")]),Double.parseDouble(atts[INDEX_MAP.get("green")]),Double.parseDouble(atts[INDEX_MAP.get("blue")]));
        String[] children = atts[INDEX_MAP.get("children")].split(COMMA);
        if(!children[0].equalsIgnoreCase("null")) {
            childMap.put(atts[INDEX_MAP.get("name")], children);
        }
        if(atts[INDEX_MAP.get("isContainer")].equalsIgnoreCase(ITEM_CONTAINER_CODE)){
            item = new ItemContainer(atts[INDEX_MAP.get("name")],Double.parseDouble(atts[INDEX_MAP.get("price")]),Double.parseDouble(atts[INDEX_MAP.get("marketPrice")]), Integer.parseInt(atts[INDEX_MAP.get("itemCount")]), Integer.parseInt(atts[INDEX_MAP.get("xcoord")]), Integer.parseInt(atts[INDEX_MAP.get("ycoord")]), Integer.parseInt(atts[INDEX_MAP.get("lengthSpan")]),Integer.parseInt(atts[INDEX_MAP.get("widthSpan")]),color);
        }else{
            item = new Item(atts[INDEX_MAP.get("name")],Double.parseDouble(atts[INDEX_MAP.get("price")]),Double.parseDouble(atts[INDEX_MAP.get("marketPrice")]), Integer.parseInt(atts[INDEX_MAP.get("itemCount")]), Integer.parseInt(atts[INDEX_MAP.get("xcoord")]), Integer.parseInt(atts[INDEX_MAP.get("ycoord")]), Integer.parseInt(atts[INDEX_MAP.get("lengthSpan")]),Integer.parseInt(atts[INDEX_MAP.get("widthSpan")]),color);
        }

        //observer pattern: attach item to subject observers list
        subject.attach(item);

        return item;
    }
    /**Recursive method to build the TreeItem
     Param: treeItem - the TreeItem that represents the item
     Param: item - the next node in the tree that needs to be traversed
     */
    public TreeItem<String> buildTree(TreeItem<String> treeItem, Items item){
        //Creating new TreeItem instance for this iteration of the tree traversal
        Main.itemMap.put(item.getName(), item);
        TreeItem<String> newTreeItem = createNewTreeItem(item);
        //Getting the children of this item
        HashMap<String, Items> children = item.getChildren();
        //Checking if the item has children
        if(children != null && children.size() > 0) {
            //Looping through all of the items children
            for (Map.Entry<String, Items> entry : children.entrySet()) {
                //Creating a new TreeItem for the child
                TreeItem<String> childTreeItem = new TreeItem<String>(entry.getKey());
                /**
                 * This is where all of the magic happens
                 * We set this childTreeItem and all of its children
                 * as a child for the newTreeItem through recursion
                 */
                newTreeItem.getChildren().add(buildTree(childTreeItem, entry.getValue()));
            }
        }
        //Return the newTreeItem after having all of its children set
        return newTreeItem;
    }

    //This method draws the boxes on in the gridPane
    public void drawFarm(String itemName){
        farmGrid.getChildren().clear();
        Map<String, Items> tempMap = new HashMap<String, Items>();
        //Loop through all of the items in Main.itemMap
        for(Map.Entry<String, Items> entry : Main.itemMap.entrySet()){
            //Use the items attributes to draw the item on the screen
            Items item = entry.getValue();
            if(item.isItemContainer()) {
                Rectangle rec = new Rectangle();
                rec.setWidth(item.getWidthSpan());
                rec.setHeight(item.getLengthSpan());
                Color c = Color.color(item.getRed(), item.getGreen(), item.getBlue());
                if (item.getName().equals(itemName)) {
                    rec.setFill(item.getColor());
                } else {
                    rec.setFill(Color.WHITE);
                }
                rec.setStroke(c);
                farmGrid.setRowIndex(rec, item.getyCoord());
                farmGrid.setColumnIndex(rec, item.getxCoord());
                farmGrid.getChildren().addAll(rec);
            }else{
                tempMap.put(item.getName(), item);
            }
        }
        for(Map.Entry<String, Items> entry : tempMap.entrySet()){
            //Use the items attributes to draw the item on the screen
            Items item = entry.getValue();
            if(!item.isItemContainer()) {
                Rectangle rec = new Rectangle();
                rec.setWidth(item.getWidthSpan());
                rec.setHeight(item.getLengthSpan());
                Color c = Color.color(item.getRed(), item.getGreen(), item.getBlue());
                if (item.getName().equals(itemName)) {
                    rec.setFill(item.getColor());
                } else {
                    rec.setFill(Color.WHITE);
                }
                rec.setStroke(c);
                farmGrid.setRowIndex(rec, item.getyCoord());
                farmGrid.setColumnIndex(rec, item.getxCoord());
                farmGrid.getChildren().addAll(rec);
            }
        }
    }

    //Adds the item's attributes to the screen
    public void displayItem(Items item){
        //Here we just check if the items attribute is null or empty and set the attribute value in the corresponding textbox
        if(!item.getName().isEmpty() || item.getName() != null) nameBox.setText(item.getName());
        priceBox.setText(String.valueOf(item.getPrice()));
        marketPriceBox.setText(String.valueOf(item.getMarketPrice()));
        xCoord.setText(String.valueOf(item.getxCoord()));
        yCoord.setText(String.valueOf(item.getyCoord()));
        widthBox.setText(String.valueOf(item.getWidthSpan()));
        lenBox.setText(String.valueOf(item.getLengthSpan()));
        colorPicker.setValue(Color.color(item.getRed(), item.getGreen(), item.getBlue()));
        //Edit to be multi condition to fix the market price disappearance when needing to tadd new item to ntiem continer.
        if(item.isItemContainer()){
            containerCheck.setSelected(true);
            marketPriceBox.setVisible(false);
        }else{
            containerCheck.setSelected(false);
            marketPriceBox.setVisible(true);

        }
        valueFinder(item);
    }

    private TreeItem<String> createNewTreeItem(Items item){
        String image = "";
        if(item.isItemContainer()) {
            image = image + FOLDER_IMAGE;
        }else{
            image = image + ITEM_IMAGE;
        }

        return new TreeItem<String>(item.getName(), new ImageView(new Image(getClass().getResourceAsStream(image))));
    }

    //Writes Main.itemMap to the file
    public void saveMap(){
        try {
            File outFile = new File(FILENAME);
            PrintWriter writer = new PrintWriter(outFile);

            if(!outFile.exists()){
                outFile.createNewFile();
            }else{
                writer.write(EMPTY_STRING);
                writer.flush();
            }

            writer.append(getRecord(Main.itemMap.get(DEFAULT_NAME)) + "\n");

            for(Map.Entry<String, Items> entry : Main.itemMap.entrySet()){
                if(!entry.getKey().equalsIgnoreCase(DEFAULT_NAME)) {
                    writer.append(getRecord(entry.getValue()) + "\n");
                }
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e){e.printStackTrace();}
    }

    private String getRecord(Items item){
        String[] atts = new String[INDEX_MAP.size()];
        StringBuffer record = new StringBuffer();

        if(item.isItemContainer()) {
            atts[INDEX_MAP.get("isContainer")] = "ic";
        }else{
            atts[INDEX_MAP.get("isContainer")] = "i";
        }
        atts[INDEX_MAP.get("name")] = item.getName();
        atts[INDEX_MAP.get("price")] = String.valueOf(item.getPrice());
        atts[INDEX_MAP.get("marketPrice")] = String.valueOf(item.getMarketPrice());
        atts[INDEX_MAP.get("itemCount")] = String.valueOf(item.getItemCount());
        atts[INDEX_MAP.get("xcoord")] = String.valueOf(item.getxCoord());
        atts[INDEX_MAP.get("ycoord")] = String.valueOf(item.getyCoord());
        atts[INDEX_MAP.get("lengthSpan")] = String.valueOf(item.getLengthSpan());
        atts[INDEX_MAP.get("widthSpan")] = String.valueOf(item.getWidthSpan());
        atts[INDEX_MAP.get("red")] = String.valueOf(item.getRed());
        atts[INDEX_MAP.get("green")] = String.valueOf(item.getGreen());
        atts[INDEX_MAP.get("blue")] = String.valueOf(item.getBlue());
        StringBuilder childBuilder = new StringBuilder();
        if(item.isItemContainer()) {
            boolean first = true;
            for (Map.Entry<String, Items> childEntry : item.getChildren().entrySet()) {
                if(!first){
                    childBuilder.append(",");
                }else{
                    first = false;
                }
                childBuilder.append(childEntry.getKey());
            }
            if(childBuilder.toString().isEmpty() || childBuilder.toString().startsWith("null")) {
                atts[INDEX_MAP.get("children")] = "null";
            }else{
                atts[INDEX_MAP.get("children")] = childBuilder.toString();
            }
        }
        for(String att : atts){
            record.append(att + SEPARATOR);
        }

        return record.toString();
    }

    //Sets tree and can load array of saved tree items
    public void loadTreeItems(String ...rootItems){
        //Assign the root item to whatever returns from the readFile method
        Items rootItem = readFile();
        //Set the root TreeItem using the rootItem
        TreeItem<String> root = new TreeItem<>(rootItem.getName());
        //Set Root in Main.itemap
        Main.itemMap.put(rootItem.getName(), rootItem);
        root.setExpanded(true);
        tree.setShowRoot(false);
        //If the file exists, build the tree based on the file
        if(fileExists) {
            root.getChildren().add(buildTree(root, rootItem));
            drawFarm(EMPTY_STRING);
            //If the file doesn't exist, create a new tree
        }else{
            root.getChildren().add(new TreeItem<>(new Item(rootItem.getName(), 0.0,0.0, 0, 1,1,1,1, Color.BLACK).getName()));
        }
        tree.setRoot(root);
        //Adds a listener to the Treeview in order to respond to changes in selection
        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                //Display items attributes within the textboxes
                displayItem(Main.itemMap.get(selectedItem.getValue()));
                drawFarm(selectedItem.getValue());
            }
        });

    }

    public Map.Entry<String,Items> getEntrySet(String findingKey) {
        ArrayList<HashMap<String, Items>> toCheck = new ArrayList<>();
        toCheck.add(Main.itemMap);
        while (!toCheck.isEmpty()) {
            HashMap<String, Items> checking = toCheck.remove(0);
            for (Map.Entry<String, Items> entry : checking.entrySet()) {
                if (entry.getKey().equals(findingKey)) return entry;
                if (entry.getValue().isItemContainer()) toCheck.add(entry.getValue().getChildren());
            }
        }
        return null;
    }

    public boolean containsKey(String findingKey){
        ArrayList<HashMap<String, Items>> toCheck = new ArrayList<>();
        toCheck.add(Main.itemMap);
        while(!toCheck.isEmpty()){
            HashMap<String, Items> checking = toCheck.remove(0);
            for(Map.Entry<String,Items> entry:checking.entrySet()){
                if (entry.getKey().equals(findingKey)) return true;
                if (entry.getValue().isItemContainer()) toCheck.add(entry.getValue().getChildren());
            }

        };
        return false;
    }
    //Visitor Pattern implementation
    PriceVisitor priceCalc = new PriceVisitor();
    MarketPriceVisitor marketPriceCalc = new MarketPriceVisitor();


    public double sumPrices(Items item){
        double totalPrice = item.accept(priceCalc);
        HashMap<String, Items> children = item.getChildren();
        if(children != null && children.size() > 0) {
            for (Map.Entry<String, Items> entry : children.entrySet()) {
                totalPrice+=sumPrices(entry.getValue());
            }
        }
        return totalPrice;
    }

    public double sumMarketValues(Items item){
        double totalPrice = item.accept(marketPriceCalc);
        HashMap<String, Items> children = item.getChildren();
        if(children != null && children.size() > 0) {
            for (Map.Entry<String, Items> entry : children.entrySet()) {
                totalPrice+= sumMarketValues(entry.getValue());
            }
        }
        return totalPrice;
    }

    public void valueFinder(Items item){
        currentPrice.setText("Current Price:  " + sumPrices(item));
        CurrentMarketValue.setText("Market Value:  " + sumMarketValues(item));
    }

    //Observer pattern implementation
    private ConcreteSubject subject = new ConcreteSubject();

    public int countItems(Items item){
        int count = 1;
        if(item.isItemContainer()) { count = 0; }
        HashMap<String, Items> children = item.getChildren();
        if(children != null && children.size() > 0) {
            for (Map.Entry<String, Items> entry : children.entrySet()) {
                count += countItems(entry.getValue());
            }
        }
        return count;
    }

    private void itemCount() {
        Items root = readFile();
        int count = countItems(root);
        subject.setCount(count);
        subject.notifyObservers();
        itemCountBox.setText("Number of Items:  " + count);
    }

    private void addLog(String theLog) {
        String prev = commandLog.getText();
        commandLog.setText(prev + theLog + "\n");
    }

    //Adds new item to the tree with the selected value
    @FXML
    void addNewItem(ActionEvent event) {
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
        Items item = Main.itemMap.get(selectedItem.getValue());

        if(selectedItem == null || nameBox.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please select an object from the tree.");
        }else if(!item.isItemContainer()){
            JOptionPane.showMessageDialog(null, "You cannot add children to an item. Please use an item container to add a new child.");
        }else{
            //Add a bool to check for item-container
            //marketPriceBox.clear();
            Color cp = colorPicker.getValue();
            Items newItem = !containerCheck.isSelected() ? new Item(nameBox.getText(), Double.parseDouble(priceBox.getText()), Double.parseDouble(marketPriceBox.getText()), 0, (Integer.parseInt(xCoord.getText())), (Integer.parseInt(yCoord.getText())),Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), cp):
                    new ItemContainer(nameBox.getText(), Double.parseDouble(priceBox.getText()),0.0, 0, (Integer.parseInt(xCoord.getText())),(Integer.parseInt(yCoord.getText())), Integer.parseInt(lenBox.getText()),Integer.parseInt(widthBox.getText()), cp);
            AddItem addItem = new AddItem(item, newItem, selectedItem);
            commandManager.Execute(addItem);
            addLog(newItem.getName() + " was added to " + item.getName());
            saveMap();
            drawFarm(EMPTY_STRING);

            //observer pattern: attach item to observers list of subject
            subject.attach(newItem);
        }
    }

    @FXML
    void confirmEdit(ActionEvent event) {
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
        if(containerCheck.isSelected()){

            selectedItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(FOLDER_IMAGE))));
            Main.itemMap.put(selectedItem.getValue(), new ItemContainer(nameBox.getText(), Double.parseDouble(priceBox.getText()),0.0, 0, Integer.parseInt(xCoord.getText()), Integer.parseInt(yCoord.getText()),Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), colorPicker.getValue()));
        }else{
            if(selectedItem.getChildren() == null || selectedItem.getChildren().isEmpty()) {
                selectedItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(ITEM_IMAGE))));
                Main.itemMap.put(selectedItem.getValue(), new Item(nameBox.getText(), Double.parseDouble(priceBox.getText()),Double.parseDouble(marketPriceBox.getText()), 0, Integer.parseInt(xCoord.getText()), Integer.parseInt(yCoord.getText()), Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), colorPicker.getValue()));
            }else{
                JOptionPane.showMessageDialog(null, "Must remove children before you can change this container into an item.");
            }
        }
        saveMap();
        drawFarm(selectedItem.getValue());
    }

    @FXML
    void removeItem(ActionEvent event) {
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();

        if(selectedItem == null || selectedItem.getValue() == DEFAULT_NAME){
            JOptionPane.showMessageDialog(null, "Please add a new item to the list!");
        }

        else{
            Items item = Main.itemMap.get(selectedItem.getValue());
            Items myParent = Main.itemMap.get(selectedItem.getParent().getValue());
            commandManager.Execute(new RemoveItem(item, selectedItem, myParent, selectedItem.getParent()));
            addLog(item.getName() + " was removed from " + myParent.getName());
            saveMap();
            drawFarm(EMPTY_STRING);

            //observer pattern: detach item from observers list of subject
            subject.detach(item);
        }
    }
    @FXML
    void undoCommand(ActionEvent event) {
        commandManager.Undo();
        addLog("Command was Undone");
        saveMap();
        drawFarm(EMPTY_STRING);
    }
    @FXML
    void redoCommand(ActionEvent event) {
        commandManager.Redo();
        addLog("Command was Redone");
        saveMap();
        drawFarm(EMPTY_STRING);
    }
    //Most certainly not needed anymore, as selection model does this.
    @FXML
    void selectItem(MouseEvent event) {

    }

    @FXML
    void sendName(ActionEvent event) {
        nameBox.getText();
    }

    @FXML
    void sendPrice(ActionEvent event) {
        Integer.parseInt(priceBox.getText());
    }

    @FXML
    void setColor(ActionEvent event) {

    }

    @FXML
    void setLen(ActionEvent event) {

    }

    @FXML
    void setWidth(ActionEvent event) {

    }

    @FXML
    void setXCoord(ActionEvent event) {

    }

    @FXML
    void setYCoord(ActionEvent event) {

    }

    @FXML
    void sendMarketPrice(ActionEvent event) {

        Integer.parseInt(marketPriceBox.getText());
    }

    @FXML
    void ItemContainerChecked(MouseEvent event) {
        if(containerCheck.isSelected()){
            marketPriceBox.clear();
            marketPriceBox.setVisible(false);
        }else{
            marketPriceBox.clear();
            marketPriceBox.setVisible(true);
        }
        if(!containerCheck.isSelected()){
            marketPriceBox.setVisible(true);
        }
    }

    @FXML
    void updateCount(ActionEvent event) {
        itemCount();
    }

    @FXML
    void ImportFarm(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv","*.csv"));
        File F = fc.showOpenDialog(null);
        if(F != null) {
            String csvFile = F.getAbsolutePath();
            String line = "";
            String cvsSplitBy = ",";
            Items rootItem = new ItemContainer(DEFAULT_NAME, 0.0, 0.0, 0, 0, 0, 0, 0, Color.BLACK);
            Map<String, String[]> hasChildMap = new HashMap<String, String[]>();

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                Main.itemMap = new HashMap<String, Items>();

                while ((line = br.readLine()) != null) {

                    // use comma as separator
                    String[] Farm = line.split(cvsSplitBy);
                    String[] children = Farm[12].split(SEPARATOR_REGEX);
                    for (String c : children) {
                        System.out.println("Child: " + c);
                    }
                    if (!children[0].equalsIgnoreCase("null")) {
                        hasChildMap.put(Farm[1], children);
                    }

                    ImportedItems.Builder item = new ImportedItems.Builder(Farm[1])
                            .withType(Farm[0])
                            .withPrice(Double.parseDouble(Farm[2]))
                            .withMarketPrice(Double.parseDouble(Farm[3]))
                            .hasItemCount(Integer.parseInt(Farm[4]))
                            .withxCoord(Integer.parseInt(Farm[5]))
                            .withyCoord(Integer.parseInt(Farm[6]))
                            .withlengthSpan(Integer.parseInt(Farm[7]))
                            .withWidthSpan(Integer.parseInt(Farm[8]))
                            .hasColor(Color.color(Double.parseDouble(Farm[9]), Double.parseDouble(Farm[10]), Double.parseDouble(Farm[11])))
                            .withChildren(children);

                    Items newItem;
                    if (item.getType().equalsIgnoreCase(ITEM_CONTAINER_CODE)) {
                        newItem = new ItemContainer(item.getName(), item.getPrice(), item.getMarketPrice(), item.getItemCount(), item.getxCoord(), item.getyCoord(), item.getLengthSpan(), item.getWidthSpan(), item.getColor());
                    } else {
                        newItem = new Item(item.getName(), item.getPrice(), item.getMarketPrice(), item.getItemCount(), item.getxCoord(), item.getyCoord(), item.getLengthSpan(), item.getWidthSpan(), item.getColor());
                    }

                    if (newItem.getName().equalsIgnoreCase(DEFAULT_NAME)) {
                        rootItem = newItem;
                        fileExists = true;
                    }
                    //observer pattern: attach item to subject observers list
                    subject.attach(newItem);
                    Main.itemMap.put(newItem.getName(), newItem);

                    System.out.println(newItem.getName());
                }

                for (Map.Entry<String, String[]> entry : hasChildMap.entrySet()) {
                    Main.itemMap.get(entry.getKey()).setChildren(entry.getValue());
                }

                //Set the root TreeItem using the rootItem
                TreeItem<String> root = new TreeItem<>(rootItem.getName());
                //Set Root in Main.itemap
                Main.itemMap.put(rootItem.getName(), rootItem);
                root.setExpanded(true);
                tree.setShowRoot(false);
                //If the file exists, build the tree based on the file
                root.getChildren().add(buildTree(root, rootItem));
                drawFarm(EMPTY_STRING);

                setSelectedTreeItem();
                tree.setRoot(root);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSelectedTreeItem(){
       MultipleSelectionModel msm = tree.getSelectionModel();
       int row = tree.getRow(tree.getRoot());
       msm.select(row);
    }
}

