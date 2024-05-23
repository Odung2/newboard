package com.wemade.newboard.service;

import com.wemade.newboard.dto.FileDTO;
import com.wemade.newboard.dto.PostDTO;
import com.wemade.newboard.dto.PostViewBO;
import com.wemade.newboard.exception.InvalidFileException;
import com.wemade.newboard.exception.UnauthorizedAccessException;
import com.wemade.newboard.mapper.PostMapper;
import com.wemade.newboard.param.BasePagingParam;
import com.wemade.newboard.param.UpdatePostParam;
import com.wemade.newboard.response.DetailPostRes;
import com.wemade.newboard.response.PublicPostRes;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
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
    private final CommentService commentService;
//    public List<PostDTO> getPostAll() { return postMapper.getPostAll(); }

    /**
     * 게시물 리스트
     * @param basePagingParam
     * @return
     */
    public List<PublicPostRes> getPublicPostIntroAllByOffset(BasePagingParam basePagingParam) {
        return postMapper.getPublicPostIntroAllByOffset((int) basePagingParam.getOffset(), (int) basePagingParam.getPageSize());
    }

    /**
     * 게시물 상세보기
     * @param postNo
     * @return
     */
    public PostViewBO getPostViewById(int postNo){
        //FIXME: 파일도 불러와야 함...
        PostViewBO postView = new PostViewBO();
        postView.setPost(postMapper.getDetailPostRes(postNo));
        postView.setComment(commentService.getCommentRes(postNo));

        return postView;
    }

    /**
     * 게시물 정보 조회
     * @param postNo
     * @return
     */
    public PostDTO getPostById(int postNo){
        return postMapper.getPostById(postNo);
    }

    /**
     * 게시물 리스트 검색
     * @param param
     * @param basePagingParam
     * @return
     */
    public List<PublicPostRes> searchPosts(String param, BasePagingParam basePagingParam) {
        List<PublicPostRes> searchPosts = postMapper.searchPosts(param, basePagingParam.getOffset(), basePagingParam.getPageSize());
        if (searchPosts == null) {
            throw new NotFoundException("게시물을 찾을 수 없습니다.");
        }
        return searchPosts;
    }

    /**
     * 내가 작성한 글 조회
     * @param userNo
     * @return
     */
    public List<DetailPostRes> getUserPosts(int userNo) {
        List<DetailPostRes> userPosts = postMapper.getUserPosts(userNo);
        if (userPosts == null) {
            throw new NotFoundException("게시물을 찾을 수 없습니다.");
        }
        return userPosts;
    }

    /**
     * 내가 작성한 임시 저장글 조회
     * @param userNo
     * @return
     */
    public List<DetailPostRes> getUserTempPosts(int userNo) {
        List<DetailPostRes> userTempPosts = postMapper.getUserTempPosts(userNo);
        if (userTempPosts == null) {
            throw new NotFoundException("게시물을 찾을 수 없습니다.");
        }
        return userTempPosts;
    }


