import completable.Completavel;

import java.util.concurrent.Executors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        var executor = Executors.newFixedThreadPool(3);

        var completavel = Completavel.submit(()->{
            try {
                Thread.sleep(10000);
            }catch (Exception e){}
            return 10000L;

        },executor);

        var completavel2 =completavel.mapearEnOutrsThread((v)->{
            var longos = (Long) v;
            System.out.println(Thread.currentThread().getName()+" sou a thread que vai executar essa tarefa");
            return longos.toString();
        },executor).aplicar((v)->{
            return  0b01010101010000;
        });

        completavel2.espere();
        var completable3=completavel2.mapearEnOutrsThread((v)->{
            System.out.println("Novo mapeamento acontecendo em outra thread "+Thread.currentThread().getName());
            return  v;
        },executor).comsumir((v)->{System.out.println("o valor de v e "+v);});
        System.out.println(completavel2.getReultado());
        completable3.espere();

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }

        executor.shutdownNow();
    }
}