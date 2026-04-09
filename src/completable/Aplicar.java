package completable;

import java.util.function.Function;

public class Aplicar <T,R> extends StackCOmpletable{
    private Function<T,R> aplicar;
    private  Completavel<R> completavelAtual;
    public Aplicar(Function<T,R> f,Completavel<R> completavel){
        super(null);
        this.aplicar=f;
        this.completavelAtual =completavel;
    }
    @Override
    public void execute(Object o) {
     if(o instanceof Sucesso<?> s){
         try{
             var modificacao=aplicar.apply((T)s.getValor());
             completavelAtual.SetResult(new Sucesso<>(modificacao));
             return;

         }catch (Exception e){
             completavelAtual.SetResult(new Falhar(e));
             return;
         }

     }
     if(o instanceof  Falhar f){
         completavelAtual.SetResult(f);
         return;
     }
        completavelAtual.SetResult(new Falhar(new RuntimeException("Falaha ao pasr os paramentros de execute") ));

    }

}
