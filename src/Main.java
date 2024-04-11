import projeto.sd.Cliente;
import projeto.sd.Server;

import java.io.IOException;


public class Main {



        public static void main(String[] args) throws IOException {
            Server server = new Server();
            Cliente cliente = new Cliente("localhost", 8080);

            enviarArquivoClienteParaServidor(server, cliente);
            fazerDownloadArquivoDoServidor(server, cliente);
            excluirArquivoServidor(server, cliente);
        }

        static void enviarArquivoClienteParaServidor(Server server, Cliente cliente) throws IOException {
            //Inicia conexão
            server.realizarOperacao(Server.Operacao.INICIAR_CONEXAO);
            cliente.realizarConexao();

            //Realiza Operação
            cliente.enviar("src/main/resources/distribuidos/sistemas/projetoprimeiranota/imagem.jpg");
            Server.realizarOperacao(Server.Operacao.DOWNLOAD);

            //Encerra conexão
            server.realizarOperacao(Server.Operacao.ENCERRAR_CONEXAO);
            cliente.encerrarConexao();
        }

        static void fazerDownloadArquivoDoServidor(Server server, Cliente cliente) throws IOException {
            //Inicia conexão
            server.realizarOperacao(Server.Operacao.INICIAR_CONEXAO);
            cliente.realizarConexao();

            //Realiza operação
            cliente.exigirDownload("imagem.jpg.jpg");
            server.realizarOperacao(Server.Operacao.UPLOAD);
            cliente.receberDownload("src/main/resources/distribuidos/sistemas/projetoprimeiranota/imagem.jpg");

            //Encerra conexão
            server.realizarOperacao(Server.Operacao.ENCERRAR_CONEXAO);
            cliente.encerrarConexao();
        }

        static void excluirArquivoServidor(Server servidor, Cliente cliente) throws IOException {
            //Inicia conexão
            servidor.realizarOperacao(Server.Operacao.INICIAR_CONEXAO);
            cliente.realizarConexao();

            //Realiza Operação
            cliente.excluir("imagem.jpg");
            servidor.realizarOperacao(Server.Operacao.DELETE);

            //Encerra conexão
            servidor.realizarOperacao(Server.Operacao.ENCERRAR_CONEXAO);
            cliente.encerrarConexao();
        }
    }

