package completable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

public class Completavel <T>{
private  StackCOmpletable stackCOmpletable;

private Completavel(){}
private volatile Object resultado=null;
private static VarHandle STACK;
static {
    try{
    STACK= MethodHandles.lookup().findVarHandle(Completavel.class,"stackCOmpletable",StackCOmpletable.class);
    }catch (Exception e){
        e.printStackTrace();
        throw new RuntimeException("falha ao iniciar o var handler");
    }
}

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
    resultado=o;
    if(stackCOmpletable!=null)
    stackCOmpletable.execute(resultado);
}

public void espere(){
    while (resultado==null){
        try{Thread.sleep(100);}catch (Exception e){e.printStackTrace();}
        System.out.println(this+" Esperando pelo resultado ");
    }

}

public static<T> Completavel<T> submit(Supplier<T> funcao, ExecutorService ex){
    var completavel =new Completavel<T>();
    var tarefa = new TarefaExecutavel(funcao,completavel);
    ex.submit(tarefa);
    return completavel;
}

public T getReultado(){
     while (resultado == null){

     }
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
    StackCOmpletable stack1;

    do{
        System.out.println("tentado acesar o valor atomico");
        if(resultado != null){
         if(resultado instanceof Sucesso<?> s)
        completable.SetResult( new Sucesso<>( transformacao.apply((T)s.getValor())));
         else completable.SetResult(resultado);
        return completable;

    }
       stack1=(StackCOmpletable) STACK.get(this);

    }while (!STACK.compareAndSet(this,stack1,aplicacao));
    return completable;


}

public<R> Completavel<R> mapear(Function<T,R> f){
    var complertable = new Completavel<R>();
    var mapeamento =new Mapear<T,R>(complertable,f);
    StackCOmpletable stack1;

    do{
        System.out.println("tentado acesar o valor atomico");
        if(resultado != null){
            if(resultado instanceof Sucesso<?> s)
                complertable.SetResult( new Sucesso<>( f.apply((T)s.getValor())));
            else complertable.SetResult(resultado);
            return complertable;

        }
        stack1=(StackCOmpletable) STACK.get(this);

    }while (!STACK.compareAndSet(this,stack1,mapeamento));
    return complertable;
}

    public<R> Completavel<R> tratarException(Function<T,R> f){
        var complertable = new Completavel<R>();
        var mapeamento =new TratarException<T,R>(complertable,f);
        StackCOmpletable stack1;

        do{
            System.out.println("tentado acesar o valor atomico");
            if(resultado != null){
                if(resultado instanceof Sucesso<?> s)
                    complertable.SetResult( new Sucesso<>( f.apply((T)s.getValor())));
                else complertable.SetResult(resultado);
                return complertable;

            }
            stack1=(StackCOmpletable) STACK.get(this);

        }while (!STACK.compareAndSet(this,stack1,mapeamento));
        return complertable;
    }
    public<R> Completavel<R> mapearEnOutrsThread(Function<T,R> f,ExecutorService ex){
        var complertable = new Completavel<R>();
        var mapeamento =new AplicarEnOutraThread<>(ex,complertable,f);
        StackCOmpletable stack1;

        do{
            System.out.println("tentado acesar o valor atomico");
           if(resultado != null){
               ex.submit(()->{
                   if(resultado instanceof Sucesso<?> s)
                       complertable.SetResult( new Sucesso<>( f.apply((T)s.getValor())));
                   else complertable.SetResult(resultado);
               });

                return complertable;

            }
            stack1=(StackCOmpletable) STACK.get(this);

        }while (!STACK.compareAndSet(this,stack1,mapeamento));
        return complertable;
    }

    public <R> Completavel<R> comsumir(Icomsumir<R> c){
    var completable = new Completavel<R>();
    var comsumidor = new Consumidor<R>(completable,c);
    StackCOmpletable stackCOmpletable1 ;
    do{
        System.out.println("tentado mudar o topo da minha pila atual");
        if(resultado!=null){
            comsumidor.execute(resultado);
            completable.SetResult(resultado);
            return completable;
        }
        stackCOmpletable1 =(StackCOmpletable) STACK.get(this);
    }while (!STACK.compareAndSet(this,stackCOmpletable1,comsumidor));
    return completable;
    }

}
