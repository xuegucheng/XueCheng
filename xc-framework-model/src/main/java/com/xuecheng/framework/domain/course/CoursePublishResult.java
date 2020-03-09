package com.xuecheng.framework.domain.course;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述:
 * Course Publish Result
 *
 * @author XueGuCheng
 * @create 2020-03-05 13:31
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {

    String previewUrl;

    public  CoursePublishResult(ResultCode resultCode,String previewUrl){
       super(resultCode);
        this.previewUrl=previewUrl;
    }
}
