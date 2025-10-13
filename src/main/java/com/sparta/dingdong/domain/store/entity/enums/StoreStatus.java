package com.sparta.dingdong.domain.store.entity.enums;

import lombok.Getter;

@Getter
public enum StoreStatus {
	PREPARING,
	OPEN,
	CLOSED,
	FORCED_CLOSED // 매니저 강제 종료
}
