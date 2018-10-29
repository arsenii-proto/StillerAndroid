import Vue from "vue";
import App from "./App.vue";
import router from "./router/index";
import store from "./store/index";
import StillerService from "./services/StillerService";
import TransService from "./services/TransService";

Vue.config.productionTip = false;

Vue.use(StillerService);
Vue.use(TransService);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");

window.Vue = Vue;
window.Store = store;
