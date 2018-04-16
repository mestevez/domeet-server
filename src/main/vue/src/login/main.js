// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import Login from './Login'
import Signup from './Signup'
import 'vuetify/dist/vuetify.min.css'

Vue.config.productionTip = false
Vue.use(Vuetify)

/* eslint-disable no-new */
new Vue({
  el: '#login',
  components: { Login },
  template: '<Login/>'
})
new Vue({
  el: '#signup',
  components: { Signup },
  template: '<Signup/>'
})
