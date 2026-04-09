package completable;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class AplicarEnOutraThread<T,R> extends StackCOmpletable {
    private ExecutorService serviceContext;
    private Completavel<R> completavelAtual;
    private Function<T,R> funcaoAplicacao;

    public AplicarEnOutraThread(ExecutorService serviceContext, Completavel<R> completavelAtual, Function<T, R> funcaoAplicacao) {
        super(null);
        this.serviceContext = serviceContext;
        this.completavelAtual = completavelAtual;
        this.funcaoAplicacao = funcaoAplicacao;
    }

    @Override
    public void execute(Object o) {
        if(o instanceof Sucesso<?> s){
            serviceContext.execute(()->{
                System.out.println("a tarefa sera executada de agora em diante  ");
                try{
                var mapeamento =funcaoAplicacao.apply((T)s.getValor());
                completavelAtual.SetResult(new Sucesso<>(mapeamento));
                }catch (Exception e){
                    e.printStackTrace();
                    completavelAtual.SetResult(new Falhar(e));
                }

            });
            return;
        }
        if(o instanceof Falhar f){

            serviceContext.execute(()->{
                System.out.println("a tarefa foi executada com falha "+f.getEx().getLocalizedMessage());
                completavelAtual.SetResult(f);
            });
            return;
        }
        serviceContext.execute(()->{
            System.out.println("a tarefa foi executada com falha ");
            completavelAtual.SetResult(new Falhar(new Throwable("Fala ao pasr ao paramentro de respsota")));
        });
    }
}
