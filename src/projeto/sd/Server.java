package projeto.sd;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
    public enum Operacao {
        INICIAR_CONEXAO,
        ENCERRAR_CONEXAO,
        UPLOAD,
        DOWNLOAD,
        DELETE
    }

    private final ServerSocket servidorSocket = new ServerSocket(8080);
    private Socket socket = null;

    public Server() throws IOException {
    }

    public static void realizarOperacao(Operacao operacao) {
        Thread thread = new Thread(
                () -> {
                    try {
                        switch (operacao) {
                            case INICIAR_CONEXAO -> iniciarConexao();
                            case ENCERRAR_CONEXAO -> encerrarConexao();
                            case UPLOAD -> {
                                try {
                                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                    String nomeArquivo = dataInputStream.readUTF();

                                    dataInputStream.close();

                                    if (listarArquivosSalvos() != null) {
                                        for (File arquivo : listarArquivosSalvos()) {
                                            if (nomeArquivo.equals(arquivo.getName())) enviar(arquivo.getPath());
                                            break;
                                        }
                                    }
                                    else {
                                        System.out.println("Diretório de arquivos salvos não existe ou está vazio!");
                                        return;
                                    }
                                } catch (NullPointerException error) {
                                    System.out.println("Falha ao inserir arquivo!");
                                    System.err.println(error);
                                }
                            }
                            case DOWNLOAD -> receber("src/main/resources/sistemas/distribuidos/projetoprimeiranota/arquivos/");
                            case DELETE -> excluir();
                        }
                    } catch (IOException error) {
                        System.out.println("Operação falhou!");
                        System.err.println(error);
                        return;
                    }
                    System.out.println("Operação realizada com sucesso!");
                }
        );

        thread.start();
    }

    private synchronized void iniciarConexao() throws IOException {
        socket = servidorSocket.accept();
        System.out.println("Conexão iniciada com sucesso!");
    }

    private synchronized void encerrarConexao() throws IOException {
        try {
            servidorSocket.close();
            socket.close();
            System.out.println("Conexão encerrada com sucesso!");
        } catch (NullPointerException error) {
            System.out.println("Falha ao encerrar a conexão!");
            System.err.println(error);
        }
    }

    private synchronized void enviar(String ArquivoEntrada) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(ArquivoEntrada);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Arquivo inserido com sucesso!");

            dataOutputStream.writeUTF(new File(ArquivoEntrada).getName());

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }

            dataOutputStream.flush();
            dataOutputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException | NullPointerException error) {
            System.out.println("Falha ao inserir arquivo!");
            System.err.println(error);
        }
    }

    private synchronized void receber(String ArquivoSaida) throws IOException {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String nomeArquivo = dataInputStream.readUTF();
            FileOutputStream fileOutputStream = new FileOutputStream(ArquivoSaida + nomeArquivo);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            dataInputStream.close();
            System.out.println("Arquivo recebido com sucesso!");
        } catch (FileNotFoundException | NullPointerException error) {
            System.out.println("Falha ao receber arquivo!");
            System.err.println(error);
        }
    }

    private synchronized void excluir() throws IOException {
        InputStream inputStream;

        try {
            inputStream = socket.getInputStream();
        } catch (NullPointerException error) {
            System.out.println("Falha ao excluir arquivo!");
            System.err.println(error);
            return;
        }

        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder caminhoArquivo = new StringBuilder();

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            caminhoArquivo.append(new String(buffer, 0, bytesRead));
        }

        File file = new File(caminhoArquivo.toString());

        if (listarArquivosSalvos() != null) {
            for (File arquivo : listarArquivosSalvos()) {
                if (file.equals(arquivo)) file.delete();
            }
        }
        else {
            System.out.println("Diretório de arquivos salvos não existe ou está vazio!");
            return;
        }
        System.out.println("Arquivo excluído com sucesso!");

        inputStream.close();
    }

    private synchronized File[] listarArquivosSalvos() {
        String caminhoPadrao = "src/main/resources/sistemas/distribuidos/projetoprimeiranota/arquivos/";
        File diretorio = new File(caminhoPadrao);

        if(diretorio.exists() && diretorio.isDirectory()) {
            System.out.println("Listagem de arquivos realizada com sucesso!");
            return diretorio.listFiles();
        } else {
            System.out.println("Falha ao listar arquivos!");
            return null;
        }
    }
}

