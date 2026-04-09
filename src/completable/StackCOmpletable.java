package completable;

public abstract class StackCOmpletable {
    public StackCOmpletable prosimo;

    public StackCOmpletable(StackCOmpletable prosimo){
        this.prosimo=prosimo;

    }
    public abstract void execute(Object o);

    public  void put(StackCOmpletable prosimo){
        this.prosimo =prosimo;
    }
}
