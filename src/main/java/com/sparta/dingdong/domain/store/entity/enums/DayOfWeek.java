package com.sparta.dingdong.domain.store.entity.enums;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayOfWeek {
	MONDAY("월요일"),
	TUESDAY("화요일"),
	WEDNESDAY("수요일"),
	THURSDAY("목요일"),
	FRIDAY("금요일"),
	SATURDAY("토요일"),
	SUNDAY("일요일");

	private final String label; // 한글 요일명

	// 한글 라벨 → Enum 변환
	public static DayOfWeek fromLabel(String label) {
		for (DayOfWeek day : DayOfWeek.values()) {
			if (day.label.equals(label)) {
				return day;
			}
		}
		throw new IllegalArgumentException("Invalid day label: " + label);
	}

	// Enum Set → CSV(한글)
	public static String toCsv(Set<DayOfWeek> days) {
		return days.stream()
			.map(DayOfWeek::getLabel) // label 기준으로 변환
			.sorted()
			.reduce((a, b) -> a + "," + b)
			.orElse("");
	}

	// CSV(한글) → Enum Set
	public static Set<DayOfWeek> fromCsv(String csv) {
		if (csv == null || csv.isEmpty())
			return new HashSet<>();
		String[] parts = csv.split(",");
		Set<DayOfWeek> days = new HashSet<>();
		for (String label : parts) {
			days.add(fromLabel(label.trim())); // 한글 → Enum
		}
		return days;
	}
}