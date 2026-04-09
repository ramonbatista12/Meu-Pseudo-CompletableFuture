package completable;

import java.util.function.Function;

public class Mapear<T,R> extends StackCOmpletable {
    private Completavel<R> completableAtual;
    private Function<T,R> funcaoMapeadora;
    public Mapear(Completavel<R> c,Function<T,R> m) {
        super(null);
        completableAtual=c;
        funcaoMapeadora=m;
    }

    @Override
    public void execute(Object o) {
        if(o instanceof Sucesso<?> s){
            try{
            var mapeament =funcaoMapeadora.apply((T)s.getValor());
            completableAtual.SetResult(new Sucesso<>(mapeament));
            return;
            }catch (Exception e){
                completableAtual.SetResult(new Falhar(e));
                return;
            }
        }
        if(o instanceof Falhar f){completableAtual.SetResult(f);return;}
        completableAtual.SetResult(new Falhar(new RuntimeException("Falaha ao receber o valor pasado")));
    }
}
