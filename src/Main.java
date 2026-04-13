import completable.Completavel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando o pool de treads");
        var executor = Executors.newFixedThreadPool(3);
        System.out.println("Fazeendo o completable 1");
        var completavel = Completavel.submit(()->{
            try {
              Thread.sleep(5000);
            }catch (Exception e){}
            System.out.println("metodo submit foi chamdao");
            return 10000L;

        },executor);
        completavel.aplicar((v)->{System.out.println("printando valor1 "+v); return v;});
        completavel.aplicar((v)->{System.out.println("printando valor2 "+v); return v;});
        var completableFalho=completavel.aplicar((v)->{throw new RuntimeException("Ececao de teste para o completable");});
        completableFalho.tratarException((throwable)->{
            System.err.println("Mostrando a pilha do erro ");
            throwable.printStackTrace();
            return 1000;
        });
        System.out.println("Fazeendo o completable 2");
        var completavel2 =completavel.mapearEnOutrsThread((v)->{
            var longos = (Long) v;
            System.out.println(Thread.currentThread().getName()+" sou a thread que vai executar essa tarefa");
            return longos.toString();
        },executor).aplicar((v)->{
            return  0b01010101010000;
        });

        //completavel2.espere();
        System.out.println("Fazeendo o completable 3");
        var completable4=completavel2.mapearEnOutrsThread((v)->{
            System.out.println("Novo mapeamento acontecendo em outra thread "+Thread.currentThread().getName());
            return  v;
        },executor).comsumir((v)->{System.out.println("o valor de v e "+v);});
        System.out.println(completavel2.getReultado());
        completable4.comsumir((v)->System.out.println("Printando respsota na piolha "));

        completable4.espere();

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
        var completableFuture = CompletableFuture.supplyAsync(()->{return 40000;});

        executor.shutdownNow();
    }
}