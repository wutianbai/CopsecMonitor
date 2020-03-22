package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.statistics.*;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.entity.*;
import com.copsec.monitor.web.repository.*;
import com.copsec.monitor.web.service.StatisticsService;
import com.copsec.monitor.web.utils.DeviceIdUtils;
import com.copsec.monitor.web.utils.FormatUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private NetStatusRepository netStatusRepository;

    @Autowired
    private ProtocolStatusRepository protocolStatusRepository;

    @Autowired
    private DbStatusRepository dbStatusRepository;

    @Autowired
    private FileStatusRepository fileStatusRepository;

    @Autowired
    private FileSyncStatusRepository fileSyncStatusRepository;

    @Autowired
    private DeviceStatusRepository deviceStatusRepository;

    @Override
    public CopsecResult getCpuStatisticsInfoBy1(ConditionBean conditionBean) {

        return null;
    }

    @Override
    public CopsecResult getCpuStatisticsInfoByDayTime(ConditionBean conditionBean) {

        return null;
    }

    @Override
    public CopsecResult getNetData(ConditionBean conditionBean, boolean isSum) {

        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        ArrayList<String> labels = new ArrayList<>();

        operations.add(project("deviceId", "year", "month", "day", "hour", "dataSum", "maxSpeed", "updateTime"));
        //安天统计
        if (conditionBean.getType().equalsIgnoreCase("day")) {

            labels = FormatUtils.getDaySequenceWith(conditionBean.getStart(), conditionBean.getEnd());

            AggregationOperation match = null;
            if (conditionBean.getDeviceIds().size() > 0) {

                match = match(criteria.and("updateTime").
                        gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                        .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59")).and("deviceId").in(conditionBean.getDeviceIds()));
            } else {

                match = match(criteria.and("updateTime").
                        gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                        .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59")));
            }
            operations.add(match);
            if (isSum) {

                operations.add(group("deviceId", "year", "month", "day").
                        sum("dataSum").as("dataSum"));
            } else {

                operations.add(group("deviceId", "year", "month", "day").
                        max("maxSpeed").as("maxSpeed"));
            }

        } else if (conditionBean.getType().equalsIgnoreCase("month")) { //按月统计

            labels = FormatUtils.getMonthSequenceWith(conditionBean.getStart(), conditionBean.getEnd());
            //当指定月进行查询时，应该根据年和月来进行match
            String[] startTime = conditionBean.getStart().split("-");
            String[] endTime = conditionBean.getEnd().split("-");

            AggregationOperation match = null;
            if (conditionBean.getDeviceIds().size() > 0) {

                match = match(criteria.and("year").
                        gte(Integer.valueOf(startTime[0])).lte(Integer.valueOf(endTime[0])).
                        and("month").gte(Integer.valueOf(startTime[1]))
                        .lte(Integer.valueOf(endTime[1])).and("deviceId").in(conditionBean.getDeviceIds()));
            } else {

                match = match(criteria.and("year").
                        gte(Integer.valueOf(startTime[0])).lte(Integer.valueOf(endTime[0])).
                        and("month").gte(Integer.valueOf(startTime[1]))
                        .lte(Integer.valueOf(endTime[1])));
            }
            operations.add(match);
            if (isSum) {

                operations.add(group("deviceId", "year", "month").
                        sum("dataSum").as("dataSum"));
            } else {

                operations.add(group("deviceId", "year", "month").
                        max("maxSpeed").as("maxSpeed"));
            }


        } else if (conditionBean.getType().equalsIgnoreCase("year")) {//按年统计

            labels = FormatUtils.getYearSequenceWith(conditionBean.getStart(), conditionBean.getEnd());
            //当指定年的时候，应该根据year来对比
            AggregationOperation match = null;
            if (conditionBean.getDeviceIds().size() > 0) {

                match = match(criteria.and("year").
                        gte(Integer.valueOf(conditionBean.getStart()))
                        .lte(Integer.valueOf(conditionBean.getEnd())).and("deviceId").in(conditionBean.getDeviceIds()));
            } else {

                match = match(criteria.and("year").
                        gte(Integer.valueOf(conditionBean.getStart()))
                        .lte(Integer.valueOf(conditionBean.getEnd())));
            }
            operations.add(match);
            if (isSum) {

                operations.add(group("deviceId", "year").
                        sum("dataSum").as("dataSum"));
            } else {

                operations.add(group("deviceId", "year").
                        max("maxSpeed").as("maxSpeed"));
            }

        } else if (conditionBean.getType().equalsIgnoreCase("hour")) {

            labels = FormatUtils.getHoursLables();
            AggregationOperation match = null;
            if (conditionBean.getDeviceIds().size() > 0) {

                match = match(criteria.and("updateTime").
                        gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                        .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59")).and("deviceId").in(conditionBean.getDeviceIds()));
            } else {

                match = match(criteria.and("updateTime").
                        gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                        .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59")));
            }
            operations.add(match);

            operations.add(group("deviceId", "year", "month", "day", "hour").
                    sum("dataSum").as("dataSum").max("maxSpeed").as("maxSpeed"));
        }
        List<NetDataHistoryStatus> results = netStatusRepository.getNetData(operations);

        //根据deviceId对数据进行分类，组成数组
        Map<String, List<NetDataHistoryStatus>> map = results.stream().collect(groupingBy(NetDataHistoryStatus::getDeviceId));

		/*results.parallelStream().forEach( item -> {

			List<NetDataHistoryStatus> arrayList = map.get(item.getDeviceId());
			if(ObjectUtils.isEmpty(arrayList)){

				arrayList = new ArrayList<>();
				map.put(item.getDeviceId(),new ArrayList<>());
			}
			arrayList.add(item);
			map.replace(item.getDeviceId(),arrayList);
		});*/

        return getSpeedOrSum(conditionBean, map, labels);
    }

    private CopsecResult getSpeedOrSum(ConditionBean conditionBean, Map<String, List<NetDataHistoryStatus>> map, ArrayList<String> labels) {

        ChartData sum = new ChartData();
        sum.setLabels(labels);
        List<DatasetBean> datasets = new ArrayList<>();
        map.entrySet().stream().forEach(entry -> {

            List<NetDataHistoryStatus> list = entry.getValue();
            HashMap<String, Object> valueMaps = Maps.newHashMap();
            list.parallelStream().forEach(item -> {

                if (conditionBean.isSum()) {

                    valueMaps.put(item.getDate(conditionBean.getType()), item.getDataSum());
                } else {

                    valueMaps.put(item.getDate(conditionBean.getType()), item.getMaxSpeed());
                }

            });
            List<Object> _data = Lists.newLinkedList();
            DatasetBean _sum = new DatasetBean();
            _sum.setBorderColor(DeviceIdUtils.getColor(entry.getKey()));
            _sum.setBackgroundColor(_sum.getBorderColor());
//            if (entry.getKey().endsWith(Resources.MUTTIPlEDEVICE)) {
//
//                _sum.setLabel(DevicePools.getInstance().getDevice(entry.getKey()) == null ? entry.getKey() :
//                        DevicePools.getInstance().getDevice(entry.getKey()).getData().getName() + "-内端");
//            } else {
//
//                _sum.setLabel(DevicePools.getInstance().getDevice(entry.getKey()) == null ? entry.getKey() :
//                        DevicePools.getInstance().getDevice(entry.getKey()).getData().getName());
//
//            }
            labels.forEach(label -> {

                Object o = valueMaps.get(label);
                if (ObjectUtils.isEmpty(o)) {

                    _data.add(0);

                } else {

                    _data.add(o);
                }
            });
            _sum.setData(_data);
            datasets.add(_sum);
        });

        sum.setDatasets(datasets);

        return CopsecResult.success(sum);
    }

    @Override
    public CopsecResult getProtocol(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "handleConnectCount", "handleNetDataSize", "updateTime"));
        //安天统计
        getOperationList(conditionBean, operationList, criteria);

        List<ProtocolHistoryStatus> result = protocolStatusRepository.getProtocolData(operationList);
        //根据deviceId对数据进行分类，组成数组
        Map<String, List<ProtocolHistoryStatus>> statusMap = new HashMap<>();
        if (conditionBean.getByDevice().equals("taskName")) {

            statusMap = result.stream().collect(groupingBy(d -> {
                return d.getTaskName() + "-" + d.getDeviceId();
            }));
        } else {

            statusMap = result.stream().collect(groupingBy(ProtocolHistoryStatus::getTaskName));
        }
		/*//根据deviceId对数据进行分类，组成数组
		result.parallelStream().forEach( item -> {

			String id = conditionBean.getByDevice().equals("taskName")?item.getDeviceId()+"-"+item.getTaskName():item.getDeviceId();
			List<ProtocolHistoryStatus> arrayList =statusMap.get(id);
			if(ObjectUtils.isEmpty(arrayList)){

				arrayList = new ArrayList<>();
				statusMap.put(id,new ArrayList<>());

			}
			arrayList.add(item);
			statusMap.replace(id,arrayList);
		});*/

        return getProtocolData(conditionBean, statusMap, labels);
    }

    private CopsecResult getProtocolData(ConditionBean conditionBean, Map<String, List<ProtocolHistoryStatus>> map,
                                         List<String> labels) {

        ChartData sum = new ChartData();
        sum.setLabels(labels);

        List<DatasetBean> datasets = new ArrayList<>();
        map.entrySet().stream().forEach(entry -> {

            List<ProtocolHistoryStatus> list = entry.getValue();
            HashMap<String, Object> valuesMaps = Maps.newHashMap();
            list.stream().forEach(d -> {

                if (conditionBean.getReference().equals("handleConnectCount")) {

                    valuesMaps.put(d.getDate(conditionBean.getType()), d.getHandleConnectCount());
                } else {

                    valuesMaps.put(d.getDate(conditionBean.getType()), d.getHandleNetDataSize());
                }
            });
            List<Object> _data = Lists.newLinkedList();
            DatasetBean _sum = new DatasetBean();
            setSumStyles(_sum, conditionBean, entry.getKey());
            labels.forEach(label -> {

                Object o = valuesMaps.get(label);
                if (ObjectUtils.isEmpty(o)) {

                    _data.add(0);
                } else {

                    _data.add(o);
                }
            });
            _sum.setData(_data);
            datasets.add(_sum);
        });

        sum.setDatasets(datasets);

        return CopsecResult.success(sum);
    }

    @Override
    public CopsecResult getDbData(ConditionBean conditionBean) {

        if (logger.isDebugEnabled()) {

            logger.debug("get db data {}", conditionBean);
        }

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "storageCount", "updateTime"));
        getOperationList(conditionBean, operationList, criteria);

        List<DBSyncHistoryStatus> result = dbStatusRepository.getDbData(operationList);
        Map<String, List<DBSyncHistoryStatus>> dbMap = new HashMap<>();
        //根据deviceId对数据进行分类，组成数组
        if (conditionBean.getByDevice().equals("taskName")) {

            dbMap = result.stream().collect(groupingBy(item -> {
                return item.getDeviceId() + "-" + item.getTaskName();
            }));
        } else {

            dbMap = result.stream().collect(groupingBy(DBSyncHistoryStatus::getDeviceId));
        }

        return getDBData(conditionBean, dbMap, labels);
    }

    private CopsecResult getDBData(ConditionBean conditionBean, Map<String, List<DBSyncHistoryStatus>> map,
                                   List<String> labels) {

        ChartData sum = new ChartData();
        sum.setLabels(labels);
        List<DatasetBean> datasets = new ArrayList<>();
        map.entrySet().stream().forEach(entry -> {

            List<DBSyncHistoryStatus> list = entry.getValue();
            HashMap<String, Object> valuesMap = Maps.newHashMap();
            list.stream().forEach(d -> {

                if (conditionBean.getReference().equals("gatherCount")) {

                    valuesMap.put(d.getDate(conditionBean.getType()), d.getGatherCount());
                } else {

                    valuesMap.put(d.getDate(conditionBean.getType()), d.getStorageCount());
                }
            });
            List<Long> datas = new ArrayList<>();
            DatasetBean _sum = new DatasetBean();
            setSumStyles(_sum, conditionBean, entry.getKey());
            labels.forEach(label -> {

                Object o = valuesMap.get(label);
                if (!ObjectUtils.isEmpty(o)) {

                    datas.add((long) o);
                } else {

                    datas.add(0L);
                }
            });
            _sum.setData(datas);
            datasets.add(_sum);
        });

        sum.setDatasets(datasets);

        return CopsecResult.success(sum);
    }

    private void setSumStyles(DatasetBean sum, ConditionBean conditionBean, String deviceId) {

        sum.setBorderColor(DeviceIdUtils.getColor(deviceId));
        sum.setBackgroundColor(sum.getBorderColor());
        if (!conditionBean.getByDevice().equals("taskName")) {

//            sum.setLabel(DeviceIdUtils.getDeviceName(deviceId));
        } else {

            sum.setLabel(deviceId);
        }
    }

    private void getOperationList(ConditionBean conditionBean, List<AggregationOperation> operationList, Criteria criteria) {

        if (conditionBean.getType().equalsIgnoreCase("hour")) {

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

            if (conditionBean.getByDevice().equals("taskName")) {

                operationList.add(group("deviceId", conditionBean.getByDevice(), "year", "month", "day", "hour").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            } else {
                operationList.add(group(conditionBean.getByDevice(), "year", "month", "day", "hour").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            }
        } else if (conditionBean.getType().equalsIgnoreCase("day")) {

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
            if (conditionBean.getByDevice().equals("taskName")) {

                operationList.add(group("deviceId", conditionBean.getByDevice(), "year", "month", "day").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            } else {
                operationList.add(group(conditionBean.getByDevice(), "year", "month", "day").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            }
        } else if (conditionBean.getType().equalsIgnoreCase("month")) { //按月统计

            //当指定月进行查询时，应该根据年和月来进行match
            String[] startTime = conditionBean.getStart().split("-");
            String[] endTime = conditionBean.getEnd().split("-");
            operationList.add(match(criteria.and("year").
                    gte(Integer.valueOf(startTime[0])).lte(Integer.valueOf(endTime[0])).
                    and("month").gte(Integer.valueOf(startTime[1]))
                    .lte(Integer.valueOf(endTime[1]))));
            if (conditionBean.getByDevice().equals("taskName")) {

                operationList.add(group("deviceId", conditionBean.getByDevice(), "year", "month").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            } else {
                operationList.add(group(conditionBean.getByDevice(), "year", "month").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            }
        } else if (conditionBean.getType().equalsIgnoreCase("year")) {//按年统计

            //当指定年的时候，应该根据year来对比
            operationList.add(match(criteria.and("year").
                    gte(Integer.valueOf(conditionBean.getStart()))
                    .lte(Integer.valueOf(conditionBean.getEnd()))));
            if (conditionBean.getByDevice().equals("taskName")) {

                operationList.add(group("deviceId", conditionBean.getByDevice(), "year").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            } else {

                operationList.add(group("deviceId", "year").
                        sum(conditionBean.getReference()).as(conditionBean.getReference()));
            }
        }
    }

    private List<String> getLable(ConditionBean conditionBean) {

        List<String> labels = new ArrayList<>();
        if (conditionBean.getType().equalsIgnoreCase("day")) {

            labels = FormatUtils.getDaySequenceWith(conditionBean.getStart(), conditionBean.getEnd());

        } else if (conditionBean.getType().equalsIgnoreCase("month")) { //按月统计

            labels = FormatUtils.getMonthSequenceWith(conditionBean.getStart(), conditionBean.getEnd());

        } else if (conditionBean.getType().equalsIgnoreCase("year")) {//按年统计

            labels = FormatUtils.getYearSequenceWith(conditionBean.getStart(), conditionBean.getEnd());

        } else if (conditionBean.getType().equalsIgnoreCase("hour")) {

            labels = FormatUtils.getHoursLables();
        }
        return labels;
    }

    @Override
    public CopsecResult getFileData(ConditionBean conditionBean) {

        if (logger.isDebugEnabled()) {

            logger.debug("get file statistic by condition {}", conditionBean);
        }
        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

        if (conditionBean.getType().equalsIgnoreCase("hour")) {

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
            if (conditionBean.getByDevice().equals("taskName")) {

                if (conditionBean.isCount()) {

                    operationList.add(group(conditionBean.getByDevice(), "year", "month", "day", "hour").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group(conditionBean.getByDevice(), "year", "month", "day", "hour").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }

            } else {

                if (conditionBean.isCount()) {

                    operationList.add(group("deviceId", "year", "month", "day", "hour").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group("deviceId", "year", "month", "day", "hour").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }
            }

        } else if (conditionBean.getType().equalsIgnoreCase("day")) {

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
            if (conditionBean.getByDevice().equals("taskName")) {

                if (conditionBean.isCount()) {

                    operationList.add(group(conditionBean.getByDevice(), "year", "month", "day").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group(conditionBean.getByDevice(), "year", "month", "day").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }

            } else {

                if (conditionBean.isCount()) {

                    operationList.add(group("deviceId", "year", "month", "day").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group("deviceId", "year", "month", "day").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }
            }
        } else if (conditionBean.getType().equalsIgnoreCase("month")) { //按月统计

            /**
             * 当指定月进行查询时，应该根据年和月来进行match
             */
            String[] startTime = conditionBean.getStart().split("-");
            String[] endTime = conditionBean.getEnd().split("-");
            operationList.add(match(criteria.and("year").
                    gte(Integer.valueOf(startTime[0])).lte(Integer.valueOf(endTime[0])).
                    and("month").gte(Integer.valueOf(startTime[1]))
                    .lte(Integer.valueOf(endTime[1]))));
            if (conditionBean.getByDevice().equals("taskName")) {

                if (conditionBean.isCount()) {

                    operationList.add(group(conditionBean.getByDevice(), "year", "month").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group(conditionBean.getByDevice(), "year", "month").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }
            } else {

                if (conditionBean.isCount()) {

                    operationList.add(group("deviceId", "year", "month").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group("deviceId", "year", "month").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }
            }
        } else if (conditionBean.getType().equalsIgnoreCase("year")) {//按年统计

            //当指定年的时候，应该根据year来对比
            operationList.add(match(criteria.and("year").
                    gte(Integer.valueOf(conditionBean.getStart()))
                    .lte(Integer.valueOf(conditionBean.getEnd()))));
            if (conditionBean.getByDevice().equals("taskName")) {

                if (conditionBean.isCount()) {

                    operationList.add(group(conditionBean.getByDevice(), "year").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group(conditionBean.getByDevice(), "year").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }
            } else {

                if (conditionBean.isCount()) {

                    operationList.add(group("deviceId", "year").
                            sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
                } else {

                    operationList.add(group("deviceId", "year").
                            sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
                }
            }
        }

        List<FileSyncHistoryStatus> result = fileStatusRepository.getFileData(operationList);

        Map<String, List<FileSyncHistoryStatus>> fileMap = new HashMap<>();
        /**
         * 根据deviceId对数据进行分类，组成数组
         */
        if (conditionBean.getByDevice().equals("taskName")) {

            fileMap = result.stream().collect(groupingBy(FileSyncHistoryStatus::getTaskName));
        } else {

            fileMap = result.stream().collect(groupingBy(FileSyncHistoryStatus::getDeviceId));
        }
		/*result.parallelStream().forEach( item -> {

			String id = conditionBean.getByDevice().equals("taskName")?item.getTaskName():item.getDeviceId();
			List<FileSyncHistoryStatus> arrayList =fileMap.get(id);
			if(ObjectUtils.isEmpty(arrayList)){

				arrayList = new ArrayList<>();
				fileMap.put(id,new ArrayList<>());

			}
			arrayList.add(item);
			fileMap.replace(id,arrayList);
		});*/

        return getFileData(conditionBean, fileMap, labels);
    }

    private CopsecResult getFileData(ConditionBean conditionBean, Map<String, List<FileSyncHistoryStatus>> map,
                                     List<String> labels) {

        ChartData sum = new ChartData();
        sum.setLabels(labels);

        List<DatasetBean> datasets = new ArrayList<>();
        map.entrySet().stream().forEach(entry -> {

            List<FileSyncHistoryStatus> list = entry.getValue();
            HashMap<String, Object> valuesMap = Maps.newHashMap();
            list.stream().forEach(d -> {

                if (conditionBean.isCount()) {

                    valuesMap.put(d.getDate(conditionBean.getType()), d.getGatherCount() + d.getStorageCount());
                } else {

                    valuesMap.put(d.getDate(conditionBean.getType()), d.getGatherSizeSum() + d.getStorageSizeSum());
                }
            });
            DatasetBean _sum = new DatasetBean();
            List<Object> _data = Lists.newLinkedList();
            setSumStyles(_sum, conditionBean, entry.getKey());
            labels.forEach(label -> {

                Object o = valuesMap.get(label);
                if (!ObjectUtils.isEmpty(o)) {

                    _data.add(o);
                } else {

                    _data.add(0L);
                }
            });
            _sum.setData(_data);
            datasets.add(_sum);
        });

        sum.setDatasets(datasets);

        return CopsecResult.success(sum);
    }

    @Override
    public CopsecResult getTotal4Net(ConditionBean conditionBean) {

        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        ArrayList<String> labels = Lists.newArrayList();

        operations.add(project("deviceId", "year", "month", "day", "hour", "dataSum", "maxSpeed", "updateTime"));
        //安天统计
        if (conditionBean.getType().equalsIgnoreCase("day")) {
            labels = FormatUtils.getDaySequenceWith(conditionBean.getStart(), conditionBean.getEnd());
            operations.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
            operations.add(group("year", "month", "day").
                    sum("dataSum").as("dataSum"));
        } else if (conditionBean.getType().equalsIgnoreCase("month")) { //按月统计

            labels = FormatUtils.getMonthSequenceWith(conditionBean.getStart(), conditionBean.getEnd());
            //当指定月进行查询时，应该根据年和月来进行match
            String[] startTime = conditionBean.getStart().split("-");
            String[] endTime = conditionBean.getEnd().split("-");
            operations.add(match(criteria.and("year").
                    gte(Integer.valueOf(startTime[0])).lte(Integer.valueOf(endTime[0])).
                    and("month").gte(Integer.valueOf(startTime[1]))
                    .lte(Integer.valueOf(endTime[1]))));
            operations.add(group("year", "month").
                    sum("dataSum").as("dataSum"));
        } else if (conditionBean.getType().equalsIgnoreCase("year")) {//按年统计

            labels = FormatUtils.getYearSequenceWith(conditionBean.getStart(), conditionBean.getEnd());
            //当指定年的时候，应该根据year来对比
            operations.add(match(criteria.and("year").
                    gte(Integer.valueOf(conditionBean.getStart()))
                    .lte(Integer.valueOf(conditionBean.getEnd()))));
            operations.add(group("deviceId", "year").
                    sum("dataSum").as("dataSum"));
        } else if (conditionBean.getType().equalsIgnoreCase("hour")) {

            labels = FormatUtils.getHoursLables();
            operations.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
            operations.add(group("year", "month", "day", "hour").
                    sum("dataSum").as("dataSum"));
        }
        List<NetDataHistoryStatus> results = netStatusRepository.getNetData(operations);

        Map<String, List<NetDataHistoryStatus>> map = new HashMap<>();
        ArrayList<NetDataHistoryStatus> arrayList = Lists.newArrayList();
        results.stream().forEach(item -> {

            arrayList.add(item);
        });
        map.putIfAbsent("total", arrayList);

        ChartData sum = new ChartData();
        sum.setLabels(labels);
        final ArrayList<String> _labels = labels;
        List<DatasetBean> datasets = new ArrayList<>();
        map.entrySet().stream().forEach(entry -> {

            List<NetDataHistoryStatus> list = entry.getValue();
            StringBuilder builder = new StringBuilder();
            list.stream().forEach(d -> {

                builder.append(d.getDate(conditionBean.getType()));
            });
            List<Long> datas = new ArrayList<>();
            DatasetBean _sum = new DatasetBean();
            _sum.setBorderColor(DeviceIdUtils.getColor(3));
            _sum.setBackgroundColor(_sum.getBorderColor());
            _sum.setLabel("今日各时段流量");
            _labels.forEach(label -> {

                if (builder.toString().contains(label)) {

                    list.stream().forEach(d -> {

                        if (d.getDate(conditionBean.getType()).equals(label)) {

                            datas.add(d.getDataSum());
                        }
                    });
                } else {

                    datas.add(0L);
                }
            });
            _sum.setData(datas);
            datasets.add(_sum);
        });
        sum.setDatasets(datasets);
        return CopsecResult.success(sum);
    }

    @Override
    public CopsecResult getTotal4Db(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "storageCount", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
        operationList.add(group("year", "month", "day").
                sum(conditionBean.getReference()).as(conditionBean.getReference()));

        List<DBSyncHistoryStatus> result = dbStatusRepository.getDbData(operationList);
        Map<String, List<DBSyncHistoryStatus>> dbMap = new HashMap<>();

        ArrayList<DBSyncHistoryStatus> arrayList = Lists.newArrayList();
        result.stream().forEach(item -> {

            arrayList.add(item);
        });
        dbMap.putIfAbsent(conditionBean.getTitle(), arrayList);
        return getDBData(conditionBean, dbMap, labels);
    }

    @Override
    public CopsecResult getTotal4File(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

        if (conditionBean.isCount()) {

            operationList.add(group("year", "month", "day").
                    sum("gatherCount").as("gatherCount").sum("storageCount").as("storageCount"));
        } else {

            operationList.add(group("year", "month", "day").
                    sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));
        }
        List<FileSyncHistoryStatus> result = fileStatusRepository.getFileData(operationList);

        Map<String, List<FileSyncHistoryStatus>> fileMap = new HashMap<>();
        ArrayList<FileSyncHistoryStatus> arrayList = Lists.newArrayList();
        result.stream().forEach(item -> {

            arrayList.add(item);
        });
        fileMap.putIfAbsent(conditionBean.getTitle(), arrayList);
        return getFileData(conditionBean, fileMap, labels);
    }

    @Override
    public CopsecResult getTotal4Protocol(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "handleConnectCount", "handleNetDataSize", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

        operationList.add(group("year", "month", "day").
                sum(conditionBean.getReference()).as(conditionBean.getReference()));

        List<ProtocolHistoryStatus> result = protocolStatusRepository.getProtocolData(operationList);
        Map<String, List<ProtocolHistoryStatus>> statusMap = new HashMap<>();
        ArrayList<ProtocolHistoryStatus> arrayList = Lists.newArrayList();
        result.stream().forEach(item -> {

            arrayList.add(item);

        });
        statusMap.replace(conditionBean.getTitle(), arrayList);
        return getProtocolData(conditionBean, statusMap, labels);
    }


    @Override
    public CopsecResult countTotal(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);

        if (conditionBean.getStatistiscalType().equals("net")) {

            operationList.add(project("deviceId", "year", "month", "day", "hour", "dataSum", "maxSpeed", "maxSpeedTime", "updateTime"));

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

            AggregationOperation groupOperate = group("year", "month", "day").sum("dataSum").as("total");
            operationList.add(groupOperate);

            StatisticalResultBean sum = netStatusRepository.countTotal(operationList);
            operationList.remove(groupOperate);

            operationList.add(group("deviceId", "year", "month", "day").sum("dataSum").as("dataSum"));
            operationList.add(sort(Direction.ASC, "dataSum"));
            operationList.add(group("year", "month", "day").last("dataSum").as("max").last("deviceId").as("maxDeviceId")
                    .first("dataSum").as("min").first("deviceId").as("minDeviceId"));

            StatisticalResultBean maxAndMin = netStatusRepository.countTotal(operationList);

            StatisticalSumBean sumBean = new StatisticalSumBean();
            if (!ObjectUtils.isEmpty(sum)) {

                sumBean.setDataSumTotal("" + sum.getTotal());
            } else {

                sumBean.setDataSumTotal("0");
            }
            if (!ObjectUtils.isEmpty(maxAndMin)) {

//                sumBean.setDataMax("流量最大设备：" + DeviceIdUtils.getDeviceName(maxAndMin.getMaxDeviceId()) + "[" + FormatUtils.getFormatSizeByte(maxAndMin.getMax()) + "]");
//                sumBean.setDataMin("流量最小设备：" + DeviceIdUtils.getDeviceName(maxAndMin.getMinDeviceId()) + "[" + FormatUtils.getFormatSizeByte(maxAndMin.getMin()) + "]");
            } else {

                sumBean.setDataMax("流量最大设备:无[0]");
                sumBean.setDataMin("流量最小设备:无[0]");
            }
            return CopsecResult.success(sumBean);
        } else if (conditionBean.getStatistiscalType().equals("file")) {

            operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

            AggregationOperation groupOperate = group("year", "month", "day")
                    .sum("gatherCount").as("gatherCountTotal").
                            sum("gatherSizeSum").as("gatherSizeTotal").
                            sum("storageCount").as("storageCountTotal").sum("storageSizeSum").as("storageSizeTotal");
            operationList.add(groupOperate);

            StatisticalResultBean sum = fileStatusRepository.countTotal(operationList);
            StatisticalSumBean sumBean = new StatisticalSumBean();
            if (!ObjectUtils.isEmpty(sum)) {

                sumBean.setDataSumTotal((sum.getGatherSizeTotal() + sum.getStorageSizeTotal()) + "");
                sumBean.setDataMax("采集总量：" + sum.getGatherCountTotal() + "个，总大小[" + FormatUtils.getFormatSizeByte(sum.getGatherSizeTotal()) + "]");
                sumBean.setDataMin("上传总量：" + sum.getStorageCountTotal() + "个，总大小[" + FormatUtils.getFormatSizeByte(sum.getStorageSizeTotal()) + "]");
            } else {
                sumBean.setDataSumTotal("0");
                sumBean.setDataMax("采集总量：0个，总大小：0");
                sumBean.setDataMin("上传总量：0个，总大小：0");
            }
            return CopsecResult.success(sumBean);
        } else if (conditionBean.getStatistiscalType().equals("db")) {

            operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "storageCount", "updateTime"));
            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

            AggregationOperation groupOperate = group("year", "month", "day").sum("gatherCount").as("gatherCountTotal")
                    .sum("storageCount").as("storageCountTotal");
            operationList.add(groupOperate);

            StatisticalResultBean countBean = dbStatusRepository.countTotal(operationList);

            operationList.remove(groupOperate);

            operationList.add(group("deviceId", "year", "month", "day").sum("gatherCount").as("gatherCount")
                    .sum("storageCount").as("storageCount"));

            operationList.add(sort(Direction.ASC, "gatherCount", "storageCount"));
            operationList.add(group("year", "month", "day").
                    last("gatherCount").as("gatherMax").last("deviceId").as("gatherMaxDeviceId")
                    .first("gatherCount").as("gatherMin").last("deviceId").as("gatherMinDeviceId")
                    .last("storageCount").as("storageMax").last("deviceId").as("storageMaxDeviceId")
                    .first("storageCount").as("storageMin").first("deviceId").as("storageMinDeviceId"));

            StatisticalResultBean resultBean = dbStatusRepository.countTotal(operationList);

            StatisticalSumBean sum = new StatisticalSumBean();
            if (!ObjectUtils.isEmpty(countBean)) {

                sum.setDataSumTotal(countBean.getGatherCountTotal() + countBean.getStorageCountTotal() + "");
            } else {

                sum.setDataSumTotal("0");
            }
            if (!ObjectUtils.isEmpty(resultBean)) {

//                sum.setDataMax("采集总量最大：" + DeviceIdUtils.getDeviceName(resultBean.getGatherMaxDeviceId()) + " [" + resultBean.getGatherMax() +
//                        "]最小设备：" + DeviceIdUtils.getDeviceName(resultBean.getGatherMinDeviceId()) + " [" + resultBean.getGatherMin() + "]");
//
//                sum.setDataMin("上传总量最大：" + DeviceIdUtils.getDeviceName(resultBean.getStorageMaxDeviceId()) + " [" + resultBean.getStorageMax() +
//                        "]最小设备：" + DeviceIdUtils.getDeviceName(resultBean.getStorageMinDeviceId()) + " [" + resultBean.getStorageMin() + "]");
            } else {

                sum.setDataMax("采集总量最大设备:无[0]" + "最小设备:无[0]");

                sum.setDataMin("上传总量最大设备:无[0]" + "最小设备:无[0]");
            }
            return CopsecResult.success(sum);

        } else if (conditionBean.getStatistiscalType().equals("protocol")) {

            operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "handleConnectCount", "handleNetDataSize", "updateTime"));
            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

            AggregationOperation groupOperate = group("year", "month", "day").
                    sum("handleConnectCount").as("connectionCount")
                    .sum("handleNetDataSize").as("connectionSize");

            operationList.add(groupOperate);
            StatisticalResultBean countBean = protocolStatusRepository.countTotal(operationList);

            operationList.remove(groupOperate);

            operationList.add(group("year", "month", "day", "deviceId")
                    .sum("handleConnectCount").as("connectionCount")
                    .sum("handleNetDataSize").as("connectionSize"));

            operationList.add(sort(Direction.ASC, "connectionCount", "connectionSize"));
            operationList.add(group("year", "month", "day")
                    .last("connectionCount").as("connectionMax").last("deviceId").as("connectionMaxDeviceId")
                    .first("connectionCount").as("connectionMin").first("deviceId").as("connectionMinDeviceId")
                    .last("connectionSize").as("sizeMax").last("deviceId").as("sizeMaxDeviceId")
                    .first("connectionSize").as("sizeMin").last("deviceId").as("sizeMinDeviceId"));

            StatisticalResultBean resultBean = protocolStatusRepository.countTotal(operationList);

            StatisticalSumBean sum = new StatisticalSumBean();

            if (!ObjectUtils.isEmpty(countBean)) {

                sum.setDataSumTotal("" + countBean.getConnectionSize());
            } else {

                sum.setDataSumTotal("0");
            }
            if (!ObjectUtils.isEmpty(resultBean)) {

//                sum.setDataMax("连接最大设备：" + DeviceIdUtils.getDeviceName(resultBean.getConnectionMaxDeviceId()) + " [" + resultBean.getConnectionMax() +
//                        "]最小设备：" + DeviceIdUtils.getDeviceName(resultBean.getConnectionMinDeviceId()) + " [" + resultBean.getConnectionMin() + "]");
//
//                sum.setDataMin("流量最大设备：" + DeviceIdUtils.getDeviceName(resultBean.getSizeMaxDeviceId()) + "[" + FormatUtils.getFormatSizeByte(resultBean.getSizeMax()) +
//                        "]最小设备：" + DeviceIdUtils.getDeviceName(resultBean.getSizeMinDeviceId()) + "[" + FormatUtils.getFormatSizeByte(resultBean.getSizeMin()) + "]");
            } else {

                sum.setDataMax("连接最大设备:无[0]" + "最小设备:无[0]");

                sum.setDataMin("流量最大设备:无[0]" + "最小设备:无[0]");
            }
            return CopsecResult.success(sum);
        } else {

            return CopsecResult.failed("数据类型不支持");
        }
    }

    @Override
    public CopsecResult countDay(ConditionBean conditionBean) {

        return null;
    }

    /**
     * 计算文件发送任务总量，当天量
     *
     * @param conditionBean
     * @return
     */
    @Override
    public CopsecResult countFileSynHistoryStatus(ConditionBean conditionBean) {


        Map<String, FileStatisticResultBean> daysMap = getDays();
        Map<String, FileStatisticResultBean> todaySum = getSumByCondition(conditionBean, true);
        Map<String, FileStatisticResultBean> totalSum = getSumByCondition(conditionBean, false);

        totalSum.entrySet().stream().forEach(entry -> {

            FileStatisticResultBean bean = entry.getValue();
            FileStatisticResultBean dayBean = daysMap.get(entry.getKey());
            if (!ObjectUtils.isEmpty(dayBean)) {

                bean.setDays(dayBean.getDays());
            }
            FileStatisticResultBean todayBean = todaySum.get(entry.getKey());
            if (!ObjectUtils.isEmpty(todayBean)) {

                bean.setGatherSpeedToday(todayBean.getGatherSpeedToday());
                bean.setStorageSpeedToday(todayBean.getStorageSpeedToday());
                bean.setDataSizeTodayStr(todayBean.getDataSizeTodayStr());
                bean.setGatherSumToday(todayBean.getGatherSumToday());
                bean.setStorageSumToday(todayBean.getStorageSumToday());
                bean.setGatherCountToday(todayBean.getGatherCountToday());
                bean.setStorageCountToday(todayBean.getStorageCountToday());
            }

            ;
        });

        return CopsecResult.success(totalSum);
    }

    /**
     * 计算总量
     *
     * @param conditionBean
     * @param countToday
     * @return
     */
    private Map<String, FileStatisticResultBean> getSumByCondition(ConditionBean conditionBean, boolean countToday) {

        List<AggregationOperation> operationList = Lists.newArrayList();

        operationList.add(project("taskName", "year", "month", "day", "hour",
                "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "gatherTime", "storageTime", "updateTime"));
        /**
         * 计算总量
         */
        if (countToday) {

            Criteria criteria = new Criteria();
            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
        }
        operationList.add(group("taskName")
                .sum("gatherCount").as("gatherCount")
                .sum("storageCount").as("storageCount")
                .sum("gatherSizeSum").as("gatherSizeSum")
                .sum("storageSizeSum").as("storageSizeSum")
                .sum("gatherTime").as("gatherTime")
                .sum("storageTime").as("storageTime").addToSet("taskName").as("taskName"));

        List<FileStatisticResultBean> result = fileStatusRepository.countFileStatistic(operationList);
        Map<String, FileStatisticResultBean> map = Maps.newConcurrentMap();
        result.stream().forEach(item -> {

            if (!map.containsKey(item.getTaskName())) {

                if (!countToday) {

                    item.setGatherSpeedTotal(FormatUtils.getSpeed(item.getGatherSizeSum(), item.getGatherTime()));
                    item.setStorageSpeedTotal(FormatUtils.getSpeed(item.getStorageSizeSum(), item.getStorageTime()));
                    item.setDataSizeTotalStr(FormatUtils.getFormatSizeByte(item.getGatherSizeSum() + item.getStorageSizeSum()));
                    item.setGatherSumTotal(FormatUtils.getFormatSizeByte(item.getGatherSizeSum()));
                    item.setStorageSumTotal(FormatUtils.getFormatSizeByte(item.getStorageSizeSum()));
                    item.setGatherCountTotal(item.getGatherCount());
                    item.setStorageCountTotal(item.getStorageCount());
                } else {

                    item.setGatherSpeedToday(FormatUtils.getSpeed(item.getGatherSizeSum(), item.getGatherTime()));
                    item.setStorageSpeedToday(FormatUtils.getSpeed(item.getStorageSizeSum(), item.getStorageTime()));
                    item.setDataSizeTodayStr(FormatUtils.getFormatSizeByte(item.getGatherSizeSum() + item.getStorageSizeSum()));
                    item.setGatherSumToday(FormatUtils.getFormatSizeByte(item.getGatherSizeSum()));
                    item.setStorageSumToday(FormatUtils.getFormatSizeByte(item.getStorageSizeSum()));
                    item.setGatherCountToday(item.getGatherCount());
                    item.setStorageCountToday(item.getStorageCount());
                }

                map.put(item.getTaskName(), item);
            }
        });
        return map;
    }

    /**
     * 计算运行天数
     */
    private Map<String, FileStatisticResultBean> getDays() {

        List<AggregationOperation> operationList = Lists.newArrayList();

        operationList.add(project("taskName", "year", "month", "day", "hour",
                "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "gatherTime", "storageTime", "updateTime"));
        /**
         * 计算运行天数
         */
        AggregationOperation sortByUpdateTime = sort(Direction.DESC, "updateTime", "taskName");
        operationList.add(sortByUpdateTime);
        AggregationOperation groupByUpdateTime = group("taskName")
                .max("updateTime").as("lastTime")
                .min("updateTime").as("oldTime").addToSet("taskName").as("taskName");
        operationList.add(groupByUpdateTime);
        AggregationOperation projection = project("taskName", "lastTime", "oldTime");
        operationList.add(projection);
        List<FileStatisticResultBean> daysResult = fileStatusRepository.countFileStatistic(operationList);
        Map<String, FileStatisticResultBean> map = Maps.newConcurrentMap();
        daysResult.stream().forEach(item -> {

            if (!map.containsKey(item.getTaskName())) {

                Date last = item.getLastTime();
                Date old = item.getOldTime();
                item.setDays(FormatUtils.getFormatTimeMillis(last.getTime() - old.getTime()));
                map.put(item.getTaskName(), item);
            }
        });
        return map;
    }

    @Override
    public List<FileSyncHistoryStatus> getTotalSize4Yesterday(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("taskName").exists(true);
        List<String> labels = getLable(conditionBean);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

        if (conditionBean.getDeviceIds().size() > 0) {

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59")).and("taskName").in(conditionBean.getDeviceIds())));
        } else {

            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
        }

        operationList.add(group("taskName", "year", "month", "day").
                sum("gatherSizeSum").as("gatherSizeSum").sum("storageSizeSum").as("storageSizeSum"));

        List<FileSyncHistoryStatus> result = fileStatusRepository.getFileData(operationList);

        return result;
    }

    @Override
    public FileSyncHistoryStatus getFileCountToday(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = Lists.newArrayList();
        Criteria criteria = new Criteria();
        criteria.where("taskName").exists(true);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:00:00"))));

        operationList.add(group("year", "month", "day").
                sum("gatherSizeSum").as("gatherSizeSum")
                .sum("storageSizeSum").as("storageSizeSum")
                .sum("gatherCount").as("gatherCount")
                .sum("storageCount").as("storageCount"));

        List<FileSyncHistoryStatus> resultList = fileStatusRepository.getFileData(operationList);

        return resultList.size() == 1 ? resultList.get(0) : new FileSyncHistoryStatus();
    }

    /**
     * 计算gather 百分比获取饼状图
     *
     * @param conditionBean
     * @return
     */
    @Override
    public CopsecResult getPrecentCountOfTask(ConditionBean conditionBean) {

        FileSyncHistoryStatus total = getFileCountToday(conditionBean);

        if (total.getGatherCount() == 0) {

            total.setGatherCount(1);
        }

        List<FileSyncHistoryStatus> result = getCountValues(conditionBean);

        PieData pieData = new PieData();
        int count = 0;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.HALF_UP);
        double totalCount = Double.valueOf(total.getGatherCount());
        for (FileSyncHistoryStatus status : result) {

            pieData.getLabels().add(status.getTaskName());
            double precent = (Double.valueOf(status.getGatherCount()) / totalCount) * 100;
            if (pieData.getDatasets().size() == 0) {

                PieDataSets dataSets = new PieDataSets();
                pieData.getDatasets().add(dataSets);
            }
            pieData.getDatasets().get(0).getData().add(Double.valueOf(df.format(precent)));
            pieData.getDatasets().get(0).getBackgroundColor().add(DeviceIdUtils.getColor(count));
            if ((count++) == 3) {
                break;
            }
        }
        return CopsecResult.success(pieData);
    }

    /**
     * 计算storage百分比获取饼状图
     *
     * @param conditionBean
     * @return
     */
    @Override
    public CopsecResult getPrecentSumOfTask(ConditionBean conditionBean) {

        FileSyncHistoryStatus total = getFileCountToday(conditionBean);

        if (total.getStorageCount() == 0) {

            total.setStorageCount(1);
        }

        List<FileSyncHistoryStatus> result = getSumValues(conditionBean);

        PieData pieData = new PieData();
        int count = 0;
        double totalCount = Double.valueOf(total.getStorageCount());
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.HALF_UP);
        for (FileSyncHistoryStatus status : result) {

            pieData.getLabels().add(status.getTaskName());
            double precent = (Double.valueOf(status.getStorageCount()) / totalCount) * 100;
            if (pieData.getDatasets().size() == 0) {

                PieDataSets dataSets = new PieDataSets();
                pieData.getDatasets().add(dataSets);
            }
            pieData.getDatasets().get(0).getData().add(Double.valueOf(df.format(precent)));
            pieData.getDatasets().get(0).getBackgroundColor().add(DeviceIdUtils.getColor(count));
            if ((count++) == 3) {
                break;
            }
        }

        return CopsecResult.success(pieData);
    }


    private List<FileSyncHistoryStatus> getCountValues(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = Lists.newArrayList();
        Criteria criteria = new Criteria();
        criteria.where("taskName").exists(true);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:00:00"))));

        operationList.add(group("taskName", "year", "month", "day")
                .sum("gatherCount").as("gatherCount"));

        List<FileSyncHistoryStatus> result = fileStatusRepository.getFileData(operationList);

        List<FileSyncHistoryStatus> newList = Lists.newArrayList(result);
        /**
         * 降序取前四个值
         */
        Collections.sort(newList, new Comparator<FileSyncHistoryStatus>() {
            @Override
            public int compare(FileSyncHistoryStatus o1, FileSyncHistoryStatus o2) {


                if (ObjectUtils.isEmpty(o1) && ObjectUtils.isEmpty(o2)) {

                    if (o1.getGatherCount() < o2.getGatherCount()) {

                        return -1;
                    } else if (o1.getGatherCount() == o2.getGatherCount()) {

                        return 0;
                    } else if (o1.getGatherCount() < o2.getGatherCount()) {

                        return 1;
                    }
                }
                return 0;
            }
        });

        return result;
    }


    private List<FileSyncHistoryStatus> getSumValues(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = Lists.newArrayList();
        Criteria criteria = new Criteria();
        criteria.where("taskName").exists(true);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "gatherSizeSum", "storageCount", "storageSizeSum", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:00:00"))));

        operationList.add(group("taskName", "year", "month", "day")
                .sum("storageCount").as("storageCount"));

        List<FileSyncHistoryStatus> result = fileStatusRepository.getFileData(operationList);

        List<FileSyncHistoryStatus> newList = Lists.newArrayList(result);
        /**
         * 降序取前四个值
         */
        Collections.sort(newList, new Comparator<FileSyncHistoryStatus>() {
            @Override
            public int compare(FileSyncHistoryStatus o1, FileSyncHistoryStatus o2) {

                if (o1.getStorageCount() < o2.getStorageCount()) {

                    return -1;
                } else if (o1.getStorageCount() == o2.getStorageCount()) {

                    return 0;
                } else if (o1.getStorageCount() < o2.getStorageCount()) {

                    return 1;
                }
                return 0;
            }
        });

        return result;
    }

    /**
     * gather百分比数值数据
     *
     * @param conditionBean
     * @return
     */
    @Override
    public CopsecResult getPrecentCountValues(ConditionBean conditionBean) {

        FileSyncHistoryStatus total = getFileCountToday(conditionBean);

        List<FileSyncHistoryStatus> result = getCountValues(conditionBean);

        int count = 0;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(0);
        df.setRoundingMode(RoundingMode.HALF_UP);
        List<FileStatisticResultBean> precentCountValues = Lists.newArrayList();
        for (FileSyncHistoryStatus status : result) {

            FileStatisticResultBean bean = new FileStatisticResultBean();

            double precent = (Double.valueOf(status.getGatherCount()) / Double.valueOf(total.getGatherCount())) * 100;
            bean.setTaskName(status.getTaskName());
            bean.setGatherCount(status.getGatherCount());
            bean.setPrecentGatherCount(df.format(precent) + "%");
            bean.setBackGroudColor(DeviceIdUtils.getColor(count));
            if ((count++) == 3) {
                break;
            }
            precentCountValues.add(bean);
        }
        return CopsecResult.success(precentCountValues);
    }

    /**
     * storage 百分比数据数据
     *
     * @param conditionBean
     * @return
     */
    @Override
    public CopsecResult getPrecentSumValues(ConditionBean conditionBean) {

        FileSyncHistoryStatus total = getFileCountToday(conditionBean);

        List<FileSyncHistoryStatus> result = getSumValues(conditionBean);
        int count = 0;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(0);
        df.setRoundingMode(RoundingMode.HALF_UP);
        List<FileStatisticResultBean> precentCountValues = Lists.newArrayList();
        for (FileSyncHistoryStatus status : result) {

            FileStatisticResultBean bean = new FileStatisticResultBean();

            double precent = (Double.valueOf(status.getStorageCount()) / Double.valueOf(total.getStorageCount())) * 100;
            bean.setTaskName(status.getTaskName());
            bean.setPrecentStorageCount(df.format(precent) + "%");
            bean.setStorageCount(status.getStorageCount());
            bean.setBackGroudColor(DeviceIdUtils.getColor(count));
            if ((count++) == 3) {
                break;
            }
            precentCountValues.add(bean);
        }
        return CopsecResult.success(precentCountValues);
    }

    /**
     * 获取当前网络接口总流量
     *
     * @param conditionBean
     * @return
     */
    @Override
    public CopsecResult getCurrentNetData(ConditionBean conditionBean) {

        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);
        ArrayList<String> labels = Lists.newArrayList();

        operations.add(project("deviceId", "year", "month", "day", "hour", "dataSum", "maxSpeed", "updateTime"));

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        operations.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " " + hour + ":01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " " + hour + ":59:59"))));
        operations.add(group("year", "month", "day", "hour").
                sum("dataSum").as("dataSum"));

        List<NetDataHistoryStatus> results = netStatusRepository.getNetData(operations);

        if (results.size() == 1) {

            return CopsecResult.success(results.get(0).getDataSum());
        } else {

            return CopsecResult.success(0);
        }
    }

    @Override
    public CopsecResult getDeviceIdsByTaskNams(List<String> taskNames) {


        return CopsecResult.success(fileStatusRepository.getDeviceIdsByTaskNames(taskNames));
    }

    @Override
    public List<String> getAlarmFileSyncTaskNames() {

        return fileSyncStatusRepository.getAlarmFileSyncTask();
    }

    @Override
    public CopsecResult getResourcesUse(boolean isCpu) {

        List<DeviceNowStatus> deviceList = deviceStatusRepository.findAll();

        if (isCpu) {

            List<DeviceNowStatus> _list = deviceList.stream().sorted(Comparator.comparingDouble(DeviceNowStatus::getCpuUseRate).reversed()).limit(4).collect(toList());


            return CopsecResult.success(_list);
        } else {

            List<DeviceNowStatus> _list = deviceList.stream().sorted(Comparator.comparingDouble(DeviceNowStatus::getMemoryUseRate).reversed()).limit(4).collect(toList());

            return CopsecResult.success(_list);
        }
    }


    @Override
    public CopsecResult getDBSyncTaskDetails(ConditionBean conditionBean) {


        Map<String, DBStatisticResultBean> daysMap = getDBSyncTaskRunningDays();
        Map<String, DBStatisticResultBean> todaySum = getSumDatabaseByCondition(conditionBean, true);
        Map<String, DBStatisticResultBean> totalSum = getSumDatabaseByCondition(conditionBean, false);

        totalSum.entrySet().stream().forEach(entry -> {

            DBStatisticResultBean bean = entry.getValue();
            DBStatisticResultBean dayBean = daysMap.get(entry.getKey());
            if (!ObjectUtils.isEmpty(dayBean)) {

                bean.setDays(dayBean.getDays());
            }
            DBStatisticResultBean todayBean = todaySum.get(entry.getKey());
            if (!ObjectUtils.isEmpty(todayBean)) {

                bean.setGatherTotal(todayBean.getGatherToday());
                bean.setStorageToday(todayBean.getStorageToday());
            }
        });

        return CopsecResult.success(totalSum);

    }

    /**
     * 计算运行天数
     */
    private Map<String, DBStatisticResultBean> getDBSyncTaskRunningDays() {

        List<AggregationOperation> operationList = Lists.newArrayList();

        operationList.add(project("taskName", "year", "month", "day", "hour",
                "gatherCount", "storageCount", "updateTime"));
        /**
         * 计算运行天数
         */
        AggregationOperation sortByUpdateTime = sort(Direction.DESC, "updateTime", "taskName");
        operationList.add(sortByUpdateTime);
        AggregationOperation groupByUpdateTime = group("taskName")
                .max("updateTime").as("lastTime")
                .min("updateTime").as("oldTime").addToSet("taskName").as("taskName");
        operationList.add(groupByUpdateTime);
        AggregationOperation projection = project("taskName", "lastTime", "oldTime");
        operationList.add(projection);
        List<DBStatisticResultBean> daysResult = dbStatusRepository.countDatabaseStatistic(operationList);
        Map<String, DBStatisticResultBean> map = Maps.newConcurrentMap();
        daysResult.stream().forEach(item -> {

            if (!map.containsKey(item.getTaskName())) {

                Date last = item.getLastTime();
                Date old = item.getOldTime();
                item.setDays(FormatUtils.getFormatTimeMillis(last.getTime() - old.getTime()));
                map.put(item.getTaskName(), item);
            }
        });
        return map;
    }

    /**
     * 计算总量
     *
     * @param conditionBean
     * @param countToday
     * @return
     */
    private Map<String, DBStatisticResultBean> getSumDatabaseByCondition(ConditionBean conditionBean, boolean countToday) {

        List<AggregationOperation> operationList = Lists.newArrayList();

        operationList.add(project("taskName", "year", "month", "day", "hour",
                "gatherCount", "storageCount", "updateTime"));
        /**
         * 计算总量
         */
        if (countToday) {

            Criteria criteria = new Criteria();
            operationList.add(match(criteria.and("updateTime").
                    gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                    .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));
        }
        operationList.add(group("taskName")
                .sum("gatherCount").as("gatherCount")
                .sum("storageCount").as("storageCount")
                .addToSet("taskName").as("taskName"));

        List<DBStatisticResultBean> result = dbStatusRepository.countDatabaseStatistic(operationList);
        Map<String, DBStatisticResultBean> map = Maps.newConcurrentMap();
        result.stream().forEach(item -> {

            if (!map.containsKey(item.getTaskName())) {

                if (countToday) {

                    item.setGatherTotal(item.getGatherCount());
                    item.setStorageToday(item.getStorageCount());
                } else {

                    item.setGatherTotal(item.getGatherCount());
                    item.setStorageTotal(item.getStorageCount());
                }
                map.put(item.getTaskName(), item);
            }
        });
        return map;
    }

    @Override
    public List<DBSumResultBean> getDBDataForToday(ConditionBean conditionBean) {

        DBSyncHistoryStatus total = getDBDataTotalForToday(conditionBean);

        if (ObjectUtils.isEmpty(total)) {

            return Lists.newArrayList();
        }
        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);

        operationList.add(project("year", "month", "day"
                , "gatherInsertCount", "gatherDeleteCount", "gatherUpdateCount",
                "storageInsertCount", "storageDeleteCount", "storageUpdateCount", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

        operationList.add(group("year", "month", "day").
                sum("gatherInsertCount").as("gatherInsertCount").
                sum("gatherDeleteCount").as("gatherDeleteCount").
                sum("gatherUpdateCount").as("gatherUpdateCount").
                sum("storageInsertCount").as("storageInsertCount").
                sum("storageDeleteCount").as("storageDeleteCount").
                sum("storageUpdateCount").as("storageUpdateCount"));

        List<DBSyncHistoryStatus> result = dbStatusRepository.getDbData(operationList);
        if (result.size() == 1) {

            DBSyncHistoryStatus today = result.get(0);
            List<DBSumResultBean> sumResult = Lists.newArrayList();

            if (conditionBean.getReference().equals("gather")) {

                if (today.getGatherInsertCount() > 0) {

                    DBSumResultBean current = new DBSumResultBean();
                    current.setType("采集增加");
                    current.setSum(today.getGatherInsertCount());
                    current.setPercent(FormatUtils.formatePecent(
                            Double.valueOf(today.getGatherInsertCount()) * 100 / Double.valueOf(total.getGatherCount())));

                    sumResult.add(current);
                } else {

                    sumResult.add(new DBSumResultBean("采集增加"));
                }
                if (today.getGatherUpdateCount() > 0) {

                    DBSumResultBean current = new DBSumResultBean();
                    current.setType("采集更新");
                    current.setSum(today.getGatherUpdateCount());
                    current.setPercent(FormatUtils.formatePecent(
                            Double.valueOf(today.getGatherUpdateCount()) * 100 / Double.valueOf(total.getGatherCount())));
                    sumResult.add(current);

                } else {

                    sumResult.add(new DBSumResultBean("采集更新"));
                }
                if (today.getGatherDeleteCount() > 0) {

                    DBSumResultBean current = new DBSumResultBean();
                    current.setType("采集删除");
                    current.setSum(today.getGatherDeleteCount());
                    current.setPercent(FormatUtils.formatePecent(
                            Double.valueOf(today.getGatherDeleteCount()) * 100 / Double.valueOf(total.getGatherCount())));
                    sumResult.add(current);
                } else {

                    sumResult.add(new DBSumResultBean("采集删除"));
                }

            } else {

                if (today.getStorageInsertCount() > 0) {

                    DBSumResultBean current = new DBSumResultBean();
                    current.setType("上传增加");
                    current.setSum(today.getStorageInsertCount());
                    current.setPercent(
                            Double.valueOf(today.getStorageInsertCount()) * 100 / Double.valueOf(total.getStorageCount()));
                    sumResult.add(current);
                } else {

                    sumResult.add(new DBSumResultBean("上传增加"));
                }
                if (today.getStorageUpdateCount() > 0) {

                    DBSumResultBean current = new DBSumResultBean();
                    current.setType("上传更新");
                    current.setSum(today.getStorageUpdateCount());
                    current.setPercent(FormatUtils.formatePecent(
                            Double.valueOf(today.getStorageUpdateCount()) * 100 / Double.valueOf(total.getStorageCount())));
                    sumResult.add(current);
                } else {

                    sumResult.add(new DBSumResultBean("上传更新"));
                }
                if (today.getStorageDeleteCount() > 0) {

                    DBSumResultBean current = new DBSumResultBean();
                    current.setType("上传删除");
                    current.setSum(today.getStorageDeleteCount());
                    current.setPercent(FormatUtils.formatePecent(
                            Double.valueOf(today.getStorageDeleteCount()) * 100 / Double.valueOf(total.getStorageCount())));
                    sumResult.add(current);
                } else {

                    sumResult.add(new DBSumResultBean("上传删除"));
                }
            }

            Collections.sort(sumResult, new Comparator<DBSumResultBean>() {
                @Override
                public int compare(DBSumResultBean o1, DBSumResultBean o2) {

                    if (ObjectUtils.isEmpty(o1) && ObjectUtils.isEmpty(o2)) {

                        if (o1.getPercent() < o2.getPercent()) {

                            return -1;
                        } else if (o1.getPercent() == o2.getPercent()) {

                            return 0;
                        } else if (o1.getPercent() > o2.getPercent()) {

                            return 1;
                        }
                    }
                    return 0;
                }
            });
            List<DBSumResultBean> sum = Lists.newArrayList();
            for (int i = 0; i < sumResult.size(); i++) {

                DBSumResultBean bean = sumResult.get(i);
                bean.setBackground(DeviceIdUtils.getColor(i));
                sum.add(bean);
            }
            return sum;
        }
        return Lists.newArrayList();
    }

    @Override
    public DBSyncHistoryStatus getDBDataTotalForToday(ConditionBean conditionBean) {

        List<AggregationOperation> operationList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.where("deviceId").exists(true);

        operationList.add(project("deviceId", "year", "month", "day", "hour", "taskName", "gatherCount", "storageCount", "updateTime"));

        operationList.add(match(criteria.and("updateTime").
                gte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getStart() + " 00:01:01"))
                .lte(FormatUtils.parseDateByPattern("yyyy-MM-dd HH:mm:ss", conditionBean.getEnd() + " 23:59:59"))));

        operationList.add(group("year", "month", "day").
                sum("gatherCount").as("gatherCount")
                .sum("storageCount").as("storageCount"));

        List<DBSyncHistoryStatus> result = dbStatusRepository.getDbData(operationList);

        if (result.size() == 1) {

            return result.get(0);
        }
        return null;
    }

    @Override
    public CopsecResult getDBTaskPieDateForToday(ConditionBean conditionBean) {

        List<DBSumResultBean> sumResult = getDBDataForToday(conditionBean);
        PieData pieData = new PieData();
        sumResult.stream().forEach(item -> {

            pieData.getLabels().add(item.getType());
            if (pieData.getDatasets().size() == 0) {

                PieDataSets dataSets = new PieDataSets();
                pieData.getDatasets().add(dataSets);
            }
            pieData.getDatasets().get(0).getData().add(item.getPercent());
            pieData.getDatasets().get(0).getBackgroundColor().add(DeviceIdUtils.getColor(pieData.getDatasets().get(0).getData().size()));
        });

        return CopsecResult.success(pieData);
    }
}
