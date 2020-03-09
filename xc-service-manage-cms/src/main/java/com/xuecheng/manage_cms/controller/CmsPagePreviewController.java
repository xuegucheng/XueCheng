package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * 描述:
 * 用于页面预览
 *
 * @author XueGuCheng
 * @create 2020-03-01 20:51
 */
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    //接收到页面id
    @RequestMapping(value="/cms/preview/{pageId}",method = RequestMethod.GET)
    public void preview(@PathVariable("pageId")String pageId){
        String pageHtml = pageService.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)){
            //为空，抛异常
        }
        try{
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(pageHtml.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
