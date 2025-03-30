import { createRouter, createWebHistory } from 'vue-router';
import LoginRegisterView from '@/views/LoginRegisterView.vue';  // Bruk alias @
import EmailVerification from '@/views/EmailVerification.vue';
import VerifyInfo from '@/views/VerifyInfo.vue';
import Dashboard from "@/views/Dashboard.vue";

const routes = [
  {
    path: '/',
    name: 'Home',
    component: LoginRegisterView,
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginRegisterView,
  },
  {
    path: '/register',
    name: 'Register',
    component: LoginRegisterView,
  },
  {
    path: '/verify',
    name: 'EmailVerification',
    component: EmailVerification,
  },
  {
    path: '/verify-info',
    name: 'VerifyInfo',
    component: VerifyInfo,
  },
  // Legg gjerne til en dashboard- eller beskyttet side for innloggede brukere
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
