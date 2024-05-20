package com.wemade.newboard.service;

import com.wemade.newboard.dto.FileDTO;
import com.wemade.newboard.dto.PostDTO;
import com.wemade.newboard.dto.PostViewBO;
import com.wemade.newboard.exception.InvalidFileException;
import com.wemade.newboard.exception.UnauthorizedAccessException;
import com.wemade.newboard.mapper.PostMapper;
import com.wemade.newboard.param.BasePagingParam;
import com.wemade.newboard.param.InsertPostParam;
import com.wemade.newboard.param.UpdatePostParam;
import com.wemade.newboard.response.DetailPostRes;
import com.wemade.newboard.response.PublicPostRes;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.webjars.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor

public class PostService {
    private final PostMapper postMapper;
    private final AuthService authService;
    private final CommentService commentService;
    public List<PostDTO> getPostAll() { return postMapper.getPostAll(); }

    public List<PublicPostRes> getPublicPostIntroAllByOffset(BasePagingParam basePagingParam) {
        return postMapper.getPublicPostIntroAllByOffset((int) basePagingParam.getOffset(), (int) basePagingParam.getPageSize());
    }

    /**
     * 주어진 아이디에 해당하는 게시글의 상세 정보를 조회합니다.
     * 게시글 정보와 해당 게시글의 모든 댓글을 포함한 PostViewBO 객체를 반환합니다.
     * @param postNo 조회할 게시글의 ID
     * @return 게시글과 댓글 정보를 포함한 PostViewBO 객체
     */
    public PostViewBO getPostViewById(int postNo){
        //FIXME: 파일도 불러와야 함...
        PostViewBO postView = new PostViewBO();
        postView.setPost(postMapper.getDetailPostRes(postNo));
        postView.setComment(commentService.getCommentRes(postNo));

        return postView;
    }

    public PostDTO getPostById(int postNo){
        return postMapper.getPostById(postNo);
    }

