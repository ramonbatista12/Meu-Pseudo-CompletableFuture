package completable;

public class Consumidor<R> extends StackCOmpletable{
    private Icomsumir<R> comsumidor;
    private Completavel<R> atual;
    public Consumidor(Completavel<R> c ,Icomsumir<R> cm) {
        super(null);
        this.atual=c;
        this.comsumidor=cm;
    }

    @Override
    public void execute(Object o) {

         if(o instanceof Sucesso<?> s){
             comsumidor.comsumir((R)s.getValor());
             atual.SetResult(s);
             return;
         }
         if(o instanceof  Falhar f) {
              f.getEx().printStackTrace();
             atual.SetResult(f);
             return;
         }

         else atual.SetResult(new Throwable("Ececao de aplicacao :Execao na pasgem dos parametros "+o));

    }
}
