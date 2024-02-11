package top.nino.chatbilibili.service;


/**
 * @author nino
 */
public interface ClientService {
	void loadRoomInfoAndOpenWebSocket(Long roomId) throws Exception;
	void reConnService() throws Exception;
	void startReceiveDanmuThread();
	void startAIThread();
	void closeConnection();
}