    public List<PublicPostRes> searchPosts(String param, BasePagingParam basePagingParam) {
        List<PublicPostRes> searchPosts = postMapper.searchPosts(param, basePagingParam.getOffset(), basePagingParam.getPageSize());
        if (searchPosts == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return searchPosts;
    }

    public List<DetailPostRes> getUserPosts(int userNo) {
        List<DetailPostRes> userPosts = postMapper.getUserPosts(userNo);
        if (userPosts == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userPosts;
    }

    public List<DetailPostRes> getUserTempPosts(int userNo) {
        List<DetailPostRes> userTempPosts = postMapper.getUserTempPosts(userNo);
        if (userTempPosts == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userTempPosts;
    }


    /**
     * 새로운 게시물을 추가합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시물의 생성자로 설정한 후 데이터베이스에 삽입합니다.
     *
     * @param insertPostParam 삽입할 게시물 데이터
     * @param userNo              요청자의 id
     * @return 데이터베이스에 삽입된 게시물 객체
     */
//    public String insertPost(InsertPostParam insertPostParam, int userNo, ArrayList<MultipartFile> files) throws IOException {
//        PostDTO post = new PostDTO();
//        post.setTitle(insertPostParam.getTitle());
//        post.setContents(insertPostParam.getContents());
//        post.setUserNo(userNo);
//        post.setIsTemp(insertPostParam.isTemp());
//        postMapper.insert(post);
//
//        int postNo = postMapper.getPostNoByUserNoAndTitle(userNo, insertPostParam.getTitle());
//        if(files != null) {
//            validateFiles(files); // 파일 유효성 검사
//            uploadFiles(postNo, userNo, insertPostParam.isTemp(), files);
//        }
//
//        return post.getTitle();
//    }
    public String insertPost(MultipartHttpServletRequest request, int userNo) throws IOException {
        PostDTO post = new PostDTO();
        post.setTitle(request.getParameter("title"));
        post.setContents(request.getParameter("contents"));
        post.setUserNo(userNo);

        post.setIsTemp(Boolean.valueOf(request.getParameter("isTemp")));
        postMapper.insert(post);

        int postNo = postMapper.getPostNoByUserNoAndTitle(userNo, request.getParameter("title"));

//        // 파일을 받기 위해 MultipartHttpServletRequest 객체 사용
//        Map<String, MultipartFile> fileMap = request.getFileMap();
//        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
//            MultipartFile file = entry.getValue();
//            // 파일 처리 로직 작성
//            // 파일 이름, 크기 등을 이용하여 필요한 작업 수행
//            validateFiles(files); // 파일 유효성 검사
//
//            System.out.println("업로드한 파일 이름: " + file.getOriginalFilename());
//        }

        ArrayList<MultipartFile> files = new ArrayList<>();
        Iterator<String> fileNames = request.getFileNames(); // Iterator로 파일 이름을 가져옴
        while (fileNames.hasNext()) { // Iterator를 사용하여 순회
            String fileName = fileNames.next();
            MultipartFile file = request.getFile(fileName);
            // 파일 처리 로직 작성
            // 파일 이름, 크기 등을 이용하여 필요한 작업 수행
            System.out.println("업로드한 파일 이름: " + file.getOriginalFilename());
            files.add(file); // ArrayList에 파일 추가
        }




        if(files != null) {
            validateFiles(files); // 파일 유효성 검사
            uploadFiles(postNo, userNo, Boolean.parseBoolean(request.getParameter("isTemp")), files);
        }

        return post.getTitle();
    }

    private void validateFiles(ArrayList<MultipartFile> files) {
        long maxTotalFileSize = 20 * 1024 * 1024; // 20MB
        long totalFileSize = 0;
        int fileCount = 0;

        for(MultipartFile file : files) {
            long fileSize = file.getSize();

            // 파일 크기 검사
            if (!isFileValidSize(fileSize)) throw new InvalidFileException("5MB 이하의 파일만 업로드 가능합니다.");

            // 파일 확장자 검사
            if (!isFileValidExtension(file)) throw new InvalidFileException(".jpg, .png, .gif 확장자의 파일만 업로드 가능합니다.");

            fileCount++;
            totalFileSize += fileSize;

            if(fileCount > 10) throw new InvalidFileException("10개까지의 파일만 업로드 가능합니다. 11개부터 파일은 업로드되지 않습니다.");

            if(totalFileSize > maxTotalFileSize) throw new InvalidFileException("합계 20MB 이상의 파일은 업로드가 불가능합니다.");
        }
    }

    private boolean isFileValidExtension(MultipartFile file) {
        String contentType = file.getContentType();
        // 파일의 MIME 타입에서 확장자를 추출
        String fileExtension = getExtensionFromContentType(contentType);
        // 허용된 확장자 목록
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        // 허용된 확장자 목록에 포함되어 있는지 확인
        return allowedExtensions.contains(fileExtension.toLowerCase());
    }

    private String getExtensionFromContentType(String contentType) {
        // MIME 타입에서 확장자 추출
        if (contentType.equals("image/jpeg")) {
            return "jpg";
        } else if (contentType.equals("image/png")) {
            return "png";
        } else if (contentType.equals("image/gif")) {
            return "gif";
        } else {
            // 기타 MIME 타입에 대한 처리
            return "";
        }
    }


    private boolean isFileValidSize(long fileSize) {
        long maxFileSizePerFile = 5 * 1024 * 1024; // 5MB
        return fileSize <= maxFileSizePerFile;
    }
    private void uploadFiles(int postNo, int userNo, boolean temp, ArrayList<MultipartFile> files) throws IOException {
        String uploadPath = "/Users/wm-id002518/newboardfiles/";

        for(MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            UUID uuid = UUID.randomUUID();
            String savedFileName = uuid.toString() + "_" + originalFileName;
            File destFile = new File(uploadPath + savedFileName);
            file.transferTo(destFile);

            FileDTO fileform = new FileDTO();
            fileform.setPostNo(postNo);
            fileform.setUserNo(userNo);
            fileform.setFileExt(originalFileName.substring(originalFileName.lastIndexOf(".")));
            fileform.setFileName(originalFileName);
            fileform.setFilePath(uploadPath + savedFileName);
            fileform.setTemp(temp);
            postMapper.uploadFile(fileform);
        }
    }

    /**
     * 주어진 ID의 게시물을 업데이트합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시물의 수정자로 설정한 후 데이터베이스에 업데이트합니다.
     *
     * @param updatePostParam 업데이트할 게시물 데이터
     * @param postNo          업데이트할 게시물의 ID
     * @return 데이터베이스에 업데이트된 게시물 객체
     */
    public String updatePost(UpdatePostParam updatePostParam, int postNo, int userNo) throws UnauthorizedAccessException {

        if(getPostById(postNo).getUserNo() != userNo){
            throw new UnauthorizedAccessException("타인의 게시물을 수정할 수 없습니다.");
        }

        PostDTO post = new PostDTO();
        post.setTitle(updatePostParam.getTitle());
        post.setContents(updatePostParam.getContents());
//        post.setFileData(updatePostParam.getFileData());
        post.setPostNo(postNo);
        //FIXME auditListener로 고쳐야 함
        post.setUpdatedBy(userNo);
        post.setIsTemp(updatePostParam.isTemp());

        postMapper.update(post);
        return post.getTitle();
    }

    /**
     * 주어진 ID의 게시물을 삭제합니다.
     * JWT 토큰에서 추출한 사용자 ID가 게시물의 생성자 ID와 일치해야 삭제가 가능합니다.
     * @param postNo 삭제할 게시물의 ID
     * @return 삭제된 게시물의 수 (성공적으로 삭제되면 1)
     * @throws BadRequestException 게시물의 생성자가 아닌 경우 예외를 던집니다.
     */
    public int deletePost(int postNo, int userNo) throws BadRequestException, UnauthorizedAccessException {
        PostDTO post = getPostById(postNo);
        if(post==null){
            throw new NotFoundException("해당 게시글이 존재하지 않습니다.");
        }
        if(post.getUserNo() != userNo){
            throw new UnauthorizedAccessException("타인의 게시물을 수정할 수 없습니다.");
        }

        return postMapper.delete(postNo);
    }


}
