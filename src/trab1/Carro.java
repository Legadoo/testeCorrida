package trab1;

import java.util.Random;

public class Carro implements Runnable {
    private String nome;
    private double velocidade;
    private double deslocamento;
    private double aceleracao;
    private static final double TEMPO_FRAME = 1.0; // Em segundos
    private long tempoInicial;

    public Carro(String nome) {
        this.nome = nome;
        this.velocidade = 0;
        this.deslocamento = 0;
    }

    @Override
    public void run() {
        Random random = new Random();
        tempoInicial = System.currentTimeMillis();
        
        while (deslocamento < Corrida.getLinhaDeChegada()) {
            aceleracao = random.nextDouble() * 10; // Gera uma aceleração aleatória entre 0 e 10 m/s²
            velocidade += aceleracao * TEMPO_FRAME;
            deslocamento += velocidade * TEMPO_FRAME;

            Corrida.log(nome, velocidade, deslocamento);

            // Verifica se o carro cruzou a linha de chegada
            if (deslocamento >= Corrida.getLinhaDeChegada()) {
                Corrida.setFimDaCorrida(nome, System.currentTimeMillis() - tempoInicial);
            }

            try {
                Thread.sleep(100); // Pausa de 100 ms entre cada iteração (simulando o tempo entre frames)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
