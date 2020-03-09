package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 描述:
 * Choose Course Task 任务类，每分钟执行任务，启动订单工程，观察定时发送消息日志，观察rabbitMQ队列中是否有消息
 *
 * @author XueGuCheng
 * @create 2020-03-09 11:01
 */
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    //每隔1分钟扫描消息表，向mq发送消息
    @Scheduled(fixedDelay = 60000)
    public void sendChoosecourseTask(){
        //取出当前时间1分钟之前的时间
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);

        //遍历任务列表
        for (XcTask xcTask : taskList) {
            //任务id
            String taskId = xcTask.getId();
            //版本号
            Integer version = xcTask.getVersion();
            //调用乐观锁方法校验任务是否可以执行
            if (taskService.getTask(taskId,version) > 0){
                //发送选课消息
                taskService.publish(xcTask,xcTask.getMqExchange(),xcTask.getMqRoutingkey());
                LOGGER.info("send choose course task id:{}",xcTask.getId());
            }


        }
    }

   // 接收选课响应结果
    //@RabbitListener(queues = {RabbitMQConfig.xc_learning_finishaddchoosecourse})
    public void receiveFinishChoosecourseTask(XcTask task, Message message, Channel channel){
        LOGGER.info("receiveChoosecourseTask...{}",task.getId());
        //接收到的消息id
        String id = task.getId();
        //删除任务，添加历史任务
        taskService.finishTask(id);
    }

}
