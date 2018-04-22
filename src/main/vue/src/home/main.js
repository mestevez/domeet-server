// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import Home from './Home'
import 'vuetify/dist/vuetify.min.css'

Vue.config.productionTip = false
Vue.use(Vuetify)

/* eslint-disable no-new */
new Vue({
  el: '#home',
  components: { Home },
  template: '<Home/>'
})
