import java.io.*;
import java.net.Socket;
import java.util.Scanner;

//Java를 사용하여 클라이언트-서버 기반의 네트워크 계산기 프로그램의 client class
// 수식을 서버로 전송
//덧셈, 뺄셈, 곱셈, 나눗셈을 포함하는 네 가지 산술 연산을 구현
public class CalcClient {
    public static void main(String[] args) {

        BufferedReader in = null;
        BufferedWriter out = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);

        try{
            socket= new Socket("localhost", 9999);
            in= new BufferedReader(new InputStreamReader(socket.getInputStream())); // 서버로부터 수신 스트림
            out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 서버로 송신 스트림

            while (true){
                System.out.println("수식을 입력하세요. (ex. 3 + 4)>>");
                String outputMessage = scanner.nextLine(); // 키보드에서 수식을 입력받음
                out.write(outputMessage+"\n"); // 수식 전송
                out.flush(); // out의 스트림 버퍼에 있는 모든 문자열 전송(반영)
                String inputMessage = in.readLine(); // 서버로부터 계산 결과 수신
                if(inputMessage.equalsIgnoreCase("bye")){ // 서버가 "bye"를 보내면 연결 종료
                    break;
                }
                System.out.println("계산 결과: "+inputMessage);
            }
        }catch (IOException e){ // 입출력 예외 처리
            System.out.println(e.getMessage());
        }finally {
            try{
                scanner.close();
                if(socket != null) socket.close(); // 클라이언트 소켓 닫기
            }catch (IOException e){
                System.out.println("클라이언트 종료");
            }
        }


    }
}
