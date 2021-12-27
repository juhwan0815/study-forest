package com.study.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.domain.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ImageUtilTest {

    @InjectMocks
    private ImageUtil imageUtil;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    @DisplayName("스터디 이미지를 삭제하고 이미지를 업로드한다.")
    void uploadImage() throws Exception {

        // given
        MockMultipartFile file = new MockMultipartFile("image", "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

        Image image = Image.createImage("이미지 URL", "이미지 저장 이름");

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(), any());

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(), any()))
                .willReturn(new URL("http:이미지 URL"));
        // when
        Image result = imageUtil.uploadImage(file, image);

        // then
        assertThat(result.getImageUrl()).isEqualTo("http:이미지 URL");
        assertThat(result.getImageStoreName()).isNotNull();
        then(amazonS3Client).should(times(1)).deleteObject(any(), any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(1)).getUrl(any(), any());
    }

    @Test
    @DisplayName("회원 이미지를 삭제한다.")
    void deleteImage()  {
        // given
        Image image = Image.createImage("이미지 URL", "이미지 저장 이름");

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(), any());

        // when
        Image result = imageUtil.uploadImage(null, image);

        // then
        assertThat(result).isNull();
        then(amazonS3Client).should(times(1)).deleteObject(any(), any());
    }

    @Test
    @DisplayName("예외 테스트 : 이미지 파일이 아닐 경우 예외가 발생한다.")
    void updateEmptyImage()  {
        // given
        MockMultipartFile file = new MockMultipartFile("image", "프로필사진.png",
                MediaType.APPLICATION_PDF_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

        // when
        assertThrows(RuntimeException.class, () -> imageUtil.uploadImage(file, null));
    }

    @Test
    @DisplayName("예외 테스트 : 파일 업로드 중 오류가 발생하면 예외가 발생한다.")
    void uploadImageException() {
        // given
        MockMultipartFile file = new MockMultipartFile("image", "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

        given(amazonS3Client.putObject(any()))
                .willThrow(MockitoException.class);

        // when
        assertThrows(RuntimeException.class, () -> imageUtil.uploadImage(file, null));

        // then
        then(amazonS3Client).should(times(1)).putObject(any());
    }

    @Test
    @DisplayName("스터디의 저장된 이미지가 없을 경우 이미지 삭제를 하지 않는다.")
    void deleteNoImage() {

        // given
        Image image = Image.createImage("이미지 URL", null);


        Image result = imageUtil.uploadImage(null, image);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("빈 파일일 경우 이미지 업로드를 하지 않는다.")
    void uploadEmptyImage() {
        // given
        MockMultipartFile file = new MockMultipartFile("image", "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE, "".getBytes(StandardCharsets.UTF_8));

        // when
        Image result = imageUtil.uploadImage(file, null);

        // then
        assertThat(result).isNull();
    }
}