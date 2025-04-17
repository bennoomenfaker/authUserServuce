package tn.platformMedical.auth_user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageResponse {
	private String message;
	private String data;

	public MessageResponse(String message){
		this.message=message;
	}


}
