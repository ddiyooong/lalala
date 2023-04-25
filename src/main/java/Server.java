import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;
import java.util.logging.*;

public class Server {

	// 서버 소켓 및 클라이언트 연결 소켓 
	private ServerSocket  ss= null;
	private Socket s= null;
	
	// 연결된 클라이언트 스레드를 관리하기 위한 ArrayList
	ArrayList<WorkerThread> workerThreads = new ArrayList<WorkerThread>();

    // 로거 객체
    Logger logger;
		
	// 멀티챗 메인 프로그램 부
	public void start() {
        logger = Logger.getLogger(this.getClass().getName());

		try {
			// 서버 소켓 생성
			ss = new ServerSocket(8000);
			logger.info("Server started");
			
			// 무한 루프를 돌면서 클라이언트 연결을 기다림
			while(true) {
				s=ss.accept();				
				// 연결된 클라이언트에 대해 쓰레드 클래스 생성
				WorkerThread worker = new WorkerThread();
				// 클라이언트 리스트 추가
				workerThreads.add(worker);
				// 스레드 시작
				worker.start();
			}
		} catch (Exception e) {
			logger.info("[Server]start() Exception 발생");
            e.printStackTrace();
		}   
	} 

	// 연결된 모든 클라이언트에 메시지 중계
	void msgSendAll(String msg) {
		for(WorkerThread wt : workerThreads) {
			wt.outMsg.println(msg);
		}
	}

	// 각각의 클라이언트 관리를 위한 쓰레드 클래스
	class WorkerThread extends Thread {
		// 수신 메시지 및 파싱 메시지 처리를 위한 변수 선언
		String msg;

        // 메시지 객체 생성
		Message m = new Message();

        // Json Parser 초기화
		Gson gson = new Gson();

		// 입출력 스트림
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		public void run() {
		
			boolean status = true;
			logger.info("[Server]WorkerThread started");

			try {
				// 입출력 스트림 생성
				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(),true);

				// 상태정보가 true 이면 루프를 돌면서 사용자로 부터 수신된 메시지 처리
				while(status) {
					if(inMsg.ready()){
						msg = inMsg.readLine();

						// JSON 메시지를 Message 객체로 매핑
						m = gson.fromJson(msg,Message.class);

						// 파싱된 문자열 배열의 두번째 요소 값에 따라 처리
						// 로그아웃 메시지 인 경우
						if(m.getType().equals("logout")) {
							workerThreads.remove(this);
							msgSendAll(gson.toJson(new Message("server", "msg", m.getId()+"님이 종료 했습니다.",null)));
							// 해당 클라이언트 스레드 종료로 인해 status 를 false 로 설정
							status = false;
						}
						// 로그인 메시지인 경우
						else if(m.getType().equals("login")) {
							msgSendAll(gson.toJson(new Message("server","msg", m.getId()+"님이 로그인 했습니다.",null)));
						}
						// 파일 전송 메세지인 경우
						else if(m.getType().equals("filePOST")) {
							msgSendAll(msg);
						}
					}

				}
				// 루프를 벗어나면 클라이언트 연결 종료이므로 스레드 인터럽트
				this.interrupt();
				logger.info(this.getName() + " 종료됨");
			} catch (IOException e) {
				workerThreads.remove(this);
				logger.info("[WorkerThread] run() IOException 발생");
                e.printStackTrace();
			}
		}
	}

    public static void main(String[] args){
        Server server = new Server();
        server.start();
    }
}