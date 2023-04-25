public class Message {
	private String id;
	private String type;
	private String msg;
	private String file;
	
	public Message() {}
	
	public Message(String id, String type, String msg,  String file) {
		this.id = id;
		this.msg = msg;
		this.file = file;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFile(){ return file; }
	public void setFile(String file){ this.file = file;}
}
