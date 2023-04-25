import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

import javax.swing.*;

public class ClientController implements Runnable {

	// 뷰 클래스 참조
	private ClientUI v;

	// 데이터 클래스 참조
	private final ClientData clientData;

	// 소켓 연결을 위한 변수
	private InetAddress ip;
	private Socket socket;
	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;

	// 메시지 파싱을 위한 객체 생성
	Gson gson = new Gson();
	Message m;

	// 상태 플래그
	boolean status;

	// 로거 객체
	Logger logger;

	// 메시지 수신 스레드
	Thread thread;


	//생성자
	public ClientController(ClientData clientData, ClientUI v) {
		// 로거 객체 초기화
		this.logger = Logger.getLogger("log");

		//UI, data 객체 초기화
		this.v = v;
		this.clientData = clientData;

		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 어플리케이션 메인 실행 메서드
	 */
	public void appMain() {

		// data 객체에서 데이터 변화를 처리할 UI 객체 추가
		clientData.addObj(v.msgOut);

		//UI 이벤트 리스너
		v.addButtonActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				// 종료버튼, 로그인버튼, 로그아웃버튼, 메시지전송버튼(엔터) 처리
				if (obj == v.exitButton) {
					if(!status){
						System.exit(0);
						return;
					}

					status=false;
					thread.interrupt();
					outMsg.println(gson.toJson(new Message(v.id, "logout","", null)));
					try {
						if (socket != null && !socket.isClosed()) {
							socket.close();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					logger.info("Client <" + v.id + ">  종료됨");
					System.exit(0);

				} else if (obj == v.loginButton) {
					v.id = v.idInput.getText();
					v.outLabel.setText("CLIENT ID : " + v.id);
					v.cardLayout.show(v.tab, "logout");
					// 서버에 연결
					connectServer();

				} else if (obj == v.logoutButton) {
					status = false;
					
					// 로그아웃 메시지 전송
					outMsg.println(gson.toJson(new Message(v.id,"logout","", null)));
					// 대화창 클리어
					v.msgOut.setText("");

					// 로그인 패널로 전환 및 소켓/스트림 닫기 + status 업데이트
					v.cardLayout.show(v.tab, "login");
					status = false;
					try {
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					thread.interrupt();

				} else if (obj == v.openFileBtn) {
					// 입력된 메시지 전송
					if(!status){
						JOptionPane.showMessageDialog(v, "로그인 필요");
						return;
					}

					try {
						if(v.chooser.showOpenDialog(v) == JFileChooser.APPROVE_OPTION){
							File[] files = v.chooser.getSelectedFiles();

							for(File file : files){
								logger.info(file.getAbsolutePath());
								FileInputStream fis = new FileInputStream(file);
								byte[] data = new byte[(int) file.length()];
								fis.read(data);
								fis.close();


								String fileContent = new String(data, "UTF-8");

								Message msg = new Message(v.id, "filePOST", file.getName(), fileContent);
								outMsg.println(gson.toJson(msg));
								logger.info(gson.toJson(msg));
							}
						}
					}catch (IOException e1){
						logger.info("File POST IOException!!");
						e1.printStackTrace();
					}

				}
			}
		});
	}


	 //서버 접속 함수
	public void connectServer() {
		try {
			// 소켓 생성
			socket = new Socket(ip, 8000);
			logger.log(Level.INFO, "Success to connect server");

			// 입출력(inMsg, outMsg) 스트림 생성
			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outMsg = new PrintWriter(socket.getOutputStream(), true);

			// 서버에 로그인 메시지 전달
			outMsg.println(gson.toJson(new Message(v.id, "login", "", null)));

			// 메시지 수신을 위한 스레드(thread) 생성 및 스타트
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			logger.log(Level.WARNING, "connectServer() Exception 발생!!");
			e.printStackTrace();
		}
	}

	/**
	 * 메시지 수신을 독립적으로 처리하기 위한 스레드 실행
	 */
	public void run() {
		// 수신 메시지
		String msg;

		// status 업데이트
		this.status = true;

		while (status) {
			try {
				if(inMsg.ready()){
					// 메시지 수신
					msg = inMsg.readLine();

					// 메시지 파싱
					m = gson.fromJson(msg, Message.class);

					// Data 객체를 통해 데이터 갱신
					clientData.refreshData(m.getId() + ">" + m.getMsg() + "\n");

					// 커서를 현재 대화 메시지에 보여줌
					v.msgOut.setCaretPosition(v.msgOut.getDocument().getLength());

				}

				Thread.sleep(100);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Client Controller IOException! (run)");
				e.printStackTrace();
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Client Controller InterruptedException! (run)");
				e.printStackTrace();
			}
		}
		logger.info("Client Controller" + thread.getName() + " 메시지 수신 스레드 종료");
	}

	// 프로그램 시작을 위한 메인 메서드
	public static void main(String[] args) {
		// Controller 객체생성 및 appMain() 실행
		ClientController controller = new ClientController(new ClientData(), new ClientUI());
		controller.appMain();

	}
}
