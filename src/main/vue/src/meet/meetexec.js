// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import axios from 'axios'
import VueAxios from 'vue-axios'
import MeetExec from './MeetExec.vue'
import ModelAjax from '@/util/ModelAjax'
import WebSocket from '@/util/WebSocket'
import 'vuetify/dist/vuetify.min.css'

Vue.config.productionTip = false
Vue.use(Vuetify)
Vue.use(VueAxios, axios)
Vue.use(ModelAjax)
Vue.use(WebSocket)

/* eslint-disable no-new */
new Vue({
  el: '#meetexec',
  components: { MeetExec },
  template: '<MeetExec/>'
})
