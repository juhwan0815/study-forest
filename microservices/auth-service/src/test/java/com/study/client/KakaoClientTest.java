package com.study.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.AuthFixture;
import com.study.exception.NetworkException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static com.study.AuthFixture.*;
import static com.study.client.KakaoClientImpl.KAKAO_PROFILE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(value = KakaoClientImpl.class)
class KakaoClientTest {

    @Autowired
    private KakaoClientImpl kakaoClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카카오 회원프로필을 가져온다.")
    void getKakaoProfile() throws JsonProcessingException {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "이미지", "이미지");
        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19", "male");
        KakaoProfile kakaoProfile = new KakaoProfile(1L, properties, kakaoAccount);

        mockServer.expect(requestTo(KAKAO_PROFILE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(kakaoProfile), MediaType.APPLICATION_JSON));

        // when
        KakaoProfile result = kakaoClient.getKakaoProfile(TEST_KAKAO_TOKEN);

        // then
        assertThat(result.getId()).isEqualTo(kakaoProfile.getId());
        assertThat(result.getKakao_account().getAge_range()).isEqualTo(kakaoAccount.getAge_range());
        assertThat(result.getKakao_account().getGender()).isEqualTo(kakaoAccount.getGender());
        assertThat(result.getProperties().getNickname()).isEqualTo(properties.getNickname());
        assertThat(result.getProperties().getProfile_image()).isEqualTo(properties.getProfile_image());
        assertThat(result.getProperties().getThumbnail_image()).isEqualTo(properties.getThumbnail_image());
    }

    @Test
    @DisplayName("카카오 서버가 장애가 나면 예외가 발생한다.")
    void getKakaoProfileNetworkError() throws JsonProcessingException {
        // given
        mockServer.expect(requestTo(KAKAO_PROFILE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED));

        // when
        assertThrows(NetworkException.class, () -> kakaoClient.getKakaoProfile(TEST_KAKAO_TOKEN));
    }


}