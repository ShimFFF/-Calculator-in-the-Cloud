//Java를 사용하여 클라이언트-서버 기반의 네트워크 계산기 프로그램의 Server class
// 수식을 서버로 전송
//덧셈, 뺄셈, 곱셈, 나눗셈을 포함하는 네 가지 산술 연산을 구현
//ThreadPool 및 Runable 인터페이스 사용 (시에 여러 클라이언트를 처리할 수 있도록 함)


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcServer {

    // 수식을 계산하는 메소드
    public static String calc(String exp) {
        StringTokenizer st = new StringTokenizer(exp, " ");
        if (st.countTokens() != 3) return "Error: Invalid expression format";

        try {
            int op1 = Integer.parseInt(st.nextToken());
            String opcode = st.nextToken();
            int op2 = Integer.parseInt(st.nextToken());

            switch (opcode) {
                case "+":
                    return Integer.toString(op1 + op2);
                case "-":
                    return Integer.toString(op1 - op2);
                case "*":
                    return Integer.toString(op1 * op2);
                case "/":
                    if (op2 == 0) {
                        return "Error: Division by zero"; // 0으로 나누는 경우
                    } else {
                        return Integer.toString(op1 / op2);
                    }
                case "bye":
                    return "bye";
                default:
                    return "Error: Invalid operator"; // 잘못된 연산자인 경우
            }
        } catch (NumberFormatException e) {
            return "Error: Invalid expression format"; // 수식이 정수가 아닌 경우
        }
    }

    public static void main(String[] args) {
        ServerSocket listener = null;

        try {
            listener = new ServerSocket(9999);
            System.out.println("클라이언트 연결을 기다리고 있습니다...");

            ExecutorService pool = Executors.newFixedThreadPool(10); // ThreadPool 생성

            while (true) {
                Socket socket = listener.accept(); // 클라이언트 연결 수락
                System.out.println("클라이언트와 연결되었습니다.");

                Runnable task = new HandleClient(socket); // 클라이언트와 통신하는 스레드 생성
                pool.execute(task); // ThreadPool을 통해 클라이언트 요청을 병렬로 처리
            }
        } catch (Exception e) {
            System.out.println("오류: " + e.getMessage());
        } finally {
            try {
                if (listener != null) listener.close();
            } catch (Exception e) {
                System.out.println("서버 소켓 닫는 중 오류 발생");
            }
        }
    }
}


class HandleClient implements Runnable {
    private final Socket socket; // 통신을 위한 소켓

    // 생성자
    public HandleClient(Socket socket) {
        this.socket = socket;
    }

    // 클라이언트와 통신하는 메소드
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 클라이언트로부터 수신 스트림
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {// 클라이언트로 송신 스트림

            while (true) {
                String inputMessage = in.readLine(); // 클라이언트로부터 수식 수신
                if (inputMessage.equalsIgnoreCase("bye")) {
                    System.out.println("클라이언트에서 연결을 종료하였습니다.");
                    out.write("bye");
                    out.flush();
                    break;
                }
                System.out.println("수신한 수식: " + inputMessage);

                String res = CalcServer.calc(inputMessage);
                System.out.println("계산 결과: " + res);

                out.write(res + "\n"); // 계산 결과 전송
                out.flush(); // out의 스트림 버퍼에 있는 모든 문자열 전송(반영)
            }
        } catch (Exception e) {
            System.out.println("클라이언트와 통신 중 오류 발생: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("클라이언트 소켓 닫는 중 오류 발생: " + e.getMessage());
            }
        }
    }
}
