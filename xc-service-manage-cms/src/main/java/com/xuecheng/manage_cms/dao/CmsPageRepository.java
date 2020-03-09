package com.xuecheng.manage_cms.dao;


import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    //根据页面名称查询
    CmsPage findByPageName(String pageName);

    /**
     *  根据页面名称、站点id、页面访问路径查询--->用于校验页面是否存在
     * @param pageName 页面名称
     * @param siteId 站点id
     * @param pageWebParh 页面访问路径
     * @return
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebParh);



}
