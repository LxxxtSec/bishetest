export const operationTypeMeta = {
  LOGIN: { label: '用户登录', tag: 'success' },
  LOGOUT: { label: '用户登出', tag: 'info' },
  USER: { label: '用户管理', tag: 'primary' },
  USER_QUERY: { label: '查询用户', tag: 'info' },
  USER_CREATE: { label: '创建用户', tag: 'primary' },
  USER_UPDATE: { label: '更新用户', tag: 'warning' },
  USER_DELETE: { label: '删除用户', tag: 'danger' },
  QUERY: { label: '查询操作', tag: 'info' },
  EXPORT: { label: '数据导出', tag: 'warning' },
  ALERT_QUERY: { label: '查询告警', tag: 'info' },
  ALERT_RESOLVE: { label: '处理告警', tag: 'warning' },
  UAV_QUERY: { label: '查询无人机', tag: 'info' },
  UAV_STATUS: { label: '更新无人机状态', tag: 'warning' }
}

export function getOperationTypeLabel(type) {
  return operationTypeMeta[type]?.label || type || ''
}

export function getOperationTagType(type) {
  return operationTypeMeta[type]?.tag || 'info'
}
