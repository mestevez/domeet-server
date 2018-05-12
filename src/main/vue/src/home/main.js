// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import Ajax from '@/util/Ajax'
import Home from './Home'
import 'vuetify/dist/vuetify.min.css'
import WebSocket from '@/util/WebSocket'

Vue.config.productionTip = false
Vue.use(Vuetify)
Vue.use(Ajax)
Vue.use(WebSocket)

/* eslint-disable no-new */
new Vue({
  el: '#home',
  components: { Home },
  template: '<Home/>'
})
