
package af.asr.notification.domain;

import af.gov.anar.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SMSConfiguration {
	@ValidIdentifier
	@Length(max = 45)
	private String identifier;
	@Length(max = 255)
	private String auth_token;
	@Length(max = 255)
	private String account_sid;
	@Length(max = 45)
	private String sender_number;
	@Length(max = 20)
	private String state;
	
	public SMSConfiguration() {
		super();
	}
	
	public static SMSConfiguration create(String identifier,
	                                      String auth_token,
	                                      String account_sid,
	                                      String sender_number,
	                                      String state) {
		SMSConfiguration smsconfiguration = new SMSConfiguration();
		smsconfiguration.setIdentifier(identifier);
		smsconfiguration.setAuth_token(auth_token);
		smsconfiguration.setAccount_sid(account_sid);
		smsconfiguration.setSender_number(sender_number);
		smsconfiguration.setState(state);
		return smsconfiguration;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getAuth_token() {
		return auth_token;
	}
	
	public void setAuth_token(String auth_token) {
		this.auth_token = auth_token;
	}
	
	public String getAccount_sid() {
		return account_sid;
	}
	
	public void setAccount_sid(String account_sid) {
		this.account_sid = account_sid;
	}
	
	public String getSender_number() {
		return sender_number;
	}
	
	public void setSender_number(String sender_number) {
		this.sender_number = sender_number;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getType() {
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SMSConfiguration that = (SMSConfiguration) o;
		return Objects.equals(identifier, that.identifier) &&
				Objects.equals(auth_token, that.auth_token) &&
				Objects.equals(account_sid, that.account_sid) &&
				Objects.equals(state, that.state);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(identifier, auth_token, auth_token, account_sid, state);
	}
	
	@Override
	public String toString() {
		return "SMSConfiguration{" +
				"identifier='" + identifier + '\'' +
				", auth_token='" + auth_token + '\'' +
				", account_sid='" + account_sid + '\'' +
				", sender_number='" + sender_number + '\'' +
				", state='" + state + '\'' +
				'}';
	}
	
	private enum State {
		ACTIVE,
		DEACTIVATED;
		
		State() {
		}
	}
}
