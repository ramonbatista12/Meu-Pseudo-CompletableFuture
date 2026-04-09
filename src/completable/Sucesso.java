package completable;

public class Sucesso<T> {
  private T valor;
  public Sucesso(T valor){
      this.valor=valor;
  }

    public T getValor() {
        return valor;
    }
}
