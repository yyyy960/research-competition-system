import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/DashboardView.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'announcement/:id',
        name: 'AnnouncementDetail',
        component: () => import('../views/announcement/AnnouncementDetail.vue'),
        meta: { title: '公告详情' }
      },
      {
        path: 'competition',
        name: 'Competition',
        component: () => import('../views/competition/CompetitionList.vue'),
        meta: { title: '学科竞赛' }
      },
      {
        path: 'competition/create',
        name: 'CompetitionCreate',
        component: () => import('../views/competition/CompetitionForm.vue'),
        meta: { title: '提交竞赛成果' }
      },
      {
        path: 'competition/:id',
        name: 'CompetitionDetail',
        component: () => import('../views/competition/CompetitionDetail.vue'),
        meta: { title: '竞赛详情' }
      },
      {
        path: 'innovation',
        name: 'Innovation',
        component: () => import('../views/innovation/InnovationList.vue'),
        meta: { title: '大创项目' }
      },
      {
        path: 'innovation/create',
        name: 'InnovationCreate',
        component: () => import('../views/innovation/InnovationForm.vue'),
        meta: { title: '提交大创项目' }
      },
      {
        path: 'innovation/:id',
        name: 'InnovationDetail',
        component: () => import('../views/innovation/InnovationDetail.vue'),
        meta: { title: '项目详情' }
      },
      {
        path: 'copyright',
        name: 'Copyright',
        component: () => import('../views/copyright/CopyrightList.vue'),
        meta: { title: '软件著作权' }
      },
      {
        path: 'copyright/create',
        name: 'CopyrightCreate',
        component: () => import('../views/copyright/CopyrightForm.vue'),
        meta: { title: '提交软著' }
      },
      {
        path: 'copyright/:id',
        name: 'CopyrightDetail',
        component: () => import('../views/copyright/CopyrightDetail.vue'),
        meta: { title: '软著详情' }
      },
      {
        path: 'paper',
        name: 'Paper',
        component: () => import('../views/paper/PaperList.vue'),
        meta: { title: '学术论文' }
      },
      {
        path: 'paper/create',
        name: 'PaperCreate',
        component: () => import('../views/paper/PaperForm.vue'),
        meta: { title: '提交论文' }
      },
      {
        path: 'paper/:id',
        name: 'PaperDetail',
        component: () => import('../views/paper/PaperDetail.vue'),
        meta: { title: '论文详情' }
      },
      {
        path: 'ccf',
        name: 'CcfVenue',
        component: () => import('../views/ccf/CcfVenueList.vue'),
        meta: { title: 'CCF目录' }
      },
      {
        path: 'review',
        name: 'Review',
        component: () => import('../views/review/ReviewList.vue'),
        meta: { title: '审核管理', roles: ['ADMIN', 'SECRETARY', 'LEADER'] }
      },
      {
        path: 'tasks',
        name: 'TaskCenter',
        component: () => import('../views/task/TaskCenter.vue'),
        meta: { title: '任务中心', roles: ['ADMIN', 'SECRETARY', 'LEADER'] }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('../views/profile/Notifications.vue'),
        meta: { title: '通知消息' }
      },
      {
        path: 'file',
        name: 'File',
        component: () => import('../views/file/FileManage.vue'),
        meta: { title: '文件管理' }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('../views/system/user/UserManage.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'system/log',
        name: 'SystemLog',
        component: () => import('../views/system/log/SystemLog.vue'),
        meta: { title: '系统日志', roles: ['ADMIN'] }
      },
      {
        path: 'system/announcement',
        name: 'AnnouncementManage',
        component: () => import('../views/system/announcement/AnnouncementManage.vue'),
        meta: { title: '系统公告', roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/profile/ProfileView.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'profile/achievements',
        name: 'PersonalAchievement',
        component: () => import('../views/profile/PersonalAchievement.vue'),
        meta: { title: '成果概览' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 科研竞赛管理系统` : '科研竞赛管理系统'

  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (to.meta.roles && !to.meta.roles.includes(userInfo.role)) {
    next('/dashboard')
    return
  }

  next()
})

export default router
