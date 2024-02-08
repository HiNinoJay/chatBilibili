package top.nino.chatbilibili.service;


/**
 * @author nino
 */
public interface ClientService {
	void loadRoomInfoAndOpenWebSocket(Long roomId) throws Exception;
	void reConnService() throws Exception;
	boolean closeConnService();

	void startReceiveDanmuThread();

	void closeByUserLogOut();

	void closeConnection();
}
