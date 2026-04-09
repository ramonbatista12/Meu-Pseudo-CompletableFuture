package completable;

import java.util.function.Function;

public class TratarException<T ,R> extends StackCOmpletable{
    private Completavel<R> completableAtual;
    private Function<T  ,R> funcao;
    public TratarException(Completavel<R> c,Function<T,R> f) {
        super(null);
        completableAtual=c;
        funcao=f;
    }

    @Override
    public void execute(Object o) {
        if(o instanceof Falhar f){
            try{
            var tratamento =funcao.apply((T)f.getEx());
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
