package com.amoa.server.domain.auth.controller.docs;

import com.amoa.server.domain.auth.dto.request.AuthDevReqDTO;
import com.amoa.server.domain.auth.dto.response.AuthDevResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth(Dev)", description = "개발자 전용 API")
public interface AuthDevControllerDocs {

    @Operation(
            summary = "개발자용 Access Token 발급",
            description = """
                    개발자 인증 키와 테스트 사용자 ID를 전달받아
                    24시간 동안 사용할 수 있는 개발자용 Access Token을 발급합니다.
                    
                    발급된 토큰은 일반 Access Token과 동일하게
                    Authorization 헤더에 Bearer 형식으로 전달합니다.
                    
                    개발자용으로 허용된 사용자만 토큰을 발급받을 수 있습니다.
                    """
    )
    ApiResponse<AuthDevResDTO.DevTokenResponse> issueDevToken(
            AuthDevReqDTO.DevTokenRequest request
    );
}