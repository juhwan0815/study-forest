package com.study.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dto.KakaoProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(value = KakaoClientImpl.class)
class KakaoClientTest {

    @Autowired
    private KakaoClientImpl kakaoClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카카오에서 회원프로필을 가져온다.")
    void getKakaoProfile() throws JsonProcessingException {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "이미지", "이미지");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19", "male");

        KakaoProfile kakaoProfile = new KakaoProfile(1L, properties, kakaoAccount);

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(kakaoProfile), MediaType.APPLICATION_JSON));

        KakaoProfile result = kakaoClient.getKakaoProfile("kakaoToken");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getKakao_account().getAge_range()).isEqualTo(kakaoAccount.getAge_range());
        assertThat(result.getKakao_account().getGender()).isEqualTo(kakaoAccount.getGender());
        assertThat(result.getProperties().getNickname()).isEqualTo(properties.getNickname());
        assertThat(result.getProperties().getProfile_image()).isEqualTo(properties.getProfile_image());
        assertThat(result.getProperties().getThumbnail_image()).isEqualTo(properties.getThumbnail_image());
    }

    @Test
    @DisplayName("예외 테스트 : 200 응답이 아닐 경우 예외가 발생한다.")
    void ifNotSuccessResponse() throws JsonProcessingException {

        // given
        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED));

        // when
        assertThrows(RuntimeException.class, () -> kakaoClient.getKakaoProfile("kakaoToken"));
    }


}