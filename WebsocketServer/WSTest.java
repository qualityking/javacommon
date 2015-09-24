package socketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

public class WSTest {
	public static void main(String[] args) throws IOException,
			InterruptedException, NoSuchAlgorithmException {
		
		WebsocketServer j = new WebsocketServer(8081);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Write something to the client!");
			j.sendMessage(br.readLine().getBytes());
		}
	}
}
