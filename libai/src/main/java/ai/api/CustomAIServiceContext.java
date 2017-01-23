package ai.api;

public class CustomAIServiceContext implements AIServiceContext{

	private String sessionId;

	public CustomAIServiceContext(String sessionId){
		this.sessionId = sessionId;
	}
	
	@Override
	public String getSessionId() {
		return this.sessionId;
	}
	
}
