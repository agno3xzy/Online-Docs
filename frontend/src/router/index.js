import Vue from 'vue'
import Router from 'vue-router'
// import index from '@/components/index'
// import register from '@/components/register'
// import password_manage from '@/components/password-manage'
// import document_manage from '@/components/document-manage'
// import document_explore from '@/components/document-explore'
// import document_edit from '@/components/document-edit'
// import document_history from '@/components/document-history'
// import inviteConfirm from '@/components/invite-confirm'
// import notfound from '@/components/notfound'
Vue.use(Router)

const routes = [
    {
      path: '/',
      name: 'index',
      component: resolve => require(['../components/index.vue'], resolve)
    },
    {
      path: '/register',
      name: 'regsiter',
      component: resolve => require(['../components/register.vue'], resolve)
    },
    {
      path: '/password-manage',
      name: 'password-manage',
      meta: {
        requireAuth: true
      },
      component: resolve => require(['../components/password-manage.vue'], resolve)
    },
    {
      path: '/document-manage',
      name: 'document-manage',
      meta: {
        requireAuth: true
      },
      component: resolve => require(['../components/document-manage.vue'], resolve)
    },
    {
      path: '/document-explore',
      name: 'document-explore',
      meta: {
        requireAuth: true
      },
      component: resolve => require(['../components/document-explore.vue'], resolve)
    },
    {
      path: '/document-edit',
      name: 'document-edit',
      meta: {
        requireAuth: true
      },
      component: resolve => require(['../components/document-edit.vue'], resolve)
    },
    {
      path: '/document-history',
      name: 'document-history',
      meta: {
        requireAuth: true
      },
      component: resolve => require(['../components/document-history.vue'], resolve)
    },
    {
      path: '/inviteConfirm/:docId/:auth',
      component: resolve => require(['../components/invite-confirm.vue'], resolve)
    },
    {
      path: '/404',
      name: '404',
      component: resolve => require(['../components/notfound.vue'], resolve)
    },
    {
      path: '*',
      redirect: '/404'
    }
]

const router = new Router({
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(res => res.meta.requireAuth)) {
    if(window.sessionStorage.username)
    { 
      next()
    } else
    {
      next('/')
    }
  } else
  {
    next()
  }
})

export default router