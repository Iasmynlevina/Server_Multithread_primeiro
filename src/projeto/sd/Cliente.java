package projeto.sd;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Cliente {
    private final String enderecoHost;
    private final int porta;
    private Socket socket = null;

    public Cliente(String enderecoHost, int porta) {
        this.enderecoHost = enderecoHost;
        this.porta = porta;
    }

    public void realizarConexao() throws IOException {
        try {
            socket = new Socket(this.enderecoHost, this.porta);
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (UnknownHostException error) {
            System.out.println("Falha ao estabelecer conexão!");
            System.err.println(error);
        }
    }

    public void encerrarConexao() throws IOException {
        try {
            socket.close();
            System.out.println("Conexão encerrada com sucesso!");
        } catch (NullPointerException error) {
            System.out.println("Falha ao encerrar conexão!");
            System.err.println(error);
        }
    }

    public void enviar(String caminhoArquivoEntrada) throws IOException {
        FileInputStream fileInputStream;
        DataOutputStream dataOutputStream;

        try {
            fileInputStream = new FileInputStream(caminhoArquivoEntrada);
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            System.out.println("Envio do arquivo bem-sucedido!");
        } catch (FileNotFoundException | NullPointerException error) {
            System.out.println("Falha ao enviar arquivo!");
            System.err.println(error);
            return;
        }

        dataOutputStream.writeUTF(new File(caminhoArquivoEntrada).getName());

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        dataOutputStream.flush();
        dataOutputStream.close();
        fileInputStream.close();
    }

    public void exigirDownload(String nomeArquivo) throws IOException {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(nomeArquivo);

            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (NullPointerException error) {
            System.out.println("Conexão com servidor não encontrada");
            System.err.println(error);
        }
    }

    public void receberDownload(String caminhoArquivoSaida) throws IOException {
        try {
            DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());
            String nomeArquivo = dataInputStream.readUTF();
            FileOutputStream fileOutputStream = new FileOutputStream(caminhoArquivoSaida + nomeArquivo);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            dataInputStream.close();
            System.out.println("Recebimento do arquivo bem-sucedido!");
        } catch (FileNotFoundException | NullPointerException error) {
            System.out.println("Falha ao receber arquivo!");
            System.err.println(error);
        }
    }

    public void excluir(String nomeArquivo) throws IOException {
        OutputStream outputStream;

        try {
            outputStream = socket.getOutputStream();
        } catch (NullPointerException error) {
            System.out.println("Falha ao excluir arquivo!");
            System.err.println(error);
            return;
        }

        byte[] bytes = nomeArquivo.getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
