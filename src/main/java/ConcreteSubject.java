import java.util.ArrayList;

public class ConcreteSubject implements Subject {
    private ArrayList<Items> observers;
    private int count;

    public ConcreteSubject() {
        observers = new ArrayList<>();
        count = 0;
    }

    public void attach(Items i) { observers.add(i); }

    public void detach(Items i) { observers.remove(i); }

    public void notifyObservers() {
        for (Items i : observers) {
            i.update(this);
        }
    }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

}
