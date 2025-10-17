package com.sparta.dingdong.common.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sparta.dingdong.common.entity.City;
import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.entity.Gu;
import com.sparta.dingdong.domain.user.repository.CityRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

	private final CityRepository cityRepository;

	@Override
	public void run(String... args) throws Exception {
		// 서울특별시 생성
		City seoul = City.of("02", "서울특별시");

		// 종로구 생성
		Gu jongno = Gu.of("23", "종로구", seoul);
		seoul.addGu(jongno);

		// 종로구 전체 동 추가
		addDong(jongno, "010", "가회동");
		addDong(jongno, "020", "견지동");
		addDong(jongno, "030", "경운동");
		addDong(jongno, "040", "계동");
		addDong(jongno, "050", "공평동");
		addDong(jongno, "060", "관철동");
		addDong(jongno, "070", "관훈동");
		addDong(jongno, "080", "교남동");
		addDong(jongno, "090", "교북동");
		addDong(jongno, "100", "구기동");
		addDong(jongno, "110", "권농동");
		addDong(jongno, "120", "낙원동");
		addDong(jongno, "130", "내수동");
		addDong(jongno, "140", "내자동");
		addDong(jongno, "150", "누상동");
		addDong(jongno, "160", "누하동");
		addDong(jongno, "170", "당주동");
		addDong(jongno, "180", "도렴동");
		addDong(jongno, "190", "돈의동");
		addDong(jongno, "200", "동숭동");
		addDong(jongno, "210", "명륜1가");
		addDong(jongno, "211", "명륜2가");
		addDong(jongno, "212", "명륜3가");
		addDong(jongno, "213", "명륜4가");
		addDong(jongno, "220", "묘동");
		addDong(jongno, "230", "무악동");
		addDong(jongno, "240", "봉익동");
		addDong(jongno, "250", "부암동");
		addDong(jongno, "260", "사간동");
		addDong(jongno, "270", "사직동");
		addDong(jongno, "280", "삼청동");
		addDong(jongno, "290", "서린동");
		addDong(jongno, "300", "세종로");
		addDong(jongno, "310", "소격동");
		addDong(jongno, "320", "송월동");
		addDong(jongno, "330", "송현동");
		addDong(jongno, "340", "수송동");
		addDong(jongno, "350", "숭인동");
		addDong(jongno, "360", "신교동");
		addDong(jongno, "370", "신문로1가");
		addDong(jongno, "371", "신문로2가");
		addDong(jongno, "380", "신영동");
		addDong(jongno, "390", "안국동");
		addDong(jongno, "400", "연건동");
		addDong(jongno, "410", "연지동");
		addDong(jongno, "420", "예지동");
		addDong(jongno, "430", "옥인동");
		addDong(jongno, "440", "와룡동");
		addDong(jongno, "450", "운니동");
		addDong(jongno, "460", "원남동");
		addDong(jongno, "470", "원서동");
		addDong(jongno, "480", "이화동");
		addDong(jongno, "490", "익선동");
		addDong(jongno, "500", "인사동");
		addDong(jongno, "510", "장사동");
		addDong(jongno, "520", "재동");
		addDong(jongno, "530", "적선동");
		addDong(jongno, "540", "종로1가");
		addDong(jongno, "541", "종로2가");
		addDong(jongno, "542", "종로3가");
		addDong(jongno, "543", "종로4가");
		addDong(jongno, "544", "종로5가");
		addDong(jongno, "545", "종로6가");
		addDong(jongno, "550", "창성동");
		addDong(jongno, "560", "창신동");
		addDong(jongno, "570", "청운동");
		addDong(jongno, "580", "청진동");
		addDong(jongno, "590", "충신동");
		addDong(jongno, "600", "통의동");
		addDong(jongno, "610", "통인동");
		addDong(jongno, "620", "팔판동");
		addDong(jongno, "630", "평동");
		addDong(jongno, "640", "평창동");
		addDong(jongno, "650", "필운동");
		addDong(jongno, "660", "행촌동");
		addDong(jongno, "670", "혜화동");
		addDong(jongno, "680", "홍지동");
		addDong(jongno, "690", "홍파동");
		addDong(jongno, "700", "화동");
		addDong(jongno, "710", "효자동");
		addDong(jongno, "720", "효제동");
		addDong(jongno, "730", "훈정동");

		// DB 저장
		cityRepository.save(seoul);
	}

	private void addDong(Gu gu, String id, String name) {
		Dong dong = Dong.of(id, name, gu); // Builder 대신 of() 사용
		gu.addDong(dong);
	}
}
