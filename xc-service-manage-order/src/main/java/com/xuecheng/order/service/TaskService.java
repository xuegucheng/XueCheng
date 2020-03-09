package com.xuecheng.order.service;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.order.dao.XcLearningCourseRepository;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 描述:
 * Task Service
 *
 * @author XueGuCheng
 * @create 2020-03-09 10:55
 */
@Service
public class TaskService {

    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;
    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;

    //取出前n条任务,取出指定时间之前处理的任务
    public List<XcTask> findTaskList(Date updateTime,int n){
        //设置分页参数，取出前n 条记录
        Pageable pageable = (Pageable)new PageRequest(0, n);
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return xcTasks.getResult();
    }

    /**
     * 发送消息
     * @param xcTask 任务对象
     * @param ex 交换机id
     * @param routingKey
     */
    @Transactional
    public void publish(XcTask xcTask,String ex,String routingKey){
        //查询任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if (taskOptional.isPresent()){
            XcTask xcTask1 = taskOptional.get();
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask1);
            //更新任务时间为当前时间
            xcTask1.setUpdateTime(new Date());
            xcTaskRepository.save(xcTask1);
        }
    }

    @Transactional
    public int getTask(String taskId,int version){
        int i = xcTaskRepository.updateTaskVersion(taskId, version);
        return i;
    }

    //向xc_learning_course添加记录，为保证不重复添加选课，先查询历史任务表，如果从历史任务表查询不到任务说 明此任务还没有处理，此时则添加选课并添加历史任务。
    @Transactional
    public ResponseResult addCourse(String userId,String courseId,String valid,Date startTime,Date endTime,XcTask xcTask){
        if (StringUtils.isEmpty(courseId)){
            //抛异常
        }
        if (StringUtils.isEmpty(userId)){
            //抛异常
        }
        if (xcTask == null || StringUtils.isEmpty(xcTask.getId())){
            //抛异常
        }
        //查询历史任务
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (optional.isPresent()){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse == null) {
            //没有选课记录则添加
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }else {
            //有选课记录则更新日期
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }
        //向历史任务表插入记录
        Optional<XcTaskHis> optional1 = xcTaskHisRepository.findById(xcTask.getId());
        if ( !optional1.isPresent()){
            //添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }

        return new ResponseResult(CommonCode.SUCCESS);


    }

    //删除任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> optional = xcTaskRepository.findById(taskId);
        if (optional.isPresent()){
            XcTask xcTask = optional.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }


}
