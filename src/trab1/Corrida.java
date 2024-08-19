package trab1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Corrida {
    // Constante que define a linha de chegada como 200 metros.
    private static final double LINHA_DE_CHEGADA = 200.0;
    
    // Lista para armazenar os logs de progresso dos carros.
    private static List<String> logList = new ArrayList<>();
    
    // Objeto de bloqueio usado para sincronizar o acesso aos recursos compartilhados.
    private static final Object lock = new Object();
    
    // Contador atômico que rastreia a posição de chegada dos carros.
    private static AtomicInteger posicaoChegada = new AtomicInteger(1);
    
    // Lista para armazenar as instâncias dos carros que participam da corrida.
    private static List<Carro> carros = new ArrayList<>();
    
    // Semáforo para garantir que apenas um carro por vez possa cruzar a linha de chegada.
    private static Semaphore semaforo = new Semaphore(1);
    
    // CountDownLatch para sincronizar a finalização de todos os carros na corrida.
    private static CountDownLatch latch;

    // Método que é chamado quando um carro cruza a linha de chegada.
    public static void setFimDaCorrida(String nomeCarro, long tempoTotal) {
        try {
            // O carro tenta adquirir o semáforo antes de cruzar a linha de chegada.
            semaforo.acquire();
            synchronized (lock) { // Bloqueia a seção crítica para garantir que apenas um carro por vez acesse esta parte do código.
                // Obtém a posição de chegada atual e incrementa para o próximo carro.
                int posicao = posicaoChegada.getAndIncrement();
                
                // Cria uma mensagem de log que indica qual carro cruzou a linha de chegada, em quanto tempo e em que posição.
                String logEntry = nomeCarro + " alcançou a linha de chegada em " + tempoTotal + " ms e ficou em " + posicao + "º lugar";
                
                // Adiciona a mensagem de log à lista de logs e imprime-a no console.
                logList.add(logEntry);
                System.out.println(logEntry);
            }
        } catch (InterruptedException e) {
            // Trata qualquer interrupção que ocorra durante a aquisição do semáforo.
            e.printStackTrace();
        } finally {
            // Libera o semáforo para que o próximo carro possa cruzar a linha de chegada.
            semaforo.release();
            
            // Decrementa o latch, indicando que mais um carro terminou a corrida.
            latch.countDown();
        }
    }

    // Método que retorna a posição da linha de chegada.
    public static double getLinhaDeChegada() {
        return LINHA_DE_CHEGADA;
    }

    // Método que registra os logs de progresso dos carros.
    public static void log(String nomeCarro, double velocidade, double deslocamento) {
        synchronized (lock) { // Bloqueia a seção crítica para garantir a consistência dos dados.
            // Cria uma mensagem de log que indica quanto o carro andou e qual é a distância total percorrida até agora.
            String logEntry = String.format("O %s andou %.2fm e já percorreu %.2fm", nomeCarro, deslocamento, velocidade);
            
            // Adiciona a mensagem de log à lista de logs e imprime-a no console.
            logList.add(logEntry);
            System.out.println(logEntry);
        }
    }

    // Método que inicia a corrida.
    public static void iniciarCorrida() {
        // Reseta a posição de chegada para 1.
        posicaoChegada.set(1);
        
        // Limpa a lista de carros para garantir que não há dados de corridas anteriores.
        carros.clear();
        
        // Inicializa o CountDownLatch com o número de carros na corrida (15).
        latch = new CountDownLatch(15);
        
        // Cria instâncias dos carros e as adiciona à lista de carros.
        for (int i = 1; i <= 15; i++) {
            carros.add(new Carro("Carro_" + String.format("%02d", i)));
        }

        // Cria e inicia uma nova thread para cada carro.
        for (Carro carro : carros) {
            new Thread(carro).start();
        }

        try {
            // Espera até que todos os carros tenham terminado a corrida.
            latch.await();
        } catch (InterruptedException e) {
            // Trata qualquer interrupção que ocorra durante a espera.
            e.printStackTrace();
        }
        
        // Imprime uma mensagem indicando que todos os carros cruzaram a linha de chegada e que a corrida terminou.
        System.out.println("Todos os carros cruzaram a linha de chegada. A corrida terminou!");
    }

    // Método principal que inicia a corrida quando a aplicação é executada.
    public static void main(String[] args) {
        iniciarCorrida();
    }
}

