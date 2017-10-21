package com.cloud.shop.core.monitor.webresmonitor;

import com.cloud.shop.core.monitor.webresmonitor.pojo.FixedSizeQueue;
import com.cloud.shop.core.monitor.webresmonitor.pojo.WebResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 监控Web应用的各项资源占用情况<br/>
 *  cpu状态，总内存状态，堆内存状态，非堆内存状态，线程数，GC次数
 * Created by ChengYun on 2017/9/10 Version 1.0
 */
@Component
public class WebResourceMonitor {
    /** ManagementFactory.getGarbageCollectorMXBeans中能获得到的1.8虚拟机GC的标识 */
    public static String MONIT_GC = "PS Scavenge";
    public static String FULL_GC = "PS MarkSweep";

    private final static Logger logger = LoggerFactory.getLogger(WebResourceMonitor.class);

    /** 缓存最新10次服务器资源状态信息 */
    private FixedSizeQueue<WebResourceModel> webResourceModels = new FixedSizeQueue<>(15);


    @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次,用于收集统计应用服务器信息
    /**  CRON表达式    含义
     * "0 0 12 * * ?"    每天中午十二点触发
     "0 15 10 ? * *"    每天早上10：15触发
     "0 15 10 * * ?"    每天早上10：15触发
     "0 15 10 * * ? *"    每天早上10：15触发
     "0 15 10 * * ? 2005"    2005年的每天早上10：15触发
     "0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发
     "0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发
     "0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发
     "0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发
     "0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发
     "0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发
     */
    public void run(){
        String heapMax = Runtime.getRuntime().totalMemory() / 1024 / 1024 +"M" ;
        String heapFree = Runtime.getRuntime().freeMemory() / 1024 / 1024 + "M" ;
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        String threadCount = threadMXBean.getThreadCount() + "" ;
        String threadDemoCount = threadMXBean.getDaemonThreadCount() + "" ;
        String monitorGCCount=null,monitorTimes=null,fullGCCount=null,fullGCTimes=null;
        List<GarbageCollectorMXBean> garbageCollectorMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeanList){
            if(MONIT_GC.equals(garbageCollectorMXBean.getName())){
                monitorGCCount = garbageCollectorMXBean.getCollectionCount() + "";
                monitorTimes = garbageCollectorMXBean.getCollectionTime()/1000+"秒";
            }else if(FULL_GC.equals(garbageCollectorMXBean.getName())){
                fullGCCount = garbageCollectorMXBean.getCollectionCount() + "";
                fullGCTimes = garbageCollectorMXBean.getCollectionTime()/1000+"秒";
            }
        }
        WebResourceModel webResourceModel = new WebResourceModel.WebResourceModelBuilder().heapMax(heapMax).heapFree(heapFree)
                .threadCount(threadCount).threadDemoCount(threadDemoCount)
                .monitorGCCount(monitorGCCount).monitorTimes(monitorTimes).fullGCCount(fullGCCount).fullGCTimes(fullGCTimes).builder();
        webResourceModels.add(webResourceModel);
//        logger.debug("\n\n");
//        logger.debug("=============服务器监控信息如下============");
//        logger.debug("堆总内存：" + heapMax);
//        logger.debug("堆空闲内存：" + heapFree);
//        logger.debug("线程数：" + threadCount);
//        logger.debug("守护线程数：" + threadDemoCount);
//        logger.debug("MonitorGC次数：" + monitorGCCount);
//        logger.debug("MonitorGC时间：" + monitorTimes);
//        logger.debug("fullGC次数：" + fullGCCount);
//        logger.debug("fullGC时间：" + fullGCTimes);


