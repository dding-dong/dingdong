package com.sparta.dingdong.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {

	@Schema(description = "시 코드", example = "02")
	@NotNull(message = "시 코드를 입력해주세요.")

	private String cityId; // ex: "02" 서울특별시

	@Schema(description = "구 코드", example = "23")
	@NotNull(message = "구 코드를 입력해주세요.")
	private String guId;   // ex: "23" 종로구

	@Schema(description = "동 코드", example = "400")
	@NotNull(message = "동 코드를 입력해주세요.")
	private String dongId; // ex: "400" 연건동

	@Schema(description = "상세 주소", example = "101호")
	@NotBlank(message = "상세 주소를 입력해주세요.")
	private String detailAddress;

	@Schema(description = "우편번호", example = "06210")
	@NotBlank(message = "우편번호를 입력해주세요.")
	@Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자로 입력해주세요.")
	private String postalCode;

	@Schema(description = "기본 주소 여부", example = "true")
	private Boolean isDefault; // 기본주소 여부
}