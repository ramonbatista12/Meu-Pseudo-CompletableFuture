package completable;

import java.util.function.Function;

public class TratarException<Throable ,R> extends StackCOmpletable{
    private Completavel<R> completableAtual;
    private Function<Throwable  ,R> funcao;
    public TratarException(Completavel<R> c,Function<Throwable,R> f) {
        super(null);
        completableAtual=c;
        funcao=f;
    }

    @Override
    public void execute(Object o) {
        if(o instanceof Falhar f){
            try{
                Throwable throwable=f.getEx();
            var tratamento =funcao.apply(f.getEx());
            completableAtual.SetResult(new Sucesso<>(tratamento));
            return;
            }catch (Exception e){
                completableAtual.SetResult(new Falhar(e));
                return;
            }
        }
        if(o instanceof Sucesso<?> s){
            completableAtual.SetResult(s);
            return;
        }


        completableAtual.SetResult(new Falhar(new Throwable("valor pasado nao se refere as clases de resultados criadas")));

    }
}
