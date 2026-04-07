# 无人机实时数据接入与告警检测模块

## 1. 需求概述

为审计平台增加无人机实时数据接入功能，实现：
- 无人机状态数据接收与存储
- 实时告警检测（禁飞区、飞行高度、信号丢失等）
- 与现有审计系统集成

## 2. 技术架构

```
无人机 ──MQTT/HTTP──> 数据接收API ──> 告警检测引擎 ──> 审计日志 + 告警记录
```

## 3. 数据模型

### 3.1 无人机实时状态表 (uav_status)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| uav_id | VARCHAR(50) | 无人机编号 |
| uav_name | VARCHAR(100) | 无人机名称 |
| latitude | DOUBLE | 纬度 |
| longitude | DOUBLE | 经度 |
| altitude | DOUBLE | 飞行高度(m) |
| speed | DOUBLE | 飞行速度(km/h) |
| heading | DOUBLE | 航向角度 |
| battery | INT | 电量(0-100) |
| signal_strength | INT | 信号强度 |
| status | VARCHAR(20) | 状态(normal/warning/error) |
| is_online | TINYINT | 在线状态 |
| last_heartbeat | DATETIME | 最后心跳时间 |
| create_time | DATETIME | 创建时间 |

### 3.2 告警规则表 (alert_rule)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| rule_name | VARCHAR(100) | 规则名称 |
| rule_type | VARCHAR(50) | 规则类型 |
| threshold_value | DOUBLE | 阈值 |
| is_enabled | TINYINT | 是否启用 |
| severity | VARCHAR(20) | 严重程度 |

## 4. 功能模块

### 4.1 数据接收API

- `POST /api/uav/status` - 接收无人机状态数据
- `POST /api/uav/heartbeat` - 接收心跳数据
- `GET /api/uav/list` - 获取无人机列表
- `GET /api/uav/{id}` - 获取无人机详情

### 4.2 告警检测引擎

支持的告警规则：
1. **禁飞区入侵** - 无人机进入指定区域
2. **高度异常** - 超过/低于安全高度限制
3. **低电量告警** - 电池低于阈值
4. **信号丢失** - 长时间无心跳
5. **超速告警** - 超过安全速度

### 4.3 与审计系统集成

- 自动记录异常事件到审计日志
- 生成安全告警到告警表

## 5. 实施计划

1. 创建数据库表
2. 创建UAV状态实体类
3. 创建UAV控制器和服务
4. 实现告警检测逻辑
5. 集成到审计系统
6. 前端展示无人机列表
