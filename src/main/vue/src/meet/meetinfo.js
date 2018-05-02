// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import axios from 'axios'
import VueAxios from 'vue-axios'
import MeetInfo from './MeetInfo.vue'
import 'vuetify/dist/vuetify.min.css'

Vue.config.productionTip = false
Vue.use(Vuetify)
Vue.use(VueAxios, axios)

/* eslint-disable no-new */
new Vue({
  el: '#meetinfo',
  components: { MeetInfo },
  template: '<MeetInfo/>'
})
