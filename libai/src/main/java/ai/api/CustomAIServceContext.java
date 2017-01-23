package ai.api;

public class CustomAIServceContext implements AIServiceContext{

	private String sessionId;

	public CustomAIServceContext(String sessionId){
		this.sessionId = sessionId;
	}
	
	@Override
	public String getSessionId() {
		return this.sessionId;
	}
	
}
