package com.xuecheng.api.filesystem;

        import com.xuecheng.framework.domain.course.CoursePic;
        import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
        import com.xuecheng.framework.model.response.ResponseResult;
        import org.springframework.web.multipart.MultipartFile;

public interface FileSystemControllerApi {

    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param metadata 元信息,json格式
     * @return
     */
    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey,String metadata);

    //添加课程图片
    public ResponseResult addCoursePic(String courseId,String pic);

    //获取课程基础信息
    public CoursePic findCoursePic(String courseId);

    //删除课程图片
    public ResponseResult deleteCoursePic(String courseId);




}
