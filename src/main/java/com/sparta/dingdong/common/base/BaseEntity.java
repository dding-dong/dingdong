package com.sparta.dingdong.common.base;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@CreatedBy
	private Long createdBy;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@LastModifiedBy
	private Long updatedBy;

	private LocalDateTime deletedAt;

	private Long deletedBy;

	public void softDelete(Long userId) {
		if (this.deletedAt != null) {
			throw new IllegalStateException("이미 삭제된 데이터입니다.");
		}
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = userId;
	}

	public void restore(Long userId) {
		this.deletedAt = null;
		this.deletedBy = userId; // 삭제 되었었다는 걸 남기기 위해 마지막으로 복구한 사람 아이디 저장
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}

	public void reactivate() {
		this.deletedAt = null;
		this.deletedBy = null;
	}
}
