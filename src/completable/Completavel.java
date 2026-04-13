package completable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

public class Completavel <T>{
private volatile StackCOmpletable stackCOmpletable;

private Completavel(){}
private volatile Object resultado=null;

private Object lock= new Object();


private static class TarefaExecutavel<T> implements Runnable{


 private Completavel completavelEmExecucao;
 private Supplier<T> tarefaASerExecutada;

    public TarefaExecutavel(Supplier<T> tarefaASerExecutada, Completavel completavelEmExecucao) {
        this.tarefaASerExecutada = tarefaASerExecutada;
        this.completavelEmExecucao = completavelEmExecucao;
    }

    @Override
    public void run() {
         var tarefa =this.tarefaASerExecutada;
         var completavel=this.completavelEmExecucao;
         tarefaASerExecutada=null;
         completavelEmExecucao=null;

        try{
            T valor=tarefa.get();
            completavel.SetResult(new Sucesso<>(valor));
        }catch (Exception e){
            e.printStackTrace();
            completavel.SetResult(new Falhar(e));
        }

    }
}

public void SetResult(Object o){
    StackCOmpletable fila =null;
    synchronized (lock){
        if(resultado!=null)return;
        resultado=o;
        System.out.println("Sete result foi chamado com sucesso  valor do resultado "+resultado);
        System.out.println("Valor da stack "+stackCOmpletable);
        fila=stackCOmpletable;
        stackCOmpletable=null;
       lock.notifyAll();
    }

    if(fila!=null){
        percorerPilha(o,fila);
      }

}

private void percorerPilha(Object o,StackCOmpletable stackCOmpletable1){
    var noAtual=stackCOmpletable1;
     while (noAtual!=null){
         System.out.println("valor do no atual "+noAtual+" seu prosimo "+noAtual.prosimo);
         noAtual.execute(o);
         noAtual=noAtual.prosimo;
     }
}


private void clearPilha(StackCOmpletable stackCOmpletable1){
    var pilha =stackCOmpletable1;
    while (pilha!=null){
        var prosimo=pilha.prosimo;
        pilha.prosimo=null;
        pilha=prosimo;
    }
}
public void espere(){
 try{synchronized (lock){
     lock.wait();
 }
     }catch (Exception e){}

}

public static<T> Completavel<T> submit(Supplier<T> funcao, ExecutorService ex){

        var completavel =new Completavel<T>();
        var tarefa = new TarefaExecutavel(funcao,completavel);
        ex.submit(tarefa);
        return completavel;


}

public T getReultado(){
    synchronized (lock){
        while (resultado == null){
         System.out.println("valor da stacck "+stackCOmpletable);
           System.out.println("o valor do resultado aonda nao chego entao vou esperar");

             try {
                 lock.wait();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }}

    if(resultado instanceof Sucesso<?> s){
        return (T) s.getValor();
    }
    if(resultado instanceof  Falhar f)
        f.getEx().printStackTrace();

   System.out.println("valor do resultado pasado antes do null "+resultado);
    return null;
}

public<R> Completavel<R> aplicar(Function<T,R>  transformacao ){
    var completable = new Completavel<R>();
    var aplicacao= new Aplicar<T,R>(transformacao,completable);
    synchronized (lock){

        StackCOmpletable stack1;
       if(resultado==null){
           setStackCOmpletable(aplicacao);
           return completable;
       } }

        try {
            if(resultado instanceof Sucesso<?> s)
                completable.SetResult(new Sucesso<>((T) transformacao.apply((T)s.getValor())));
            if(resultado instanceof  Falhar f) completable.SetResult(f);
        }catch (Exception e){
            completable.SetResult(new Falhar(e));
        }
        return completable;




}

public<R> Completavel<R> mapear(Function<T,R> f){
    var complertable = new Completavel<R>();
    var mapeamento =new Mapear<T,R>(complertable,f);
    synchronized (lock){

        if(resultado==null){
            setStackCOmpletable(mapeamento);
            return complertable;
        } }
       try {
            if(resultado instanceof Sucesso<?> s)
                complertable.SetResult(new Sucesso<>((T) f.apply((T)s.getValor())));
            if(resultado instanceof  Falhar f1) complertable.SetResult(f1);
        }catch (Exception e){
            e.printStackTrace();
            complertable.SetResult(new Falhar(e));
        }
        return complertable;


}

    public<R> Completavel<R> tratarException(Function<Throwable,R> f){

        var complertable = new Completavel<R>();
        var exception =new TratarException<T,R>(complertable,f);
        synchronized (lock){

            if(resultado==null){
                setStackCOmpletable(exception);
                return complertable;
            }}
            try {
                if(resultado instanceof Sucesso<?> s)
                    complertable.SetResult(s);
                if(resultado instanceof  Falhar f1){
                    var tratamento=f.apply(f1.getEx());
                    complertable.SetResult(new Sucesso<>(tratamento));
                }
            }catch (Exception e){
                e.printStackTrace();
                complertable.SetResult(new Falhar(e));
            }
            return complertable;

    }
    public<R> Completavel<R> mapearEnOutrsThread(Function<T,R> f,ExecutorService ex){
        var complertable = new Completavel<R>();
        var mapeamento =new AplicarEnOutraThread<>(ex,complertable,f);


        synchronized (lock){

            if(resultado==null){
                setStackCOmpletable(mapeamento);
                return complertable;
            }
        }
            try {
                if(resultado instanceof Sucesso<?> s)
                    complertable.SetResult(new Sucesso<>((T) f.apply((T)s.getValor())));
                if(resultado instanceof  Falhar f1) complertable.SetResult(f1);
            }catch (Exception e){
                e.printStackTrace();
                complertable.SetResult(new Falhar(e));
            }
            return complertable;



    }

private void setStackCOmpletable(StackCOmpletable no){
    if(stackCOmpletable==null){
        System.out.println("a stack e null vou setala com "+no);
        stackCOmpletable=no;}
    else {
        System.out.println("stack nao e null "+stackCOmpletable+" seu prosimo "+stackCOmpletable.prosimo);
        var ponteiroAuxilia=stackCOmpletable;
        no.prosimo=ponteiroAuxilia;
        stackCOmpletable=no;
        System.out.println("stack  apos modificaccao "+stackCOmpletable+" seu prosimo "+stackCOmpletable.prosimo);
    }
}
    public <R> Completavel<R> comsumir(Icomsumir<R> c){
        var completable = new Completavel<R>();
        var comsumidor = new Consumidor<R>(completable,c);
        synchronized (lock){
            System.out.println("Setando a pilha de comsumir");

            if(resultado==null){
                setStackCOmpletable(comsumidor);
                return  completable;
            }}

            try {
                if(resultado instanceof Sucesso<?> s){
                    c.comsumir((R)s.getValor());
                    completable.SetResult(s);
                }

                if(resultado instanceof  Falhar f1) completable.SetResult(f1);
            }catch (Exception e){
                e.printStackTrace();
                completable.SetResult(new Falhar(e));
            }
            return  completable;


    }


}
