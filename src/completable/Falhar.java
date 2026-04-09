package completable;

public class Falhar {
    private Throwable ex;
    public Falhar(Throwable ex){
        this.ex=ex;
    }

    public Throwable getEx() {
        return ex;
    }
}
