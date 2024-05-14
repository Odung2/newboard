package com.wemade.newboard.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class BasePagingParam {
    @Schema(description = "현재 페이지 [default: 1, min: 1]")
    @Min(value = 1, message = "페이지는 1부터 가능합니다.")
    @Digits(integer=3, fraction = 0, message = "1 이상 정수만 입력가능합니다.")
    private int currPage = 1;

    @Schema(description = "페이지 사이즈 [default: 20, min: 2, max: 200]")
    @Min(value = 2, message = "페이지 사이즈는 2에서 200 사이의 값만 가능합니다.")
    @Max(value = 200, message = "페이지 사이즈는 2에서 200 사이의 값만 가능합니다.")
    @Digits(integer = 3, fraction = 0, message = "2 이상 정수만 입력가능합니다.")
    private int pageSize = 3;

    /**
     * paging의 offset 계산해서 리턴
     *
     * @return
     */
    @JsonIgnore
    public int getOffset() {
        return (currPage -1) * pageSize;
    }
}
