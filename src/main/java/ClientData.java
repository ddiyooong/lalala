import javax.swing.*;

public class ClientData {
    // 데이터 제공 객체
    JTextArea msgOut;
    


//JComponent 객체를 파라미터로 받아 데이터 변화에 대응 처리
    public void addObj(JComponent comp) {
        this.msgOut = (JTextArea)comp;
    }


//데이터 변화가 발생했을때 UI 에 데이터 변경을 반영하기 위한 메서드
    public void refreshData(String msg) {
        //JTextArea 에 수신된 메시지 추가
        msgOut.append(msg);
    }
}
