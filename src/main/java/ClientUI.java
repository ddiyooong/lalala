import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ClientUI extends JFrame{

    // 로그인 패널
    private JPanel loginPanel;
    // 로그인 버튼
    protected JButton loginButton;

    // 대화명 라벨
    private JLabel inLabel;
    // 대화명 출력 라벨
    protected JLabel outLabel;
    // 대화명 입력 텍스트필드
    protected JTextField idInput;

    // 로그아웃 패널
    private JPanel logoutPanel;
    // 로그아웃 버튼
    protected JButton logoutButton;

    //파일 열기 + 종료 버튼 패널
    private JPanel msgPanel;
	protected JFileChooser chooser;
	protected JButton openFileBtn;
    // 종료 버튼
    protected JButton exitButton;

    // 내용 출력창
    protected JTextArea msgOut;
    protected JScrollPane jsp;

    // 화면 구성 전환을 위한 카드레이아웃
    protected Container tab;
    protected CardLayout cardLayout;

    // 클라이언트 로그인 아이디
    protected String id;
    
    
    // 생성자
    public ClientUI() {

        // 메인 프레임 구성
    	super("::Client::");
    	this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);


        // 로그인 패널 화면 구성 (JPanel)
    	loginPanel = new JPanel();
    	
        // 로그인 패널 레이아웃 설정
    	loginPanel.setLayout(new BorderLayout(5,0));
    	loginPanel.setBorder(BorderFactory.createEmptyBorder(4, 3, 3, 3));
    	
        // 로그인 입력필드 버튼 생성
    	loginButton = new JButton("로그인");
    	inLabel = new JLabel("대화명");
    	idInput = new JTextField();
    	loginButton.setBackground(Color.white);
    	
    	
        // 로그인 패널에 위젯 구성 (add메소드이용하여 위에서 생성한 필드/버튼 등 패널에 추가)
    	loginPanel.add(inLabel, BorderLayout.WEST);
    	loginPanel.add(idInput, BorderLayout.CENTER);
    	loginPanel.add(loginButton, BorderLayout.EAST);
    	
    	
        // 로그아웃 패널 구성 (JPanel)
    	logoutPanel = new JPanel();
    	logoutButton = new JButton("로그아웃");
    	logoutButton.setBackground(Color.white);
    	
    	outLabel = new JLabel();
    	
    	
        // 로그아웃 패널 레이아웃 설정
    	logoutPanel.setLayout(new BorderLayout(3,0));
    	logoutPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    	
        // 로그아웃 패널에 위젯 구성
    	logoutPanel.add(outLabel, BorderLayout.WEST);
    	logoutPanel.add(logoutButton, BorderLayout.EAST);
    	
        // 메시지 입력 패널 구성 (JPanel)
    	msgPanel = new JPanel();

        // 레이아웃 설정
    	msgPanel.setLayout(new FlowLayout());

        // 메시지 입력 패널에 위젯 구성
    	//msgInput = new JTextField(45);
    	exitButton = new JButton("종료");
    	exitButton.setBackground(Color.white);
		openFileBtn = new JButton("파일 열기");
		openFileBtn.setBackground(Color.white);
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);

    	//msgPanel.add(msgInput);
    	msgPanel.add(exitButton);
		msgPanel.add(openFileBtn);
    	
    	
        // 로그인/로그아웃 패널 선택을 위한 CardLayout 패널 
    	tab = new Container();
    	cardLayout = new CardLayout();
    	tab.setLayout(cardLayout);
    	tab.add("login", loginPanel);
    	tab.add("logout", logoutPanel);

        // 메시지 출력 영역 초기화
    	msgOut = new JTextArea();

        // JTextArea 의 내용을 수정하지 못하도록 함. 즉 출력전용으로 사용
    	msgOut.setEditable(false);

        // 메시지 출력(msgOut) 영역 스크롤바 구성, 수직 스크롤바는 항상 나타내고 수평 스크롤바는 필요시 나타나도록 함. (JScrollPane)
    	jsp = new JScrollPane(msgOut, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	
        // 메인 프레임에 패널 배치 (tab, jsp, msgPanel)
    	this.add(tab, BorderLayout.NORTH);
    	this.add(jsp, BorderLayout.CENTER);
    	this.add(msgPanel, BorderLayout.SOUTH);

        // loginPanel 을 우선 보이도록 함.
    	cardLayout.show(tab, "login");    	
    	
        // 프레임 크기 자동으로 설정
    	this.setSize(600, 300);
    	
        // 프레임 크기 조정 불가 설정
    	this.setResizable(false);
    	
        // 프레임이 보여지도록 함
    	this.setVisible(true);
    }

    /**
     * 이벤트 리스너 등록을 위한 메서드로 파라미터의 리스너 객체는 컨트롤러에서 구현한 객체가 됨.
     * 따라서 실제 이벤트 처리는 컨트롤러 클래스 코드를 따라감.
     */
    public void addButtonActionListener(ActionListener listener) {
        // 이벤트 리스너 등록
    	loginButton.addActionListener(listener);
    	logoutButton.addActionListener(listener);
    	exitButton.addActionListener(listener);
		openFileBtn.addActionListener(listener);
    	
    }
    

}