        /**
        logger.debug("\n");logger.debug("\n");
        logger.debug("=============进入测试：内存情况(在用)============");
        logger.debug("堆总：" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M");
        logger.debug("堆闲：" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + "M");
        logger.debug("堆用："
                + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + "M");

        logger.debug("=============进入测试：线程占用情况============");
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while(threadGroup.getParent() != null){
            threadGroup = threadGroup.getParent();
        }
        int totalThread = threadGroup.activeCount();
        logger.debug("总线程数："+totalThread);

        logger.debug("=============Java虚拟机============");
        logger.debug("Java虚拟机内存总量：" + (Runtime.getRuntime().totalMemory()
                + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()) / 1024 / 1024 + "M");
        logger.debug("Java虚拟机空闲内存：" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + "M");

        logger.debug("=============分配的堆内存当前使用量============");
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        logger.debug("可用于内存管理的最大内存量：" + heapUsage.getMax() / 1024 / 1024 + "M");
        logger.debug("初始内存大小：" + heapUsage.getInit() / 1024 / 1024 + "M");
        logger.debug("当前已使用的内存量：" + heapUsage.getUsed() / 1024 / 1024 + "M");
        logger.debug("Java虚拟机能使用的内存量：" + heapUsage.getCommitted() / 1024 / 1024 + "M");

        logger.debug("=============分配的非堆内存当前使用量============");
        MemoryUsage noHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        logger.debug("可用于内存管理的最大内存量：" + noHeapUsage.getMax() / 1024 / 1024 + "M");
        logger.debug("初始内存大小：" + noHeapUsage.getInit() / 1024 / 1024 + "M");
        logger.debug("当前已使用的内存量：" + noHeapUsage.getUsed() / 1024 / 1024 + "M");
        logger.debug("Java虚拟机能使用的内存量：" + noHeapUsage.getCommitted() / 1024 / 1024 + "M");

        logger.debug("=============Class Loader信息收集============");
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        logger.debug("Java虚拟机已加载类的总数：" + classLoadingMXBean.getLoadedClassCount());
        logger.debug("Java虚拟机从启动开始已加载的类总数：" + classLoadingMXBean.getTotalLoadedClassCount());
        logger.debug("Java虚拟机从启动开始已卸载的类总数：" + classLoadingMXBean.getUnloadedClassCount());

        logger.debug("=============线程监控信息(在用)============");
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        logger.debug("线程数：" + threadMXBean.getThreadCount());
        logger.debug("线程峰值：" + threadMXBean.getPeakThreadCount());
        logger.debug("守护线程数：" + threadMXBean.getDaemonThreadCount());

        logger.debug("=============垃圾回收期信息============");
        List<GarbageCollectorMXBean> garbageCollectorMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeanList){
            String name = garbageCollectorMXBean.getName();
            logger.debug("垃圾回收器的名称：" + name);
            logger.debug("垃圾回收器已回收的总次数：" + garbageCollectorMXBean.getCollectionCount());
            logger.debug("垃圾回收器已回收的总时间：" + garbageCollectorMXBean.getCollectionTime()/1000+"秒");
        }
        */
//        //TODO 如何获得CPU情况(这种方式太慢，太占CPU资源了)
//        String osName = System.getProperty("os.name");
//        double cpuRatio = 0;
//        if (osName.toLowerCase().startsWith("windows")) {
//            cpuRatio = this.getCpuRatioForWindows();
//        }
//        else {
//            cpuRatio = this.getCpuRateForLinux();
//        }
//        logger.debug("cpuRatio："+cpuRatio);
    }

    public FixedSizeQueue<WebResourceModel> getWebResourceModels() {
        return webResourceModels;
    }

    /**
     * 获得CPU使用率.
     * @return 返回cpu使用率
     */
    private double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                    + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return Double.valueOf(
                        100 * (busytime) / (busytime + idletime))
                        .doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * 读取CPU信息.
     * @param proc
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < 10) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = substring(line, capidx, cmdidx - 1)
                        .trim();
                String cmd = substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                // log.info("line="+line);
                if (caption.equals("System Idle Process")
                        || caption.equals("System")) {
                    idletime += Long.valueOf(
                            substring(line, kmtidx, rocidx - 1).trim())
                            .longValue();
                    idletime += Long.valueOf(
                            substring(line, umtidx, wocidx - 1).trim())
                            .longValue();
                    continue;
                }
                kneltime += Long.valueOf(
                        substring(line, kmtidx, rocidx - 1).trim())
                        .longValue();
                usertime += Long.valueOf(
                        substring(line, umtidx, wocidx - 1).trim())
                        .longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private double getCpuRateForLinux(){
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat = null;
        String linuxVersion = null;
        try{
            System.out.println("Get usage rate of CUP , linux version: "+linuxVersion);
            Process process = Runtime.getRuntime().exec("top -b -n 1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);

            if(linuxVersion.equals("2.4")){
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();

                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                String user = tokenStat.nextToken();
                tokenStat.nextToken();
                String system = tokenStat.nextToken();
                tokenStat.nextToken();
                String nice = tokenStat.nextToken();

                System.out.println(user+" , "+system+" , "+nice);

                user = user.substring(0,user.indexOf("%"));
                system = system.substring(0,system.indexOf("%"));
                nice = nice.substring(0,nice.indexOf("%"));

                float userUsage = new Float(user).floatValue();
                float systemUsage = new Float(system).floatValue();
                float niceUsage = new Float(nice).floatValue();

                return (userUsage+systemUsage+niceUsage)/100;
            }else{
                brStat.readLine();
                brStat.readLine();

                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                String cpuUsage = tokenStat.nextToken();


                System.out.println("CPU idle : "+cpuUsage);
                Float usage = new Float(cpuUsage.substring(0,cpuUsage.indexOf("%")));

                return (1-usage.floatValue()/100);
            }

        } catch(IOException ioe){
            System.out.println(ioe.getMessage());
            freeResource(is, isr, brStat);
            return 1;
        } finally{
            freeResource(is, isr, brStat);
        }
    }

    private void freeResource(InputStream is, InputStreamReader isr, BufferedReader br){
        try{
            if(is!=null)
                is.close();
            if(isr!=null)
                isr.close();
            if(br!=null)
                br.close();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    public static String substring(String src, int start_idx, int end_idx) {
        byte[] b = src.getBytes();
        String tgt = "";
        for (int i = start_idx; i <= end_idx; i++) {
            tgt += (char) b[i];
        }
        return tgt;
    }

}
