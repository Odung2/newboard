package com.wemade.newboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class BaseDTO {

    @CreatedDate
    @Schema(description = "작성일")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정한 사용자의 ID")
    private Integer updatedBy;

    @LastModifiedDate
    @Schema(description = "최종 수정일")
    private LocalDateTime updatedAt;

}