//    public String insertPost(MultipartHttpServletRequest request, int userNo) throws IOException {
//        PostDTO post = new PostDTO();
//        post.setTitle(request.getParameter("title"));
//        post.setContents(request.getParameter("contents"));
//        post.setUserNo(userNo);
//        post.setTemp(request.getParameter("isTemp"));
//        postMapper.insert(post);
//
//        int postNo = postMapper.getPostNoByUserNoAndTitle(userNo, request.getParameter("title"));
//
//        ArrayList<MultipartFile> files = new ArrayList<>();
//        MultiValueMap<String, MultipartFile> multipartFiles = request.getMultiFileMap();
//
//        for (List<MultipartFile> fileList : multipartFiles.values()) {
//            files.addAll(fileList);
//        }
//        if(hasFiles(files)){
//            validateFiles(files); // 파일 유효성 검사
//            uploadFiles(postNo, userNo, Boolean.parseBoolean(request.getParameter("isTemp")), files);
//        }
//        return post.getTitle();
//    }

    /**
     * 게시물 작성 및 파일 저장
     * @param request
     * @param userNo
     * @return
     * @throws IOException
     */
    public String insertPost(MultipartHttpServletRequest request, int userNo) throws IOException {
        PostDTO post = createPostFromRequest(request, userNo);
        int postNo = savePostAndGetId(post);

        ArrayList<MultipartFile> files = extractFilesFromRequest(request);
        if (hasFiles(files)) {
            validateAndSaveFiles(postNo, userNo, Boolean.parseBoolean(request.getParameter("isTemp")), files);
        }
        return post.getTitle();
    }

    /**
     * 요청에서 post DTO set
     * @param request
     * @param userNo
     * @return
     */
    private PostDTO createPostFromRequest(MultipartHttpServletRequest request, int userNo) {
        PostDTO post = new PostDTO();
        post.setTitle(request.getParameter("title"));
        post.setContents(request.getParameter("contents"));
        post.setUserNo(userNo);
        post.setTemp(request.getParameter("isTemp"));
        return post;
    }

    /**
     * 게시물 저장 후 게시물 번호 반환
     * @param post
     * @return
     */
    private int savePostAndGetId(PostDTO post) {
        postMapper.insert(post);
        return postMapper.getPostNoByUserNoAndTitle(post.getUserNo(), post.getTitle());
    }

    /**
     * 요청에서 파일들 추출
     * @param request
     * @return
     */
    private ArrayList<MultipartFile> extractFilesFromRequest(MultipartHttpServletRequest request) {
        ArrayList<MultipartFile> files = new ArrayList<>();
        MultiValueMap<String, MultipartFile> multipartFiles = request.getMultiFileMap();
        for (List<MultipartFile> fileList : multipartFiles.values()) {
            files.addAll(fileList);
        }
        return files;
    }

    /**
     * 파일 유효성 검사 후 저장
     * @param postNo
     * @param userNo
     * @param isTemp
     * @param files
     * @throws IOException
     */
    private void validateAndSaveFiles(int postNo, int userNo, boolean isTemp, ArrayList<MultipartFile> files) throws IOException {
        validateFiles(files);
        saveFiles(postNo, userNo, isTemp, files);
    }

    /**
     * 파일 존재 여부 확인
     * @param files
     * @return
     */
    private boolean hasFiles(ArrayList<MultipartFile> files){
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 파일 유효성 검사
     * @param files
     */
    private void validateFiles(ArrayList<MultipartFile> files) {

        long maxTotalFileSize = 20 * 1024 * 1024; // 20MB
        long totalFileSize = 0;
        int fileCount = 0;

        for(MultipartFile file : files) {
            if(file.isEmpty()) break;

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

    /**
     * 파일 확장자 검사
     * @param file
     * @return
     */
    private boolean isFileValidExtension(MultipartFile file) {
        String contentType = file.getContentType();
        // 파일의 MIME 타입에서 확장자를 추출
        String fileExtension = getExtensionFromContentType(contentType);
        // 허용된 확장자 목록
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        // 허용된 확장자 목록에 포함되어 있는지 확인
        return allowedExtensions.contains(fileExtension.toLowerCase());
    }

    /**
     * 파일 확장자 추출
     * @param contentType
     * @return
     */
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

    /**
     * 파일 사이즈 검사
     * @param fileSize
     * @return
     */
    private boolean isFileValidSize(long fileSize) {
        long maxFileSizePerFile = 5 * 1024 * 1024; // 5MB
        return fileSize <= maxFileSizePerFile;
    }

    /**
     * 파일 저장
     * @param postNo
     * @param userNo
     * @param temp
     * @param files
     * @throws IOException
     */
    private void saveFiles(int postNo, int userNo, boolean temp, ArrayList<MultipartFile> files) throws IOException {
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
     * 파일 수정
     * @param updatePostParam
     * @param postNo
     * @param userNo
     * @return
     * @throws UnauthorizedAccessException
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
        post.setUpdatedBy(userNo);
        post.setIsTemp(updatePostParam.isTemp());

        postMapper.update(post);
        return post.getTitle();
    }

    /**
     * 파일 삭제
     * @param postNo
     * @param userNo
     * @return
     * @throws BadRequestException
     * @throws UnauthorizedAccessException
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
