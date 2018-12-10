public interface Subject {

    public void attach(Items i);

    public void detach(Items i);

    public void notifyObservers();

}
