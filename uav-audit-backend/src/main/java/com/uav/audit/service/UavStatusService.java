package com.uav.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.audit.dto.UavStatusDTO;
import com.uav.audit.entity.UavStatus;
import com.uav.audit.mapper.UavStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UavStatusService extends ServiceImpl<UavStatusMapper, UavStatus> {

    /**
     * 更新或创建无人机状态
     */
    public UavStatus updateStatus(UavStatusDTO dto) {
        UavStatus status = this.getOne(new LambdaQueryWrapper<UavStatus>()
                .eq(UavStatus::getUavId, dto.getUavId()));

        if (status == null) {
            status = new UavStatus();
            status.setUavId(dto.getUavId());
            status.setCreateTime(LocalDateTime.now());
        }

        status.setUavName(dto.getUavName());
        status.setLatitude(dto.getLatitude());
        status.setLongitude(dto.getLongitude());
        status.setAltitude(dto.getAltitude());
        status.setSpeed(dto.getSpeed());
        status.setHeading(dto.getHeading());
        status.setBattery(dto.getBattery());
        status.setSignalStrength(dto.getSignalStrength());
        status.setStatus(dto.getStatus() != null ? dto.getStatus() : "normal");
        status.setIsOnline(1);
        status.setLastHeartbeat(LocalDateTime.now());

        this.saveOrUpdate(status);
        return status;
    }

    /**
     * 处理心跳
     */
    public void processHeartbeat(String uavId) {
        UavStatus status = this.getOne(new LambdaQueryWrapper<UavStatus>()
                .eq(UavStatus::getUavId, uavId));

        if (status != null) {
            status.setIsOnline(1);
            status.setLastHeartbeat(LocalDateTime.now());
            this.updateById(status);
        }
    }

    /**
     * 获取无人机列表（分页）
     */
    public IPage<UavStatus> getUavList(Integer pageNum, Integer pageSize) {
        Page<UavStatus> page = new Page<>(pageNum, pageSize);
        return this.page(page, new LambdaQueryWrapper<UavStatus>()
                .orderByDesc(UavStatus::getLastHeartbeat));
    }

    /**
     * 获取所有在线无人机
     */
    public List<UavStatus> getOnlineUavs() {
        return this.list(new LambdaQueryWrapper<UavStatus>()
                .eq(UavStatus::getIsOnline, 1));
    }

    /**
     * 检查信号丢失（超过指定秒数无心跳）
     */
    public List<UavStatus> checkSignalLost(int seconds) {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(seconds);
        return this.list(new LambdaQueryWrapper<UavStatus>()
                .eq(UavStatus::getIsOnline, 1)
                .lt(UavStatus::getLastHeartbeat, threshold));
    }
}
