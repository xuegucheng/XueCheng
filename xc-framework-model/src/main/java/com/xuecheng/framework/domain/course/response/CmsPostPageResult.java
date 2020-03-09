package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 * Cms Post Page Result
 *
 * @author XueGuCheng
 * @create 2020-03-06 15:25
 */
@Data
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
    String pageUrl;
    public CmsPostPageResult(ResultCode resultCode,String pageUrl){
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
