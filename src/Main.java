import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    private static final int REAL_BRASILEIRO = 1;
    private static final int DOLAR_AMERICANO = 2;
    private static final int PESO_ARGENTINO = 3;
    private static final int PESO_COLOMBIANO = 4;
    private static final int SAIR = 5;

    public static void main(String[] args) {
        boolean sair = false;
        Scanner scanner = new Scanner(System.in);

        while (sair == false) {
            int opcao1 = mostrarMenu(scanner, "Escolha a moeda que deseja converter:");
            if (opcao1 == SAIR) {
                sair = true;
                break;
            }

            int opcao2;
            do {
                opcao2 = mostrarMenu(scanner, "Escolha para qual moeda deseja converter:");
                if (opcao2 == SAIR) {
                    sair = true;
                    break;
                }

                if (opcao1 == opcao2) {
                    System.out.println("Não é possível converter uma moeda para a mesma moeda. Tente novamente.");
                }
            } while (opcao1 == opcao2);

            if (sair) {
                break;
            }


            System.out.println("DE: " + opcaoToString(opcao1));
            System.out.println("PARA: " + opcaoToString(opcao2));

            System.out.println("QUAL O VALOR QUE DESEJA CONVERTER? (EX: 75.50)");
            double valor = scanner.nextDouble();



            double valorConvertido = converterMoeda(opcao1, opcao2, valor);
            System.out.println("Valor convertido: " + valorConvertido);
        }
        scanner.close();
    }

    private static int mostrarMenu(Scanner scanner, String mensagem) {
        System.out.println(mensagem);
        System.out.println("1. Real brasileiro");
        System.out.println("2. Dolar americano");
        System.out.println("3. Peso argentino");
        System.out.println("4. Peso colombiano");
        System.out.println("5. Sair | Encerrar programa");
        return scanner.nextInt();
    }

    private static String opcaoToString(int opcao) {
        switch (opcao) {
            case REAL_BRASILEIRO:
                return "REAL BRASILEIRO";
            case DOLAR_AMERICANO:
                return "DOLAR AMERICANO";
            case PESO_ARGENTINO:
                return "PESO ARGENTINO";
            case PESO_COLOMBIANO:
                return "PESO COLOMBIANO";
            default:
                return "Opção inválida";
        }
    }

    private static double converterMoeda(int de, int para, double valor) {
        try {
            String codigoMoedaDe = pegarSigla(de);
            String codigoMoedaPara = pegarSigla(para);

            String apiKey = "a4f264f5505faabe9c4219e2";
            String url_str = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + codigoMoedaDe;

            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            String req_result = jsonobj.get("result").getAsString();
            if (!"success".equals(req_result)) {
                System.out.println("Erro ao obter a taxa de conversão.");
                return 0;
            }

            JsonObject conversionRates = jsonobj.getAsJsonObject("conversion_rates");
            double taxaDeConversao = conversionRates.get(codigoMoedaPara).getAsDouble();

            return valor * taxaDeConversao;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return 0;
        }
    }

    private static String pegarSigla(int opcao) {
        switch (opcao) {
            case REAL_BRASILEIRO:
                return "BRL";
            case DOLAR_AMERICANO:
                return "USD";
            case PESO_ARGENTINO:
                return "ARS";
            case PESO_COLOMBIANO:
                return "COP";
            default:
                return "";
        }
    }
}