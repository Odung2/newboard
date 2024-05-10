package com.wemade.newboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class fileDTO {
    @Schema(description = "파일 번호")
    public int fileNo;

    @Schema(description = "게시물 번호")
    public int postNo;

    @Schema(description = "사용자 번호")
    public int userNo;

    @Schema(description = "파일 경로")
    public String filePath;

    @Schema(description = "파일 이름")
    public String fileName;

    @Schema(description = "파일 확장자")
    public String fileExt;

    @Schema(description = "임시 저장 파일")
    public boolean isTemp;

    @Schema(description = "삭제 여부")
    public boolean isDelete;


}
